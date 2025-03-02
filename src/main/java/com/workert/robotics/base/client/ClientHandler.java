package com.workert.robotics.base.client;

import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.ItemRegistry;
import com.workert.robotics.content.robotics.clockcopter.ClockcopterModel;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxModel;
import com.workert.robotics.content.utility.extendoboots.ExtendOBootsModel;
import com.workert.robotics.unused.miner.MinerModel;
import net.createmod.ponder.foundation.registration.DefaultPonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientHandler {
	private static final PonderLocalization LOCALIZATION = new PonderLocalization();
	private static final PonderSceneRegistry SCENES = new PonderSceneRegistry(LOCALIZATION);
	static final DefaultPonderSceneRegistrationHelper PONDER_REGISTRATION_HELPER = new DefaultPonderSceneRegistrationHelper(
			Robotics.MOD_ID, SCENES);

	public static void init() {
		MinecraftForge.EVENT_BUS.register(ClientHandler.class);

		PONDER_REGISTRATION_HELPER.forComponents(ItemRegistry.PROGRAM.getId())
				.addStoryBoard("programming", PonderList::programming);
	}

	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClockcopterModel.LAYER_LOCATION, ClockcopterModel::createBodyLayer);
		event.registerLayerDefinition(MinerModel.LAYER_LOCATION, MinerModel::createBodyLayer);
		event.registerLayerDefinition(ExtendOBootsModel.LAYER_LOCATION, ExtendOBootsModel::createBodyLayer);
		event.registerLayerDefinition(FlyingToolboxModel.LAYER_LOCATION, FlyingToolboxModel::createBodyLayer);
	}
}
