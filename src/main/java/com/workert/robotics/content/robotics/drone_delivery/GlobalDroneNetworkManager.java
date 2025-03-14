package com.workert.robotics.content.robotics.drone_delivery;

import com.workert.robotics.content.robotics.drone_delivery.drone_port.DronePortBlockEntity;
import com.workert.robotics.content.robotics.drone_delivery.pathing.Cell;
import com.workert.robotics.content.robotics.drone_delivery.pathing.Pathfinder;
import com.workert.robotics.content.robotics.drone_delivery.pathing.SimpleWorldProvider;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GlobalDroneNetworkManager {

	public Map<String, Map<BlockPos, String>> dronePorts = new ConcurrentHashMap<>();
	public Map<String, Map<Couple<BlockPos>, List<BlockPos>>> savedPaths = new ConcurrentHashMap<>();

	private DroneNetworkSavedData savedData;

	public GlobalDroneNetworkManager() {
	}

	public void levelLoaded(LevelAccessor level) {
		MinecraftServer server = level.getServer();
		if (server == null || server.overworld() != level)
			return;
		this.dronePorts = new HashMap<>();
		this.savedData = null;
		this.loadLogisticsData(server);
	}

	public void portAdded(LevelAccessor levelAccessor, GlobalPos pos, String filter) {
		BlockPos blockPos = pos.pos();

		Map<BlockPos, String> portsInThisDimension = this.dronePorts.computeIfAbsent(pos.dimension().toString(), key -> new HashMap<>());

		if (((DronePortBlockEntity) Objects.requireNonNull(levelAccessor.getBlockEntity(blockPos))).acceptsPackages) {
			List<BlockPos> trackedPorts = new ArrayList<>();

			portsInThisDimension.forEach((portBlockPos, portFilter) -> {
				if (!portBlockPos.equals(blockPos)) {
					double distance = blockPos.distToCenterSqr(portBlockPos.getX(), portBlockPos.getY(), portBlockPos.getZ());
					if (distance < 16384) { // TODO Make configurable
						trackedPorts.add(portBlockPos);
					}
				}
			});

			Map<Couple<BlockPos>, List<BlockPos>> pathsInThisDimension = this.savedPaths.computeIfAbsent(
					((Level) levelAccessor).dimension().toString(),
					key -> new HashMap<>());

			trackedPorts.forEach(trackedPortPos -> {
				if (!pathsInThisDimension.containsKey(Couple.create(trackedPortPos, blockPos))) {
					this.recalculatePath((Level) levelAccessor, blockPos, trackedPortPos);
					pathsInThisDimension.put(Couple.create(trackedPortPos, blockPos), null);
					pathsInThisDimension.put(Couple.create(blockPos, trackedPortPos), null);
				}

			});
		}

		portsInThisDimension.put(blockPos, filter);
		this.markDirty();
	}

	public void recalculatePath(Level level, BlockPos blockPos, BlockPos trackedPortPos) {
		if (level.isClientSide())
			return;

		System.out.println("Calculating path from " + blockPos + " to " + trackedPortPos);
		this.savedPaths.computeIfAbsent(level.dimension().toString(),
				key -> new HashMap<>()).remove(Couple.create(blockPos, trackedPortPos));
		Thread thread = new Thread(() -> {
			final SimpleWorldProvider worldProvider = new SimpleWorldProvider();

			addCellsFromWorld(worldProvider, trackedPortPos, blockPos, 16, level); // TODO Make configurable

			Pathfinder pathfinder = new Pathfinder(
					new Cell(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()),
					new Cell(trackedPortPos.getX(), trackedPortPos.getY() + 1, trackedPortPos.getZ()),
					Pathfinder.COMMON_NEIGHBORS,
					worldProvider
			);
			ArrayList<Cell> path = pathfinder.findPath(2048);

			if (path.isEmpty() || path.size() == 1) {
				this.savedPaths.computeIfAbsent(level.dimension().toString(), key -> new HashMap<>())
						.remove(Couple.create(blockPos, trackedPortPos));
				this.savedPaths.computeIfAbsent(level.dimension().toString(), key -> new HashMap<>())
						.remove(Couple.create(trackedPortPos, blockPos));
				return;
			}

			ArrayList<BlockPos> pathList = path.stream().map(cell -> new BlockPos(cell.x, cell.y, cell.z))
					.collect(Collectors.toCollection(ArrayList::new));

			ArrayList<BlockPos> reversePathList = new ArrayList<>(pathList);
			Collections.reverse(reversePathList);

			this.savedPaths.computeIfAbsent(level.dimension().toString(), key -> new HashMap<>())
					.put(Couple.create(blockPos, trackedPortPos), pathList);
			this.savedPaths.computeIfAbsent(level.dimension().toString(), key -> new HashMap<>())
					.put(Couple.create(trackedPortPos, blockPos), reversePathList);
			this.markDirty();
			System.out.println("Finished path from " + blockPos + " to " + trackedPortPos);
		});
		thread.setDaemon(true);
		thread.setPriority(2);
		thread.start();
	}

	private static void addCellsFromWorld(SimpleWorldProvider blockManager, BlockPos trackedPortPos, BlockPos blockPos, int margin, Level level) {
		for (int y = Math.min(blockPos.getY(), trackedPortPos.getY()) - margin; y <= Math.max(blockPos.getY(),
				trackedPortPos.getY()) + margin; y++) {
			for (int x = Math.min(blockPos.getX(), trackedPortPos.getX()) - margin; x <= Math.max(blockPos.getX(),
					trackedPortPos.getX()) + margin; x++) {
				for (int z = Math.min(blockPos.getZ(), trackedPortPos.getZ()) - margin; z <= Math.max(blockPos.getZ(),
						trackedPortPos.getZ()) + margin; z++) {
					if (!level.isEmptyBlock(new BlockPos(x, y, z)) ||
							(
									!(level.getBlockEntity(new BlockPos(x, y - 1, z)) instanceof DronePortBlockEntity) &&
											!level.isEmptyBlock(new BlockPos(x, y - 1, z))
							)
					)
						blockManager.addWall(new Cell(x, y, z));
				}
			}
		}
	}

	public void portRemoved(GlobalPos pos) {
		this.dronePorts.computeIfAbsent(pos.dimension().toString(), key -> new HashMap<>()).remove(pos.pos());
		List<Couple<BlockPos>> toRemove = new ArrayList<>();
		this.savedPaths.computeIfAbsent(pos.dimension().toString(), key -> new HashMap<>()).forEach((couple, path) -> {
			if (couple.either(blockPos -> blockPos.equals(pos.pos())))
				toRemove.add(couple);
		});
		toRemove.forEach(couple -> this.savedPaths.get(pos.dimension().toString()).remove(couple));
		this.markDirty();
	}

	private void loadLogisticsData(MinecraftServer server) {
		if (this.savedData != null)
			return;
		this.savedData = DroneNetworkSavedData.load(server);
		this.dronePorts = this.savedData.getDroneNetwork();
		this.savedPaths = this.savedData.getSavedPaths();
	}

	public void markDirty() {
		if (this.savedData != null)
			this.savedData.setDirty();
	}

}
