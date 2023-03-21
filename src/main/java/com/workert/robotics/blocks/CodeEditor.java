package com.workert.robotics.blocks;

import com.workert.robotics.lists.ItemList;
import com.workert.robotics.lists.PacketList;
import com.workert.robotics.packets.EditCodePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

public class CodeEditor extends HorizontalDirectionalBlock {

	public CodeEditor(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
								 BlockHitResult ray) {
		if (!world.isClientSide() && player.getItemInHand(hand).is(ItemList.PROGRAM.get())) {
			PacketList.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
					new EditCodePacket(player.getItemInHand(hand).getOrCreateTag().getString("code")));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

}
