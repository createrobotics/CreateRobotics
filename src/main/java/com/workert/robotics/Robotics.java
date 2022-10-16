package com.workert.robotics;

import com.workert.robotics.recipe.ModRecipes;
import org.slf4j.Logger;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.mojang.logging.LogUtils;
import com.workert.robotics.block.ModBlocks;
import com.workert.robotics.block.entity.ModBlockEntities;
import com.workert.robotics.client.flywheel.ClockcopterInstance;
import com.workert.robotics.entities.ModEntities;
import com.workert.robotics.item.ModItems;
import com.workert.robotics.screen.ModMenuTypes;
import com.workert.robotics.screen.SmasherBlockScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Robotics.MOD_ID)
public class Robotics {

	public static final String MOD_ID = "robotics";
	public static final Logger LOGGER = LogUtils.getLogger();

	final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

	public Robotics() {
		// this.modEventBus.addListener(this::setup);
		this.modEventBus.addListener(this::clientSetup);

		this.modEventBus.addListener(ModEntities::addEntityAttributes);

		ModBlocks.register(this.modEventBus);
		ModEntities.EntityTypes.register(this.modEventBus); // Needs to register before ModItems because some items depend on the Registry Objects in ModEntities
		ModItems.register(this.modEventBus);
		ModBlockEntities.register(this.modEventBus);
		ModMenuTypes.register(this.modEventBus);
		ModRecipes.register(this.modEventBus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			InstancedRenderRegistry.configure(ModEntities.CLOCKCOPTER.get()).factory(ClockcopterInstance::new).apply();
		});
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.SMASHER_BLOCK.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DRONE_ASSEMBLER.get(), RenderType.translucent());
		MenuScreens.register(ModMenuTypes.SMASHER_BLOCK_MENU.get(), SmasherBlockScreen::new);
	}

}
