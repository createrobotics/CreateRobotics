package com.workert.robotics.world;

import com.workert.robotics.world.gen.ModOreGeneration;

public class ModWorldEvents {
	//@SubscribeEvent
	public static void biomeLoadingEvent(final BiomeLoadingEvent event) {
		ModOreGeneration.generateOres(event);
	}
}
