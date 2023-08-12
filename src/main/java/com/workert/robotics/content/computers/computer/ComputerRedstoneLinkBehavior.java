package com.workert.robotics.content.computers.computer;
import com.simibubi.create.content.logistics.IRedstoneLinkable;
import com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;

public class ComputerRedstoneLinkBehavior implements IRedstoneLinkable {
	int signalStrength;
	Couple<RedstoneLinkNetworkHandler.Frequency> frequency;
	BlockPos blockPos;

	ComputerRedstoneLinkBehavior(int signalStrength, Couple<RedstoneLinkNetworkHandler.Frequency> frequency, BlockPos blockPos) {
		this.signalStrength = signalStrength;
		this.frequency = frequency;
		this.blockPos = blockPos;
	}

	@Override
	public int getTransmittedStrength() {
		return this.signalStrength;
	}

	@Override
	public void setReceivedStrength(int power) {
	}

	@Override
	public boolean isListening() {
		return false;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
		return this.frequency;
	}

	@Override
	public BlockPos getLocation() {
		return this.blockPos;
	}
}
