package com.workert.robotics.content.computers.ioblocks.scanner;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.base.roboscript.util.RoboScriptObjectConversions;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ScannerBlockEntity extends KineticTileEntity implements ScannerBehaviour.ScanningBehaviorSpecifics, IOBlockEntity {
	public ScannerBehaviour processingBehaviour;
	private BlockPos targetPos = this.getBlockPos();
	private String signalName = "";

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
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		this.processingBehaviour = new ScannerBehaviour(this);
		behaviours.add(this.processingBehaviour);
	}


	@Override
	public boolean scanOnBelt(TransportedItemStack itemStack) {
		if (this.level.getBlockEntity(this.targetPos) instanceof ComputerBlockEntity computer) {
			// TODO: Make this a class system
			computer.interpretSignal(this.getSignalName(),
					new Object[] {RoboScriptObjectConversions.itemStack(itemStack.stack)});
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
	public BlockPos getBlockEntityPos() {
		return this.getBlockPos();
	}

	@Override
	public SyncedTileEntity getBlockEntity() {
		return this;
	}
}
