package com.workert.robotics;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.workert.robotics.base.client.ClientHandler;
import com.workert.robotics.base.client.KeybindList;
import com.workert.robotics.base.client.LangPartials;
import com.workert.robotics.base.config.RoboticsConfigs;
import com.workert.robotics.base.registries.*;
import com.workert.robotics.base.world.feature.RoboticsConfiguredFeatures;
import com.workert.robotics.base.world.feature.RoboticsPlacedFeatures;
import com.workert.robotics.unused.smasher.SmasherBlockScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Robotics.MOD_ID)
public class Robotics {

	public static final String MOD_ID = "robotics";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

	final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

	public Robotics() {
		this.modEventBus.addListener(Robotics::clientSetup);
		this.modEventBus.addListener(ClientHandler::registerLayerDefinition);

		REGISTRATE.registerEventListeners(this.modEventBus);
		this.modEventBus.addListener(Robotics::gatherData);

		Robotics.REGISTRATE.creativeModeTab(() -> ItemRegistry.ROBOTICS_TAB, "Create Robotics");
		BlockRegistry.register();
		EntityRegistry.register();
		ItemRegistry.register();
		BlockEntityRegistry.register();

		MenuRegistry.register(this.modEventBus);
		RecipeRegistry.register(this.modEventBus);

		RoboticsConfiguredFeatures.register(this.modEventBus);
		RoboticsPlacedFeatures.register(this.modEventBus);

		PacketRegistry.registerPackets();

		RoboticsConfigs.register(ModLoadingContext.get());
	}

	private static void clientSetup(final FMLClientSetupEvent event) {
		ClientHandler.init();
		KeybindList.init();

		MenuScreens.register(MenuRegistry.SMASHER_BLOCK_MENU.get(), SmasherBlockScreen::new);
	}

	public static void gatherData(GatherDataEvent event) {
		DataGenerator dataGenerator = event.getGenerator();
		if (event.includeClient()) {
			PonderLocalization.provideRegistrateLang(Robotics.REGISTRATE);
			dataGenerator.addProvider(true,
					new LangMerger(dataGenerator, Robotics.MOD_ID, "Create Robotics", LangPartials.values()));
		}
	}
}
