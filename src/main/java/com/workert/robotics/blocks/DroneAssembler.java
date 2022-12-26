package com.workert.robotics.blocks;

import com.workert.robotics.blockentities.DroneAssemblerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DroneAssembler extends BaseEntityBlock {

	public DroneAssembler(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new DroneAssemblerBlockEntity(pPos, pState);
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {

		BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

		if (!(blockEntity instanceof DroneAssemblerBlockEntity))
			return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);

		((DroneAssemblerBlockEntity) blockEntity).tryAssemble();

		return InteractionResult.SUCCESS;
	}
}
