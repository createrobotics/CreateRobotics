package com.workert.robotics.content.computers.ioblocks.redstonedetector;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.base.roboscript.RoboScriptClass;
import com.workert.robotics.base.roboscript.RoboScriptHelper;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.base.roboscript.RoboScriptSignal;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IORoboScriptBlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneDetectorBlockEntity extends SyncedBlockEntity implements IOBlockEntity {

	public static RoboScriptClass roboScriptBlockClass = IORoboScriptBlockHelper.createClass()
			.addMethod("getPower", 0,
					(vm, fun) -> RoboScriptHelper.numToDouble(
							((RedstoneDetectorBlockEntity) IORoboScriptBlockHelper.getBlockEntityFromMethod(
									fun)).redstoneLevel))
			.build();
	int redstoneLevel = 0;

	private String signalName = "";
	private BlockPos targetPos = this.getBlockPos();

	public RoboScriptSignal powerChanged = new RoboScriptSignal();
	public RoboScriptObject roboScriptBlockInstance = IORoboScriptBlockHelper.createObject(roboScriptBlockClass, this)
			.addField("powerChanged", this.powerChanged, true)
			.build();


	public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(BlockEntityRegistry.REDSTONE_DETECTOR.get(), blockPos, blockState);
	}

	public void setRedstoneLevel(int redstoneLevel) {
		if (redstoneLevel != this.redstoneLevel && this.getConnectedComputer() != null) {
			this.getConnectedComputer().interpretSignal(this.powerChanged.callable,
					new Object[] {RoboScriptHelper.numToDouble(redstoneLevel)});
		}
		this.redstoneLevel = redstoneLevel;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		this.signalName = compound.getString("SignalName");
		this.targetPos = NbtUtils.readBlockPos(compound.getCompound("TargetPosition"));
		this.redstoneLevel = compound.getInt("RedstoneLevel");
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putString("SignalName", this.signalName);
		compound.put("TargetPosition", NbtUtils.writeBlockPos(this.targetPos));
		compound.putInt("RedstoneLevel", this.redstoneLevel);
	}

	@Override
	public String getSignalName() {
		return this.signalName;
	}

	@Override
	public void setSignalName(String signalName) {
		this.signalName = signalName;
		if (this.getConnectedComputer() != null) {
			this.getConnectedComputer().connectedBlocks.put(this.signalName, this.getBlockEntityPos());
		}
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
	public SyncedBlockEntity getBlockEntity() {
		return this;
	}

	@Override
	public RoboScriptObject getRoboScriptObject() {
		return this.roboScriptBlockInstance;
	}

	@Override
	public void resetSignals() {
		this.powerChanged = new RoboScriptSignal();
	}

}
