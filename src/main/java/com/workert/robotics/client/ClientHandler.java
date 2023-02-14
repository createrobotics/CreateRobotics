package com.workert.robotics.client;

import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.workert.robotics.Robotics;
import com.workert.robotics.client.models.ClockcopterModel;
import com.workert.robotics.client.models.ExtendOBootsModel;
import com.workert.robotics.client.models.MinerModel;
import com.workert.robotics.lists.ItemList;
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

		PONDER_REGISTRATION_HELPER.forComponents(ItemList.PROGRAM)
				.addStoryBoard("programming", PonderList::programming);
	}

	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClockcopterModel.LAYER_LOCATION, ClockcopterModel::createBodyLayer);
		event.registerLayerDefinition(MinerModel.LAYER_LOCATION, MinerModel::createBodyLayer);
		event.registerLayerDefinition(ExtendOBootsModel.LAYER_LOCATION, ExtendOBootsModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void addToItemTooltip(ItemTooltipEvent event) {
		if (!AllConfigs.CLIENT.tooltips.get())
			return;
		if (event.getEntity() == null)
			return;

		ItemStack stack = event.getItemStack();
		String translationKey = stack.getItem()
				.getDescriptionId(stack);

		if (translationKey.startsWith("item." + Robotics.MOD_ID) || translationKey.startsWith(
				"block." + Robotics.MOD_ID))
			if (TooltipHelper.hasTooltip(stack, event.getEntity())) {
				List<Component> itemTooltip = event.getToolTip();
				List<Component> toolTip = new ArrayList<>();
				toolTip.add(itemTooltip.remove(0));
				TooltipHelper.getTooltip(stack)
						.addInformation(toolTip);
				itemTooltip.addAll(0, toolTip);
			}
	}
}
