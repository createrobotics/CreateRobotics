package com.workert.robotics.world;

import com.workert.robotics.Robotics;
import com.workert.robotics.world.gen.ModOreGeneration;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Robotics.MOD_ID)
public class ModWorldEvents {
	@SubscribeEvent
	public static void biomeLoadingEvent(final BiomeLoadingEvent event) {
		ModOreGeneration.generateOres(event);
	}
}
