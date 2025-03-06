package com.workert.robotics.base.registries;

import com.workert.robotics.Robotics;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class PartialModelRegistry {
	public static final PartialModel
			DELIVERY_DRONE = entity("delivery_drone");

	private static PartialModel block(String path) {
		return PartialModel.of(Robotics.asResource("block/" + path));
	}

	private static PartialModel entity(String path) {
		return PartialModel.of(Robotics.asResource("entity/" + path));
	}

	public static void init() {
		// init static fields
	}
}
