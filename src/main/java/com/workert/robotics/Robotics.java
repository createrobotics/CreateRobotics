package com.workert.robotics;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.workert.robotics.lists.BlockEntityList;
import com.workert.robotics.lists.BlockList;
import com.workert.robotics.lists.EntityList;
import com.workert.robotics.lists.ItemList;
import com.workert.robotics.lists.RecipeList;
import com.workert.robotics.screens.ModMenuTypes;
import com.workert.robotics.screens.SmasherBlockScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Robotics.MOD_ID)
public class Robotics {

	public static final String MOD_ID = "robotics";
	public static final Logger LOGGER = LogUtils.getLogger();

	final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

	public Robotics() {
		this.modEventBus.addListener(this::clientSetup);

		this.modEventBus.addListener(EntityList::addEntityAttributes);

		BlockList.register(this.modEventBus);
		EntityList.ENTITY_TYPES.register(this.modEventBus); // Needs to register before ModItems because some items depend on the Registry Objects in EntityList.class
		ItemList.register(this.modEventBus);
		BlockEntityList.register(this.modEventBus);
		ModMenuTypes.register(this.modEventBus);
		RecipeList.register(this.modEventBus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(BlockList.SMASHER_BLOCK.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(BlockList.DRONE_ASSEMBLER.get(), RenderType.translucent());
		MenuScreens.register(ModMenuTypes.SMASHER_BLOCK_MENU.get(), SmasherBlockScreen::new);
	}

}
