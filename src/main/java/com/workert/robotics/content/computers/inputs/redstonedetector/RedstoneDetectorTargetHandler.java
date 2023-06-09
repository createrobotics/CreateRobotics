package com.workert.robotics.content.computers.inputs.redstonedetector;

import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.networking.AllPackets;
import com.workert.robotics.base.lists.BlockList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RedstoneDetectorTargetHandler {
	static BlockPos currentSelection;
	static ItemStack currentItem;
	static long lastHoveredBlockPos;

	@SubscribeEvent
	public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (currentItem == null)
			return;
		BlockPos pos = event.getPos();
		Level world = event.getLevel();
		if (!world.isClientSide)
			return;
		Player player = event.getEntity();
		if (player == null || player.isSpectator() || !player.isShiftKeyDown())
			return;

		player.displayClientMessage(Component.literal("Target set").withStyle(ChatFormatting.GOLD), true);
		currentSelection = pos;
		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.SUCCESS);
	}

	@SubscribeEvent
	public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (currentItem == null)
			return;
		if (!event.getLevel().isClientSide)
			return;
		if (!event.getEntity().isShiftKeyDown())
			return;
		BlockPos pos = event.getPos();
		if (pos.equals(currentSelection)) {
			currentSelection = null;
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}

	public static void flushSettings(BlockPos pos) {
		LocalPlayer player = Minecraft.getInstance().player;
		player.displayClientMessage(Component.literal("Selected computer"), true);
		AllPackets.channel.sendToServer(new RedstoneDetectorPlacementPacket(pos, currentSelection));
		currentSelection = null;
		currentItem = null;
	}

	public static void tick() {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return;
		ItemStack heldItem = player.getMainHandItem();
		if (!BlockList.REDSTONE_DETECTOR.isIn(heldItem)) {
			currentItem = null;
		} else {
			if (heldItem != currentItem) {
				currentSelection = null;
				currentItem = heldItem;
			}
			drawOutline(currentSelection);
		}
		checkForWrench(heldItem);
	}

	private static void checkForWrench(ItemStack heldItem) {
		if (!AllItems.WRENCH.isIn(heldItem))
			return;
		HitResult objectMouseOver = Minecraft.getInstance().hitResult;
		if (!(objectMouseOver instanceof BlockHitResult))
			return;
		BlockHitResult result = (BlockHitResult) objectMouseOver;
		BlockPos pos = result.getBlockPos();

		BlockEntity te = Minecraft.getInstance().level.getBlockEntity(pos);
		if (!(te instanceof RedstoneDetectorBlockEntity)) {
			lastHoveredBlockPos = -1;
			currentSelection = null;
			return;
		}

		if (lastHoveredBlockPos == -1 || lastHoveredBlockPos != pos.asLong()) {
			RedstoneDetectorBlockEntity rd = (RedstoneDetectorBlockEntity) te;
			if (!rd.getTargetPos()
					.equals(rd.getBlockPos()))
				currentSelection = rd.getTargetPos();
			lastHoveredBlockPos = pos.asLong();
		}

		if (lastHoveredBlockPos != -1)
			drawOutline(currentSelection);
	}

	public static void drawOutline(BlockPos selection) {
		Level world = Minecraft.getInstance().level;
		if (selection == null)
			return;

		BlockPos pos = selection;
		BlockState state = world.getBlockState(pos);
		VoxelShape shape = state.getShape(world, pos);
		AABB boundingBox = shape.isEmpty() ? new AABB(BlockPos.ZERO) : shape.bounds();
		CreateClient.OUTLINER.showAABB("target", boundingBox.move(pos))
				.colored(0xffcb74)
				.lineWidth(1 / 16f);
	}
}
