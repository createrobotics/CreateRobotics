package com.workert.robotics.content.computers.inputs;

import com.simibubi.create.foundation.networking.TileEntityConfigurationPacket;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureInputSignalPacket extends TileEntityConfigurationPacket<SyncedTileEntity> {
	private String signalName;

	public ConfigureInputSignalPacket(BlockPos pos, String signalName) {
		super(pos);
		this.signalName = signalName;
	}

	public ConfigureInputSignalPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}


	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {
		CompoundTag tag = new CompoundTag();
		tag.putString("SignalName", this.signalName);
		buffer.writeNbt(tag);
	}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {
		this.signalName = buffer.readNbt().getString("SignalName");
	}

	@Override
	protected void applySettings(SyncedTileEntity be) {
		if (!(be instanceof IInputBlockEntity input)) return;
		input.setSignalName(this.signalName);
		input.getBlockEntity().sendData();
	}
}
