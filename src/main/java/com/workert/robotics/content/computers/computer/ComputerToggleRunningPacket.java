package com.workert.robotics.content.computers.computer;
import com.simibubi.create.foundation.networking.TileEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ComputerToggleRunningPacket extends TileEntityConfigurationPacket<ComputerBlockEntity> {
	private boolean running;

	public ComputerToggleRunningPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public ComputerToggleRunningPacket(BlockPos pos, boolean running) {
		super(pos);
		this.running = running;
	}

	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.running);
	}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {
		this.running = buffer.readBoolean();
	}

	@Override
	protected void applySettings(ComputerBlockEntity computerBlockEntity) {
		if (this.running) {
			computerBlockEntity.runScript();
		} else {
			computerBlockEntity.turnOff();
		}
	}
}
