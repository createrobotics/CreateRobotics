package com.workert.robotics.content.computers.ioblocks.redstonepulser;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.workert.robotics.base.roboscript.RoboScriptClass;
import com.workert.robotics.base.roboscript.RoboScriptHelper;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IORoboScriptBlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RedstonePulserBlockEntity extends SmartTileEntity implements IOBlockEntity {


	public static RoboScriptClass roboScriptBlockClass = IORoboScriptBlockHelper.createClass()
			.addMethod("pulse", 3, (vm, fun) -> {
				int ticks = RoboScriptHelper.doubleToInt(RoboScriptHelper.asPositiveWholeDouble(vm.popStack()));
				int endLevel = RoboScriptHelper.doubleToInt(RoboScriptHelper.asPositiveWholeDouble(vm.popStack()));
				int startLevel = RoboScriptHelper.doubleToInt(RoboScriptHelper.asPositiveWholeDouble(vm.popStack()));
				((RedstonePulserBlockEntity) IORoboScriptBlockHelper.getBlockEntityFromMethod(fun)).pulse(startLevel,
						endLevel, ticks);
				return null;
			})
			.addMethod("getPower", 0, (vm, fun) -> RoboScriptHelper.numToDouble(
					((RedstonePulserBlockEntity) IORoboScriptBlockHelper.getBlockEntityFromMethod(fun)).redstoneLevel))
			.addMethod("getRemainingTicks", 0, (vm, fun) -> RoboScriptHelper.numToDouble(
					((RedstonePulserBlockEntity) IORoboScriptBlockHelper.getBlockEntityFromMethod(
							fun)).ticksLeftForPulse))
			.addMethod("getEndPower", 0, (vm, fun) -> RoboScriptHelper.numToDouble(
					((RedstonePulserBlockEntity) IORoboScriptBlockHelper.getBlockEntityFromMethod(
							fun)).nextRedstoneLevel))
			.build();

	public RoboScriptObject roboScriptBlockInstance = IORoboScriptBlockHelper.createObject(roboScriptBlockClass, this)
			.build();

	int redstoneLevel;
	int nextRedstoneLevel;
	int ticksLeftForPulse;

	private String signalName = "";
	private BlockPos targetPos = this.getBlockPos();


	public RedstonePulserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {

	}


	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putString("SignalName", this.signalName);
		tag.put("TargetPosition", NbtUtils.writeBlockPos(this.targetPos));
		tag.putInt("RedstoneLevel", this.redstoneLevel);
		tag.putInt("NextRedstoneLevel", this.nextRedstoneLevel);
		tag.putInt("Ticks", this.ticksLeftForPulse);
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		this.signalName = tag.getString("SignalName");
		this.targetPos = NbtUtils.readBlockPos(tag.getCompound("TargetPosition"));
		this.redstoneLevel = tag.getInt("RedstoneLevel");
		this.nextRedstoneLevel = tag.getInt("NextRedstoneLevel");
		this.ticksLeftForPulse = tag.getInt("Ticks");
	}


	@Override
	public void tick() {
		super.tick();
		if (this.ticksLeftForPulse < 0) this.ticksLeftForPulse = 0;
		if (this.ticksLeftForPulse != 0 && this.redstoneLevel != this.nextRedstoneLevel) {
			this.ticksLeftForPulse--;
			if (this.ticksLeftForPulse == 0) {
				this.redstoneLevel = this.nextRedstoneLevel;
				this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 0);
			}
		}
	}

	public void pulse(int startLevel, int endLevel, int ticks) {
		if (startLevel == endLevel) {
			this.redstoneLevel = this.nextRedstoneLevel = startLevel;
			this.ticksLeftForPulse = 0;
			this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 0);
			return;
		}
		this.redstoneLevel = startLevel;
		this.nextRedstoneLevel = endLevel;
		this.ticksLeftForPulse = ticks;
		this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 0);
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
	public SyncedTileEntity getBlockEntity() {
		return this;
	}

	@Override
	public RoboScriptObject getRoboScriptObject() {
		return this.roboScriptBlockInstance;
	}

	@Override
	public void resetSignals() {

	}
}
