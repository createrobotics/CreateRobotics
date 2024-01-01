package com.workert.robotics.base.client;

import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.ItemRegistry;
import com.workert.robotics.content.robotics.clockcopter.ClockcopterModel;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxModel;
import com.workert.robotics.content.utility.extendoboots.ExtendOBootsModel;
import com.workert.robotics.unused.miner.MinerModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	static final PonderRegistrationHelper PONDER_REGISTRATION_HELPER = new PonderRegistrationHelper(Robotics.MOD_ID);

	public static void init() {
		MinecraftForge.EVENT_BUS.register(ClientHandler.class);

		PONDER_REGISTRATION_HELPER.forComponents(ItemRegistry.PROGRAM)
				.addStoryBoard("programming", PonderList::programming);
	}

	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClockcopterModel.LAYER_LOCATION, ClockcopterModel::createBodyLayer);
		event.registerLayerDefinition(MinerModel.LAYER_LOCATION, MinerModel::createBodyLayer);
		event.registerLayerDefinition(ExtendOBootsModel.LAYER_LOCATION, ExtendOBootsModel::createBodyLayer);
		event.registerLayerDefinition(FlyingToolboxModel.LAYER_LOCATION, FlyingToolboxModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void addToItemTooltip(ItemTooltipEvent event) {
		if (!AllConfigs.client().tooltips.get()) return;
		if (event.getEntity() == null) return;

		ItemStack stack = event.getItemStack();
		String translationKey = stack.getItem().getDescriptionId(stack);

		if (translationKey.startsWith("item." + Robotics.MOD_ID) || translationKey.startsWith(
				"block." + Robotics.MOD_ID)) {
			List<Component> itemTooltip = event.getToolTip();
			List<Component> toolTip = new ArrayList<>();
			toolTip.add(itemTooltip.remove(0));
			TooltipHelper.getTooltip(stack).addInformation(toolTip);
			itemTooltip.addAll(0, toolTip);
		}
	}
}
