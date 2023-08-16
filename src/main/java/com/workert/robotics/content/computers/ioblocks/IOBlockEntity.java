package com.workert.robotics.content.computers.ioblocks;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.core.BlockPos;

public interface IOBlockEntity {
	String getSignalName();

	void setSignalName(String signalName);

	BlockPos getTargetPos();

	void setTargetPos(BlockPos targetPos);

	BlockPos getBlockEntityPos();

	SyncedTileEntity getBlockEntity();
}
