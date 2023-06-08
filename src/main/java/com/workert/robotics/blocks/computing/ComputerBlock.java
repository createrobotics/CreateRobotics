package com.workert.robotics.blocks.computing;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.workert.robotics.blocks.computing.blockentities.ComputerBlockEntity;
import com.workert.robotics.client.screens.ConsoleScreen;
import com.workert.robotics.lists.BlockEntityList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends Block implements EntityBlock, ICogWheel, ITE<ComputerBlockEntity> {

	public ComputerBlock(Properties properties) {
		super(properties);
	}


	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack held = player.getMainHandItem();
		if (AllItems.WRENCH.isIn(held)) return InteractionResult.PASS;
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> this.withTileEntityDo(level, blockPos, te -> this.displayScreen(te, player)));
		return InteractionResult.SUCCESS;
	}

	@OnlyIn(value = Dist.CLIENT)
	protected void displayScreen(ComputerBlockEntity computerBlockEntity, Player player) {
		if (player instanceof LocalPlayer) ScreenOpener.open(new ConsoleScreen(computerBlockEntity.roboScript));
	}

	@Override
	public Class<ComputerBlockEntity> getTileEntityClass() {
		return ComputerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ComputerBlockEntity> getTileEntityType() {
		return BlockEntityList.COMPUTER.get();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return BlockEntityList.COMPUTER.get().create(blockPos, blockState);
	}


	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return Direction.Axis.Y;
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.MEDIUM;
	}

}
