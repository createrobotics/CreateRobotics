package com.workert.robotics.client;

import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionEntityRenderer;
import com.workert.robotics.Robotics;
import com.workert.robotics.client.model.ClocktoperModel;
import com.workert.robotics.client.renderers.ClocktoperRenderer;
import com.workert.robotics.entities.DroneContraptionEntity;
import com.workert.robotics.entities.ModEntities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Robotics.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientHandler {

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.DRONE.get(), ContraptionEntityRenderer<DroneContraptionEntity>::new);
		event.registerEntityRenderer(ModEntities.CLOCKTOPER.get(), ClocktoperRenderer::new);
	}

	@SubscribeEvent
	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClocktoperModel.LAYER_LOCATION, ClocktoperModel::createBodyLayer);
	}

}
