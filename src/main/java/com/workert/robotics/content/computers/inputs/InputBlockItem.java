package com.workert.robotics.content.computers.inputs;
import com.simibubi.create.CreateClient;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InputBlockItem extends BlockItem {
	public InputBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		Player player = pContext.getPlayer();
		if (player == null)
			return null;

		if (player.isShiftKeyDown()) {
			if (!(pContext.getLevel()
					.getBlockEntity(pContext.getClickedPos()) instanceof ComputerBlockEntity)) {
				player.displayClientMessage(Component.literal("Target is not a computer")
						.withStyle(ChatFormatting.RED), true);
				return InteractionResult.FAIL;
			}


			CompoundTag blockEntityTag = new CompoundTag();
			blockEntityTag.put("TargetPosition", NbtUtils.writeBlockPos(pContext.getClickedPos()));
			pContext.getItemInHand().getOrCreateTag().put("BlockEntityTag", blockEntityTag);
			player.displayClientMessage(Component.literal("Target set").withStyle(ChatFormatting.GOLD), true);


			return InteractionResult.SUCCESS;
		} else {
			if (!pContext.getItemInHand().getOrCreateTag().contains("BlockEntityTag")) {
				player.displayClientMessage(Component.literal("Select a Computer as Target first")
						.withStyle(ChatFormatting.RED), true);
				return InteractionResult.FAIL;

			} else if (NbtUtils.readBlockPos(
							pContext.getItemInHand().getTag().getCompound("BlockEntityTag").getCompound("TargetPosition"))
					.distToCenterSqr(pContext.getClickedPos().getX(), pContext.getClickedPos().getY(),
							pContext.getClickedPos().getZ()) > 64) {
				player.displayClientMessage(Component.literal("Target Computer is too far from here")
						.withStyle(ChatFormatting.RED), true);
				return InteractionResult.FAIL;
			}
			pContext.getItemInHand().getTag().remove("BlockEntityTag");
			if (pContext.getLevel().isClientSide)
				InputTargetHandler.flush();
			return super.useOn(pContext);
		}
	}


	@Override
	public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		if (pPlayer.isShiftKeyDown()) {
			if (pPos.equals(NbtUtils.readBlockPos(
					pPlayer.getUseItem().getOrCreateTag().getCompound("BlockEntityTag").getCompound("TargetPosition"))))
				pPlayer.getUseItem().getTag().getCompound("BlockEntityTag").remove("TargetPosition");
			return false;
		}
		return true;
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if (pEntity instanceof Player && pLevel.isClientSide && pIsSelected)
			drawOutline(NbtUtils.readBlockPos(
					pEntity.getSlot(pSlotId).get().getOrCreateTag().getCompound("BlockEntityTag")
							.getCompound("TargetPosition")));
		super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
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
