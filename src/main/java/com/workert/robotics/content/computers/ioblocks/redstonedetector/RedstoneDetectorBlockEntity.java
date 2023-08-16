package com.workert.robotics.content.computers.ioblocks.redstonedetector;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneDetectorBlockEntity extends SyncedTileEntity implements IOBlockEntity {
	private String signalName = "";
	private BlockPos targetPos = this.getBlockPos();

	public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(BlockEntityRegistry.REDSTONE_DETECTOR.get(), blockPos, blockState);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		this.signalName = compound.getString("SignalName");
		this.targetPos = NbtUtils.readBlockPos(compound.getCompound("TargetPosition"));
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putString("SignalName", this.signalName);
		compound.put("TargetPosition", NbtUtils.writeBlockPos(this.targetPos));
	}

	@Override
	public String getSignalName() {
		return this.signalName;
	}

	@Override
	public void setSignalName(String signalName) {
		this.signalName = signalName;
	}

	@Override
	public BlockPos getTargetPos() {
		return this.targetPos;
	}

	@Override
	public BlockPos getBlockEntityPos() {
		return this.getBlockPos();
	}

	@Override
	public SyncedTileEntity getBlockEntity() {
		return this;
	}
}
