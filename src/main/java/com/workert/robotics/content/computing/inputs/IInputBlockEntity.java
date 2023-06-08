package com.workert.robotics.content.computing.inputs;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.core.BlockPos;

public interface IInputBlockEntity {
	String getSignalName();

	void setSignalName(String signalName);

	BlockPos getTargetPos();

	void setTargetPos(BlockPos targetPos);

	SyncedTileEntity getBlockEntity();
}
