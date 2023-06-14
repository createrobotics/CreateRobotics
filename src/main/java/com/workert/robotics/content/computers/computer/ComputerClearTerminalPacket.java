package com.workert.robotics.content.computers.computer;
import com.simibubi.create.foundation.networking.TileEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ComputerClearTerminalPacket extends TileEntityConfigurationPacket<ComputerBlockEntity> {

	public ComputerClearTerminalPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public ComputerClearTerminalPacket(BlockPos pos) {
		super(pos);
	}

	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {

	}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {

	}

	@Override
	protected void applySettings(ComputerBlockEntity computerBlockEntity) {
		computerBlockEntity.clearTerminal();
	}
}
