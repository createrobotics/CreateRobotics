package com.workert.robotics.base.client;
import com.workert.robotics.content.computers.inputs.InputTargetHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (!isGameActive())
			return;

		InputTargetHandler.tick();
	}

	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}
}
