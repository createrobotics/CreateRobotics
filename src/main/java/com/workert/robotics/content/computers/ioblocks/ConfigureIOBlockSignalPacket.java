package com.workert.robotics.content.computers.ioblocks;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureIOBlockSignalPacket extends BlockEntityConfigurationPacket<SyncedBlockEntity> {
	private String signalName;

	public ConfigureIOBlockSignalPacket(BlockPos pos, String signalName) {
		super(pos);
		this.signalName = signalName;
	}

	public ConfigureIOBlockSignalPacket(FriendlyByteBuf buffer) {
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
	protected void applySettings(SyncedBlockEntity be) {
		if (!(be instanceof IOBlockEntity input)) return;
		input.setSignalName(this.signalName);
		input.getBlockEntity().sendData();
	}
}
