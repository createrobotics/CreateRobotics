package com.workert.robotics.content.computing.inputs.scanner;

import com.workert.robotics.base.lists.PacketList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ScannerItem extends BlockItem {

	public ScannerItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		Player player = pContext.getPlayer();
		if (player != null && player.isShiftKeyDown())
			return InteractionResult.SUCCESS;
		return super.useOn(pContext);
	}

	@Override
	public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		return !pPlayer.isShiftKeyDown();
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
		if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer)
			//A block has been placed on the server
			PacketList.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
					new com.lightdev6.computing.packets.ScannerPlacementPacket.ClientBoundRequest(pPos));
		return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
	}
}
