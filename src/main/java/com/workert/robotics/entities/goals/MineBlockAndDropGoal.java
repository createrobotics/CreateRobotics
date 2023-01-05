package com.workert.robotics.entities.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MineBlockAndDropGoal extends MoveToBlockGoal {

	private final List<Block> blocksToRemove;

	private final PathfinderMob mob;
	private int ticksSinceReachedGoal;
	private final List<BlockPos> posBlackList = new ArrayList<>();

	public MineBlockAndDropGoal(PathfinderMob pMob, List<Block> blocksToRemove, double pSpeedModifier, int pSearchRange,
			int pVerticalSearchRange) {
		super(pMob, pSpeedModifier, pSearchRange, pVerticalSearchRange);
		this.blocksToRemove = blocksToRemove;
		this.mob = pMob;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
	}

	@Override
	public void start() {
		super.start();
		this.ticksSinceReachedGoal = 0;
	}

	@Override
	public double acceptedDistance() {
		return 2;
	}

	// Tick method copied from RemoveBlockGoal and edited for block drop
	@Override
	public void tick() {
		super.tick();
		Level level = this.mob.level;
		RandomSource random = this.mob.getRandom();

		if (this.blockPos != null && this.isValidTarget(level, this.blockPos) && this.blockPos.closerToCenterThan(
				this.mob.position(), this.acceptedDistance())) {

			this.mob.getLookControl()
					.setLookAt(this.blockPos.getX() + 0.5, this.blockPos.getY() + 0.5, this.blockPos.getZ() + 0.5);

			this.mob.getNavigation().stop();

			if (!level.isClientSide && this.ticksSinceReachedGoal % 2 == 0) {
				for (int i = 0; i < 20; ++i) {
					double d3 = random.nextGaussian() * 0.02D;
					double d1 = random.nextGaussian() * 0.02D;
					double d2 = random.nextGaussian() * 0.02D;
					((ServerLevel) level).sendParticles(
							new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(this.blockPos)),
							this.blockPos.getX() + 0.5D, this.blockPos.getY() + 0.5D, this.blockPos.getZ() + 0.5D, 1,
							d3, d1, d2, 0.15F);
				}
			}

			if (this.ticksSinceReachedGoal > 60 && !level.isClientSide) {
				level.destroyBlock(this.blockPos, true);
				//level.getBlockState(blockPos).getDrops(new LootContext.Builder((ServerLevel) level).withOptionalParameter(LootContextParams.ORIGIN, pValue));
				if (this.findNearestBlock())
					this.start();
			}
			++this.ticksSinceReachedGoal;
		}

		if (this.blockPos != null && this.mob.getNavigation().isDone() && !this.blockPos.closerToCenterThan(
				this.mob.position(), this.acceptedDistance()) && this.isValidTarget(level, this.blockPos))
			this.posBlackList.add(this.blockPos);
	}

	@Override
	protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
		if (pLevel == null || pPos == null || this.blocksToRemove == null || this.posBlackList.contains(pPos)) {
			return false;
		} else {
			if (this.blocksToRemove.contains(pLevel.getBlockState(pPos).getBlock())) {
				for (Direction direction : Direction.values()) {
					if (pLevel.getBlockState(pPos.relative(direction)).getBlock() == Blocks.AIR)
						return true;
				}
			}
			return false;
		}
	}

}
