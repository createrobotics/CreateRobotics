package com.workert.robotics.content.computers.ioblocks.redstonedetector;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.base.roboscript.*;
import com.workert.robotics.base.roboscript.util.RoboScriptObjectConversions;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

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
		if (redstoneLevel != this.redstoneLevel && this.getConnectedComputer() != null) {
			this.getConnectedComputer().interpretSignal(
					((RoboScriptSignal) (this.roboScriptBlockInstance.fields.get("powerChanged").value)).callable,
					new Object[] {RoboScriptObjectConversions.prepareForRoboScriptUse(redstoneLevel)});
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

	@Nullable
	public ComputerBlockEntity getConnectedComputer() {
		return this.getLevel().getExistingBlockEntity(this.targetPos) instanceof ComputerBlockEntity e ? e : null;
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
		getPower.function = (vm, fun) -> getBlockEntityFromMethod((RoboScriptNativeMethod) fun).redstoneLevel;

		clazz.functions.put("getPower", getPower);
		return clazz;
	}

	private RoboScriptObject makeInstance() {
		RoboScriptObject object = new RoboScriptObject(roboScriptBlockClass);
		object.fields.put("", new RoboScriptField(this, true));
		object.fields.put("powerChanged", new RoboScriptField(new RoboScriptSignal(), true));
		return object;
	}

	private static RedstoneDetectorBlockEntity getBlockEntityFromMethod(RoboScriptNativeMethod method) {
		return ((RedstoneDetectorBlockEntity) ((RoboScriptObject) method.getParentClassInstance()).fields.get(
				"").value);
	}
}
