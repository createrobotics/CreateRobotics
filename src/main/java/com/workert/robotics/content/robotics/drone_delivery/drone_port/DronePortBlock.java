package com.workert.robotics.content.robotics.drone_delivery.drone_port;

import com.simibubi.create.foundation.block.IBE;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

public class DronePortBlock extends Block implements IBE<DronePortBlockEntity> {
	public DronePortBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
								 BlockHitResult pHit) {
		return this.onBlockEntityUse(pLevel, pPos, be -> be.use(pPlayer));
	}

	@Override
	public Class<DronePortBlockEntity> getBlockEntityClass() {
		return DronePortBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends DronePortBlockEntity> getBlockEntityType() {
		return BlockEntityRegistry.DRONE_PORT.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		return this.getBlockEntityOptional(pLevel, pPos).map(pbe -> pbe.getComparatorOutput())
				.orElse(0);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	}
}
