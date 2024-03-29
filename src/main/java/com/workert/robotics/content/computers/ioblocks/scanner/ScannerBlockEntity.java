package com.workert.robotics.content.computers.ioblocks.scanner;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
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

import java.util.List;

public class ScannerBlockEntity extends KineticBlockEntity implements ScannerBehaviour.ScanningBehaviorSpecifics, IOBlockEntity {
	public ScannerBehaviour processingBehaviour;
	private BlockPos targetPos = this.getBlockPos();
	private String signalName = "";

	public static RoboScriptClass roboScriptBlockClass = IORoboScriptBlockHelper.createClass()
			.build();

	public RoboScriptSignal scan = new RoboScriptSignal();

	public RoboScriptObject roboScriptBlockInstance = IORoboScriptBlockHelper.createObject(roboScriptBlockClass, this)
			.addField("scan", this.scan, true)
			.build();

	public ScannerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.SCANNER.get(), pos, state);
	}


	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		this.signalName = compound.getString("SignalName");
		this.targetPos = NbtUtils.readBlockPos(compound.getCompound("TargetPosition"));
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("SignalName", this.signalName);
		compound.put("TargetPosition", NbtUtils.writeBlockPos(this.targetPos));
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		this.processingBehaviour = new ScannerBehaviour(this);
		behaviours.add(this.processingBehaviour);
	}


	@Override
	public boolean scanOnBelt(TransportedItemStack itemStack) {
		if (this.getConnectedComputer() != null) {
			this.getConnectedComputer().interpretSignal(this.scan.callable,
					new Object[] {RoboScriptHelper.itemStackToRoboScriptList(itemStack.stack)});
		}
		return true;
	}

	@Override
	public float getKineticSpeed() {
		return this.getSpeed();
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
		this.scan = new RoboScriptSignal();
	}
}
