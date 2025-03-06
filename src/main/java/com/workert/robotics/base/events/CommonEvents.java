package com.workert.robotics.base.events;

import com.workert.robotics.Robotics;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CommonEvents {

	@SubscribeEvent
	public static void onLoadWorld(LevelEvent.Load event) {
		LevelAccessor world = event.getLevel();
		Robotics.DRONE_NETWORK.levelLoaded(world);
	}
}
