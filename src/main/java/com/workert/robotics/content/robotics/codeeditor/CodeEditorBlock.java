package com.workert.robotics.content.robotics.codeeditor;

import com.simibubi.create.foundation.gui.ScreenOpener;
import com.workert.robotics.base.registries.ItemRegistry;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class CodeEditorBlock extends HorizontalDirectionalBlock {

	public CodeEditorBlock(Properties properties) {
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
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
		if (!world.isClientSide() && player.getItemInHand(hand).is(ItemRegistry.PROGRAM.get())) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ScreenOpener.open(
					new CodeEditorScreen(player.getItemInHand(hand).getOrCreateTag().getString("code"))));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

}
