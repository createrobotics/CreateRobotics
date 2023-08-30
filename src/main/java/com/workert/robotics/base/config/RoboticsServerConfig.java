package com.workert.robotics.base.config;
import com.simibubi.create.foundation.config.ConfigBase;

public class RoboticsServerConfig extends ConfigBase {
	public final ConfigInt maxIOBlocksPlacementRange =
			this.i(12, 1, "maxIOBlocksPlacementRange",
					"Max range an IO Block can be apart of the linked Computer, in Blocks. A Value of 1 means that it can only be placed next to the Computer.");

	@Override
	public String getName() {
		return "server";
	}
}
