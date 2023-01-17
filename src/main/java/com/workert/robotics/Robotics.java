package com.workert.robotics;

import com.mojang.logging.LogUtils;
import com.workert.robotics.client.KeybindList;
import com.workert.robotics.client.screens.ModMenuTypes;
import com.workert.robotics.client.screens.SmasherBlockScreen;
import com.workert.robotics.helpers.CodeHelper;
import com.workert.robotics.lists.*;
import com.workert.robotics.world.feature.ModConfiguredFeatures;
import com.workert.robotics.world.feature.ModPlacedFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Robotics.MOD_ID)
public class Robotics {

	public static final String MOD_ID = "robotics";
	public static final Logger LOGGER = LogUtils.getLogger();

	final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

	public Robotics() {
		this.modEventBus.addListener(Robotics::clientSetup);

		this.modEventBus.addListener(EntityList::addEntityAttributes);

		BlockList.register(this.modEventBus);
		EntityList.ENTITY_TYPES.register(
				this.modEventBus); // Needs to register before ModItems because some items depend on the Registry Objects in EntityList.class
		ItemList.register(this.modEventBus);
		BlockEntityList.register(this.modEventBus);
		ModMenuTypes.register(this.modEventBus);
		RecipeList.register(this.modEventBus);

		ModConfiguredFeatures.register(this.modEventBus);
		ModPlacedFeatures.register(this.modEventBus);

		PacketList.registerPackets();

		MinecraftForge.EVENT_BUS.register(this);

		CodeHelper.registerDefaultCommands();

		boolean isLocal = Minecraft.getInstance().player.isLocalPlayer();
	}

	private static void clientSetup(final FMLClientSetupEvent event) {
		KeybindList.init();

		ItemBlockRenderTypes.setRenderLayer(BlockList.SMASHER_BLOCK.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(BlockList.DRONE_ASSEMBLER.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(BlockList.CODE_EDITOR.get(), RenderType.translucent());
		// TODO Set the render types in block model's JSON

		MenuScreens.register(ModMenuTypes.SMASHER_BLOCK_MENU.get(), SmasherBlockScreen::new);
	}

}
