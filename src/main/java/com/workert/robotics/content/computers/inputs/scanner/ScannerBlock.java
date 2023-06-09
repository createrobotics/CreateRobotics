package com.workert.robotics.content.computers.inputs.scanner;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.workert.robotics.base.registries.AllBlockEntities;
import com.workert.robotics.content.computers.inputs.InputSignalScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class ScannerBlock extends HorizontalKineticBlock implements ITE<ScannerBlockEntity> {

	public ScannerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<ScannerBlockEntity> getTileEntityClass() {
		return ScannerBlockEntity.class;
	}

	@Override
	public BlockEntityType getTileEntityType() {
		return AllBlockEntities.SCANNER.get();
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
	protected void displayScreen(ScannerBlockEntity scanner, Player player) {
		if (player instanceof LocalPlayer) ScreenOpener.open(new InputSignalScreen(scanner));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction preferredSide = this.getPreferredHorizontalFacing(context);
		if (preferredSide != null) return this.defaultBlockState().setValue(HORIZONTAL_FACING, preferredSide);
		return super.getStateForPlacement(context);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() instanceof Player)
			return AllShapes.CASING_14PX.get(Direction.DOWN);

		return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
	}
}
