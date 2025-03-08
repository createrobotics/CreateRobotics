package com.workert.robotics.content.robotics.drone_delivery.drone_port;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import com.simibubi.create.foundation.item.ItemHelper;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.base.registries.BlockRegistry;
import com.workert.robotics.content.robotics.drone_delivery.delivery_drone.DeliveryDroneEntity;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DronePortBlockEntity extends PackagePortBlockEntity {
	public DronePortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.target = new DronePortPackagePortTarget(pos);
		this.setLazyTickRate(4);
	}

	@Override
	protected void onOpenChange(boolean open) {
		if (this.level != null)
			this.level.playSound(null, this.worldPosition, open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE,
					SoundSource.BLOCKS);
	}

	@Override
	public void lazyTick() {
		if (this.level != null && !this.inventory.isEmpty()) {
			ItemStack box = ItemHelper.extract(this.inventory,
					itemStack -> !itemStack.isEmpty() && PackageItem.isPackage(itemStack) && !PackageItem.matchAddress(itemStack,
							this.addressFilter),
					1, true);
			if (!box.isEmpty() && this.target != null && this.level.getEntitiesOfClass(DeliveryDroneEntity.class,
					new AABB(this.getBlockPos()).inflate(0, 1, 0)).isEmpty())
				if (this.target.export(this.level, this.worldPosition, box, false))
					ItemHelper.extract(this.inventory,
							itemStack -> !itemStack.isEmpty() && PackageItem.isPackage(itemStack) && !PackageItem.matchAddress(itemStack,
									this.addressFilter),
							1, false);
		}
		super.lazyTick();
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);

		if (tag.isEmpty()) {
			this.target = null;
		} else {
			BlockPos relativePos = NbtUtils.readBlockPos(tag.getCompound("RelativePos"));
			DronePortPackagePortTarget target = new DronePortPackagePortTarget(relativePos);
			target.readInternal(tag);
			this.target = target;
		}
	}

	private static class DronePortPackagePortTarget extends PackagePortTarget {

		public DronePortPackagePortTarget(BlockPos relativePos) {
			super("DronePort", relativePos);
		}

		@Override
		public boolean export(LevelAccessor levelAccessor, BlockPos portPos, ItemStack box, boolean simulate) {
			if (simulate)
				return true;

			List<BlockPos> validDestinations = new ArrayList<>();

			Robotics.DRONE_NETWORK.dronePorts.computeIfAbsent(((Level) levelAccessor).dimension().toString(), key -> new HashMap<>())
					.forEach((otherPortPos, filter) -> {
						if (!filter.isEmpty() && PackageItem.matchAddress(box, filter) && Robotics.DRONE_NETWORK.savedPaths.computeIfAbsent(
										((Level) levelAccessor).dimension().toString(), key -> new HashMap<>())
								.get(Couple.create(portPos, otherPortPos)) != null) {
							validDestinations.add(otherPortPos);
						}
					});

			if (validDestinations.isEmpty()) {
				return false;
			}


			return levelAccessor.addFreshEntity(DeliveryDroneEntity.fromItemStack(
					(Level) levelAccessor, portPos.getCenter().add(0, 0.8, 0), box, portPos,
					getNearestBlockPos(portPos, validDestinations)));
		}

		private static BlockPos getNearestBlockPos(BlockPos target, List<BlockPos> destinations) {
			if (destinations == null || destinations.isEmpty()) {
				return null;
			}

			BlockPos nearest = null;
			double minDistance = Double.MAX_VALUE;

			for (BlockPos pos : destinations) {
				double distance = target.distToCenterSqr(pos.getX(), pos.getY(), pos.getZ());
				if (distance < minDistance) {
					minDistance = distance;
					nearest = pos;
				}
			}

			return nearest;
		}

		@Override
		public void register(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
			Robotics.DRONE_NETWORK.portAdded(level, GlobalPos.of(Objects.requireNonNull(ppbe.getLevel()).dimension(), portPos),
					ppbe.acceptsPackages ? ppbe.addressFilter : "");
		}

		@Override
		public void deregister(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
			Robotics.DRONE_NETWORK.portRemoved(GlobalPos.of(Objects.requireNonNull(ppbe.getLevel()).dimension(), portPos));
		}

		@Override
		protected void writeInternal(CompoundTag tag) {
		}

		@Override
		protected void readInternal(CompoundTag tag) {
		}

		@Override
		public Vec3 getExactTargetLocation(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
			return ppbe.getBlockPos().getCenter();
		}

		@Override
		public ItemStack getIcon() {
			return BlockRegistry.DRONE_PORT.asStack();
		}

		@Override
		public boolean canSupport(BlockEntity be) {
			return BlockEntityRegistry.DRONE_PORT.is(be);
		}

	}
}
