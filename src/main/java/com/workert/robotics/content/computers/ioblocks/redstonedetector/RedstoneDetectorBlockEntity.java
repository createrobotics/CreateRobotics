package com.workert.robotics.content.computers.ioblocks.redstonedetector;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.base.roboscript.RoboScriptClass;
import com.workert.robotics.base.roboscript.RoboScriptNativeMethod;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.base.roboscript.util.RoboScriptObjectConversions;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneDetectorBlockEntity extends SyncedTileEntity implements IOBlockEntity {

	public static RoboScriptClass roboScriptBlockClass = makeClass();
	int redstoneLevel = 0;

	private String signalName = "";
	private BlockPos targetPos = this.getBlockPos();
	public RoboScriptObject roboScriptBlockInstance = this.makeInstance();

	public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(BlockEntityRegistry.REDSTONE_DETECTOR.get(), blockPos, blockState);
	}

	public void setRedstoneLevel(int redstoneLevel) {
		this.redstoneLevel = redstoneLevel;
		this.roboScriptBlockInstance.fields.put("power", redstoneLevel);
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
		if (this.getLevel().getBlockEntity(this.targetPos) instanceof ComputerBlockEntity e) {
			System.out.println("IS COMPOOTER");
			e.connectedBlocks.put(this.signalName, this.getBlockEntityPos());
			System.out.println(e.connectedBlocks);
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
	public SyncedTileEntity getBlockEntity() {
		return this;
	}

	@Override
	public RoboScriptObject getRoboScriptObject() {
		return this.roboScriptBlockInstance;
	}


	private static RoboScriptClass makeClass() {
		RoboScriptClass clazz = new RoboScriptClass();
		RoboScriptNativeMethod getPower = new RoboScriptNativeMethod((byte) 0);
		getPower.function = () ->
				RoboScriptObjectConversions.prepareForRoboScriptUse(
						((RedstoneDetectorBlockEntity) getPower.instance).redstoneLevel);

		clazz.functions.put("getPower", getPower);
		return clazz;
	}

	private RoboScriptObject makeInstance() {
		RoboScriptObject object = new RoboScriptObject(roboScriptBlockClass);
		object.fields.put("power", 0);
		return object;
	}
}
