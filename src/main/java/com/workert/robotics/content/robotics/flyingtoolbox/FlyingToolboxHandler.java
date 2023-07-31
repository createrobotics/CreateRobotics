package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.foundation.utility.WorldAttached;

import java.util.ArrayList;
import java.util.List;

public class FlyingToolboxHandler {
	public static final WorldAttached<List<FlyingToolbox>> flyingToolboxes =
			new WorldAttached<>(w -> new ArrayList<>());

	public static void onLoad(FlyingToolbox flyingToolbox) {
		flyingToolboxes.get(flyingToolbox.getLevel())
				.add(flyingToolbox);
	}

	public static void onUnload(FlyingToolbox flyingToolbox) {
		flyingToolboxes.get(flyingToolbox.getLevel())
				.remove(flyingToolbox);
	}
}
