package com.workert.robotics.content.robotics.drone_delivery;

import com.simibubi.create.foundation.utility.SavedDataUtil;
import com.workert.robotics.Robotics;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DroneNetworkSavedData extends SavedData {
	private Map<String, Map<BlockPos, String>> droneNetwork = new HashMap<>();
	public Map<String, Map<Couple<BlockPos>, List<BlockPos>>> savedPaths = new HashMap<>();

	@Override
	public CompoundTag save(CompoundTag nbt) {
		GlobalDroneNetworkManager drones = Robotics.DRONE_NETWORK;

		CompoundTag dronePortLevelCompound = new CompoundTag();
		drones.dronePorts.forEach((level, network) -> {
			ListTag dronePorts = new ListTag();
			network.forEach((blockPos, filter) -> {
				CompoundTag dronePort = new CompoundTag();
				dronePort.put("Pos", NbtUtils.writeBlockPos(blockPos));
				dronePort.putString("Filter", filter);
				dronePorts.add(dronePort);
			});
			dronePortLevelCompound.put(level, dronePorts);
		});
		nbt.put("DronePorts", dronePortLevelCompound);

		CompoundTag savedPathLevelCompound = new CompoundTag();
		drones.savedPaths.forEach((level, paths) -> {
			ListTag savedPaths = new ListTag();
			paths.forEach((fromToBlockPos, waypointList) -> {
				if (waypointList != null && !waypointList.isEmpty()) {
					CompoundTag savedPath = new CompoundTag();

					savedPath.put("From", NbtUtils.writeBlockPos(fromToBlockPos.getFirst()));
					savedPath.put("To", NbtUtils.writeBlockPos(fromToBlockPos.getSecond()));

					CompoundTag waypoints = new CompoundTag();
					waypoints.putInt("Length", waypointList.size());
					for (int i = 0; i < waypointList.size(); i++) {
						waypoints.put(String.valueOf(i), NbtUtils.writeBlockPos(waypointList.get(i)));
					}
					savedPath.put("Waypoints", waypoints);

					savedPaths.add(savedPath);
				}
			});
			savedPathLevelCompound.put(level, savedPaths);
		});
		nbt.put("SavedPaths", savedPathLevelCompound);

		return nbt;
	}

	private static DroneNetworkSavedData load(CompoundTag nbt) {
		DroneNetworkSavedData sd = new DroneNetworkSavedData();
		sd.droneNetwork = new HashMap<>();
		sd.savedPaths = new HashMap<>();

		nbt.getCompound("DronePorts").getAllKeys().forEach(key -> {
			Map<BlockPos, String> levelMap = sd.droneNetwork.computeIfAbsent(key, funcKey -> new HashMap<>());
			NBTHelper.iterateCompoundList(nbt.getCompound("DronePorts").getList(key, Tag.TAG_COMPOUND),
					c -> levelMap.put(NbtUtils.readBlockPos(c.getCompound("Pos")), c.getString("Filter")));
		});

		nbt.getCompound("SavedPaths").getAllKeys().forEach(key -> {
			Map<Couple<BlockPos>, List<BlockPos>> pathMap = sd.savedPaths.computeIfAbsent(key, funcKey -> new HashMap<>());
			NBTHelper.iterateCompoundList(nbt.getCompound("SavedPaths").getList(key, Tag.TAG_COMPOUND), c -> {
				List<BlockPos> waypointList = new ArrayList<>();
				CompoundTag waypoints = c.getCompound("Waypoints");
				for (int i = 0; i < waypoints.getInt("Length"); i++) {
					waypointList.add(NbtUtils.readBlockPos(waypoints.getCompound(String.valueOf(i))));
				}
				pathMap.put(Couple.create(NbtUtils.readBlockPos(c.getCompound("From")), NbtUtils.readBlockPos(c.getCompound("To"))),
						waypointList);
			});
			System.out.println(sd.droneNetwork);
			System.out.println(sd.savedPaths);
		});

		return sd;
	}

	@Override
	public void save(File file) {
		SavedDataUtil.saveWithDatOld(this, file);
	}

	public Map<String, Map<BlockPos, String>> getDroneNetwork() {
		return this.droneNetwork;
	}

	public Map<String, Map<Couple<BlockPos>, List<BlockPos>>> getSavedPaths() {
		return this.savedPaths;
	}

	private DroneNetworkSavedData() {
	}

	public static DroneNetworkSavedData load(MinecraftServer server) {
		return server.overworld()
				.getDataStorage()
				.computeIfAbsent(DroneNetworkSavedData::load, DroneNetworkSavedData::new, "robotics_drones");
	}
}
