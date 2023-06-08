package com.workert.robotics.content.computing.inputs.redstonedetector;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.lists.BlockEntityList;
import com.workert.robotics.content.computing.inputs.IInputBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneDetectorBlockEntity extends SyncedTileEntity implements IInputBlockEntity {
	private String signalName = "";
	private BlockPos targetPos = this.getBlockPos();

	public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(BlockEntityList.REDSTONE_DETECTOR.get(), blockPos, blockState);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		this.signalName = compound.getString("SignalName");
		this.targetPos = new BlockPos(compound.getInt("TX"), compound.getInt("TY"), compound.getInt("TZ"));
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putString("SignalName", this.signalName);
		compound.putInt("TX", this.getTargetPos().getX());
		compound.putInt("TY", this.getTargetPos().getY());
		compound.putInt("TZ", this.getTargetPos().getZ());

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
	public void setTargetPos(BlockPos targetPos) {
		this.targetPos = targetPos;
	}

	@Override
	public SyncedTileEntity getBlockEntity() {
		return this;
	}
}
