package com.workert.robotics.blocks.computing.blockentities;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.workert.robotics.lists.BlockEntityList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerBlockEntity extends KineticTileEntity implements ScannerBehaviour.ScanningBehaviorSpecifics, IInputBlockEntity {
	public ScannerBehaviour processingBehaviour;
	private BlockPos targetPos = this.getBlockPos();
	private String signalName = "";

	public ScannerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(BlockEntityList.SCANNER.get(), pos, state);
	}


	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		this.signalName = compound.getString("SignalName");
		this.targetPos = new BlockPos(compound.getInt("TX"), compound.getInt("TY"), compound.getInt("TZ"));
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("SignalName", this.signalName);
		compound.putInt("TX", this.getTargetPos().getX());
		compound.putInt("TY", this.getTargetPos().getY());
		compound.putInt("TZ", this.getTargetPos().getZ());
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
			Map<String, Object> fields = new HashMap<>();
			fields.put("id", ForgeRegistries.ITEMS.getKey(itemStack.stack.getItem()).toString());
			fields.put("name", itemStack.stack.getHoverName().getString());
			fields.put("count", (double) itemStack.stack.getCount());
			/*Computing.runFunctionProgram(this.signalName,
					Arrays.asList(new ZincStructureConversionObject("Item", fields)), computer.getScript(), computer);*/
			// TODO Run method
		}
		System.out.println(itemStack.stack.getHoverName());
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
	public SyncedTileEntity getBlockEntity() {
		return this;
	}
}
