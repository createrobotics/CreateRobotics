package com.workert.robotics.content.computers.computer;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.base.registries.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
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
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends Block implements EntityBlock, ICogWheel, ITE<ComputerBlockEntity> {

	public ComputerBlock(Properties properties) {
		super(properties);
	}


	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack held = player.getMainHandItem();
		if (AllItems.WRENCH.isIn(held)) {
			if (!level.isClientSide) ((ComputerBlockEntity) level.getBlockEntity(blockPos)).runScript();
			player.playSound(SoundEvents.NOTE_BLOCK_CHIME);
		} else if (ItemRegistry.PROGRAM.isIn(held)) {
			if (!level.isClientSide) ((ComputerBlockEntity) level.getBlockEntity(blockPos)).script =
					player.getItemInHand(hand).getOrCreateTag().getString("code");

			player.playSound(SoundEvents.NOTE_BLOCK_BIT);
		} else {
			if (!level.isClientSide)
				player.sendSystemMessage(
						Component.literal(((ComputerBlockEntity) level.getBlockEntity(blockPos)).terminal));
			player.playSound(SoundEvents.BEACON_ACTIVATE);
		}
		//DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.withTileEntityDo(level, blockPos, te -> this.displayScreen(te, player)));
		return InteractionResult.SUCCESS;
	}

	@OnlyIn(value = Dist.CLIENT)
	protected void displayScreen(ComputerBlockEntity computerBlockEntity, Player player) {
		//if (player instanceof LocalPlayer) ScreenOpener.open(new ConsoleScreen(computerBlockEntity.roboScript));
	}

	@Override
	public Class<ComputerBlockEntity> getTileEntityClass() {
		return ComputerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ComputerBlockEntity> getTileEntityType() {
		return BlockEntityRegistry.COMPUTER.get();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return BlockEntityRegistry.COMPUTER.get().create(blockPos, blockState);
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
