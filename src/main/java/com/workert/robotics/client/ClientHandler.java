package com.workert.robotics.client;

import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.components.steam.SteamEngineBlock;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.ponder.PonderTooltipHandler;
import com.simibubi.create.foundation.utility.Components;
import com.workert.robotics.Robotics;
import com.workert.robotics.client.models.ClockcopterModel;
import com.workert.robotics.client.models.CodeDroneModel;
import com.workert.robotics.client.models.ExtendOBootsModel;
import com.workert.robotics.client.models.MinerModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	public static void init() {
		MinecraftForge.EVENT_BUS.register(ClientHandler.class);
	}

	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClockcopterModel.LAYER_LOCATION, ClockcopterModel::createBodyLayer);
		event.registerLayerDefinition(MinerModel.LAYER_LOCATION, MinerModel::createBodyLayer);
		event.registerLayerDefinition(CodeDroneModel.LAYER_LOCATION, CodeDroneModel::createBodyLayer);
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

		if (stack.getItem() instanceof BlockItem) {
			BlockItem item = (BlockItem) stack.getItem();
			if (item.getBlock() instanceof IRotate || item.getBlock() instanceof SteamEngineBlock) {
				List<Component> kineticStats = ItemDescription.getKineticStats(item.getBlock());
				if (!kineticStats.isEmpty()) {
					event.getToolTip()
							.add(Components.immutableEmpty());
					event.getToolTip()
							.addAll(kineticStats);
				}
			}
		}

		PonderTooltipHandler.addToTooltip(event.getToolTip(), stack);
		SequencedAssemblyRecipe.addToTooltip(event.getToolTip(), stack);
	}
}
