package com.workert.robotics.content.computers.ioblocks;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import net.minecraft.core.BlockPos;

public interface IOBlockEntity {
	String getSignalName();

	void setSignalName(String signalName);

	BlockPos getTargetPos();

	BlockPos getBlockEntityPos();

	SyncedTileEntity getBlockEntity();

	RoboScriptObject getRoboScriptObject();

	void resetSignals();

	default ComputerBlockEntity getConnectedComputer() {
		return this.getBlockEntity().getLevel()
				.getExistingBlockEntity(this.getTargetPos()) instanceof ComputerBlockEntity e ? e : null;
	}
}
