package com.workert.robotics.content.robotics.flyingtoolbox;

import net.createmod.catnip.data.WorldAttached;

import java.util.ArrayList;
import java.util.List;

public class FlyingToolboxHandler {
	public static final WorldAttached<List<FlyingToolbox>> flyingToolboxes =
			new WorldAttached<>(w -> new ArrayList<>());

	public static void onLoad(FlyingToolbox flyingToolbox) {
		flyingToolboxes.get(flyingToolbox.level())
				.add(flyingToolbox);
	}

	public static void onUnload(FlyingToolbox flyingToolbox) {
		flyingToolboxes.get(flyingToolbox.level())
				.remove(flyingToolbox);
	}
}
