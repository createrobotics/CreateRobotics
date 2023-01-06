package com.workert.robotics.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.workert.robotics.Robotics;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Robotics.MOD_ID)
public class KeybindList {
	public static List<KeyMapping> keyMappings = new ArrayList<>();
	public static KeyMapping changeExtendOBootsHeight;

	public static void init() {
		changeExtendOBootsHeight = registerKeyMapping("changeExtendOBootsHeight", InputConstants.KEY_LCONTROL);
	}

	private static KeyMapping registerKeyMapping(String name, int defaultKey) {
		KeyMapping key = new KeyMapping("key." + Robotics.MOD_ID + "." + name, defaultKey, "itemGroup.robotics");
		keyMappings.add(key);
		return key;
	}

	@SubscribeEvent
	public static void registerAllKeyMappings(RegisterKeyMappingsEvent event) {
		for (KeyMapping keyMapping : keyMappings) {
			event.register(keyMapping);
		}
	}
}
