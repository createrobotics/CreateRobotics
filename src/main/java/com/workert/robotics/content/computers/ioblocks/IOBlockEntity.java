package com.workert.robotics.content.computers.ioblocks;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import net.minecraft.core.BlockPos;

public interface IOBlockEntity {
	String getSignalName();

	void setSignalName(String signalName);

	BlockPos getTargetPos();

	BlockPos getBlockEntityPos();

	SyncedTileEntity getBlockEntity();

	RoboScriptObject getRoboScriptObject();
}
