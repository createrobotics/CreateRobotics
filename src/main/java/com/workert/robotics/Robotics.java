package com.workert.robotics;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.workert.robotics.client.ClientHandler;
import com.workert.robotics.client.KeybindList;
import com.workert.robotics.client.LangPartials;
import com.workert.robotics.client.screens.MenuList;
import com.workert.robotics.client.screens.SmasherBlockScreen;
import com.workert.robotics.helpers.CodeHelper;
import com.workert.robotics.lists.*;
import com.workert.robotics.world.feature.ModConfiguredFeatures;
import com.workert.robotics.world.feature.ModPlacedFeatures;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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

		Robotics.REGISTRATE.creativeModeTab(() -> ItemList.ROBOTICS_TAB, "Create Robotics");
		BlockList.register();
		EntityList.register();
		ItemList.register();
		BlockEntityList.register();

		MenuList.register(this.modEventBus);
		RecipeList.register(this.modEventBus);

		ModConfiguredFeatures.register(this.modEventBus);
		ModPlacedFeatures.register(this.modEventBus);

		PacketList.registerPackets();

		CodeHelper.registerDefaultCommands();
	}

	private static void clientSetup(final FMLClientSetupEvent event) {
		ClientHandler.init();
		KeybindList.init();

		MenuScreens.register(MenuList.SMASHER_BLOCK_MENU.get(), SmasherBlockScreen::new);
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
