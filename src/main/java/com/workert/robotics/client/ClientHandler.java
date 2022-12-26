package com.workert.robotics.client;

import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionEntityRenderer;
import com.workert.robotics.Robotics;
import com.workert.robotics.client.models.ClockcopterModel;
import com.workert.robotics.client.models.CodeDroneModel;
import com.workert.robotics.client.models.MinerModel;
import com.workert.robotics.client.renderers.ClockcopterRenderer;
import com.workert.robotics.client.renderers.CodeDroneRenderer;
import com.workert.robotics.client.renderers.CodeEditorRenderer;
import com.workert.robotics.client.renderers.MinerRenderer;
import com.workert.robotics.entities.DroneContraptionEntity;
import com.workert.robotics.lists.BlockEntityList;
import com.workert.robotics.lists.EntityList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Robotics.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientHandler {

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityList.DRONE.get(), ContraptionEntityRenderer<DroneContraptionEntity>::new);
		event.registerEntityRenderer(EntityList.CLOCKCOPTER.get(), ClockcopterRenderer::new);
		event.registerEntityRenderer(EntityList.MINER.get(), MinerRenderer::new);
		event.registerEntityRenderer(EntityList.CODE_DRONE.get(), CodeDroneRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityList.CODE_EDITOR_BLOCK_ENTITY.get(), CodeEditorRenderer::new);
	}

	@SubscribeEvent
	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClockcopterModel.LAYER_LOCATION, ClockcopterModel::createBodyLayer);
		event.registerLayerDefinition(MinerModel.LAYER_LOCATION, MinerModel::createBodyLayer);
		event.registerLayerDefinition(CodeDroneModel.LAYER_LOCATION, CodeDroneModel::createBodyLayer);
	}

}
