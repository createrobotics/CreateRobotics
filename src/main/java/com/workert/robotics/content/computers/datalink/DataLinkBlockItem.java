package com.workert.robotics.content.computers.datalink;
import com.simibubi.create.CreateClient;
import com.workert.robotics.content.computers.inputs.InputBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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

public class DataLinkBlockItem extends BlockItem {
	public DataLinkBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		Player player = pContext.getPlayer();
		if (player == null)
			return null;

		if (player.isShiftKeyDown()) {
			return this.handleShiftClick(pContext, player);
		} else {
			return this.handleAttemptedPlace(pContext, player);
		}
	}

	private InteractionResult handleAttemptedPlace(UseOnContext pContext, Player player) {
		ItemStack hand = pContext.getItemInHand();
		CompoundTag tag = hand.getTag();
		if (hand.getOrCreateTag().contains("BlockEntityTag")) {
			player.displayClientMessage(
					Component.literal("Select an I/O block as a target first.").withStyle(ChatFormatting.RED),
					true
			);
			return InteractionResult.FAIL;
		} else if (!this.checkAllPositions(pContext,
				tag.getCompound("BlockEntityTag").getList("TargetPositions", Tag.TAG_COMPOUND))) {
			return InteractionResult.FAIL;
		}
		InteractionResult result = super.useOn(pContext);
		tag.remove("BlockEntityTag");
		if (pContext.getLevel().isClientSide) DataLinkTargetHandler.flush();
		return result;
	}

	private InteractionResult handleShiftClick(UseOnContext pContext, Player player) {
		if (!(pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof InputBlockEntity)) {
			player.displayClientMessage(
					Component.literal("Target is not an I/O block.").withStyle(ChatFormatting.RED),
					true
			);
			return InteractionResult.FAIL;
		}


		CompoundTag blockEntityTag = new CompoundTag();
		ListTag list = new ListTag();
		list.add(NbtUtils.writeBlockPos(pContext.getClickedPos()));
		blockEntityTag.put("TargetPositions", list);
		pContext.getItemInHand().getOrCreateTag().put("BlockEntityTag", blockEntityTag);
		player.displayClientMessage(Component.literal("Target set").withStyle(ChatFormatting.GOLD), true);
		return InteractionResult.SUCCESS;
	}

	private boolean checkAllPositions(UseOnContext pContext, ListTag list) {
		BlockPos clickedPos = pContext.getClickedPos();
		for (int i = 0; i < list.size(); i++) {
			if (NbtUtils.readBlockPos(list.getCompound(i))
					.distToCenterSqr(clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()) > 64) {
				return false;
			}
		}
		return true;
	}

	private void findAndRemovePosition(BlockPos pos, ListTag targetPositions) {
		for (int i = 0; i < targetPositions.size(); i++) {
			if (pos.equals(NbtUtils.readBlockPos(targetPositions.getCompound(i)))) {
				targetPositions.remove(i);
				return;
			}
		}
	}

	@Override
	public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		if (pPlayer.isShiftKeyDown()) {
			this.findAndRemovePosition(pPos, pPlayer.getUseItem().getOrCreateTag().getCompound("BlockEntityTag")
					.getList("TargetPositions", Tag.TAG_COMPOUND));
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
