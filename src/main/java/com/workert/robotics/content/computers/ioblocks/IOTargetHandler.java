package com.workert.robotics.content.computers.ioblocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.depot.EjectorTargetHandler;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class IOTargetHandler {
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

		if (!(event.getLevel()
				.getBlockEntity(event.getPos()) instanceof ComputerBlockEntity)) {
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.FAIL);
			return;
		}

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

	public static void flush() {
		currentSelection = null;
		currentItem = null;
	}

	public static void tick() {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return;
		ItemStack heldItem = player.getMainHandItem();
		if (!(heldItem.getItem() instanceof IOBlockItem)) {
			currentItem = null;
		} else {
			if (heldItem != currentItem) {
				currentSelection = null;
				currentItem = heldItem;
			}
			EjectorTargetHandler.drawOutline(currentSelection);
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
		BlockPos resultPos = result.getBlockPos();

		BlockEntity te = Minecraft.getInstance().level.getBlockEntity(resultPos);
		if (!(te instanceof IOBlockEntity inputBlockEntity)) {
			lastHoveredBlockPos = -1;
			currentSelection = null;
			return;
		}

		if (lastHoveredBlockPos == -1 || lastHoveredBlockPos != resultPos.asLong()) {
			if (!inputBlockEntity.getTargetPos()
					.equals(inputBlockEntity.getBlockEntityPos()))
				currentSelection = inputBlockEntity.getTargetPos();
			lastHoveredBlockPos = resultPos.asLong();
		}

		if (lastHoveredBlockPos != -1)
			EjectorTargetHandler.drawOutline(currentSelection);
	}
}
