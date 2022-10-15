package com.workert.robotics.entities.goals;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

public class MineBlockAndDropGoal extends MoveToBlockGoal {

	private Block blockToRemove;
	private PathfinderMob mob;
	private int ticksSinceReachedGoal;

	public MineBlockAndDropGoal(PathfinderMob pMob, Block blockToRemove, double pSpeedModifier, int pSearchRange,
			int pVerticalSearchRange) {
		super(pMob, pSpeedModifier, pSearchRange, pVerticalSearchRange);
		this.blockToRemove = blockToRemove;
		this.mob = pMob;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
	}

	@Override
	public void start() {
		super.start();
		this.ticksSinceReachedGoal = 0;
	}

	// Tick method copied from RemoveBlockGoal and edited for block drop
	@Override
	public void tick() {
		super.tick();
		Level level = this.mob.level;
		BlockPos mineBlockPos = this.mob.blockPosition();
		BlockPos targetPos = this.getPosWithBlock(mineBlockPos, level);
		Random random = this.mob.getRandom();
		if (targetPos != null && this.mob.distanceToSqr(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < 3) {

			this.mob.getLookControl().setLookAt(targetPos.getX(), targetPos.getY(), targetPos.getZ());

			if (this.ticksSinceReachedGoal > 60) {
				level.destroyBlock(this.blockPos, true);
				if (!level.isClientSide) {
					for (int i = 0; i < 20; ++i) {
						double d3 = random.nextGaussian() * 0.02D;
						double d1 = random.nextGaussian() * 0.02D;
						double d2 = random.nextGaussian() * 0.02D;
						((ServerLevel) level).sendParticles(ParticleTypes.POOF, targetPos.getX() + 0.5D,
								(double) targetPos.getY(), targetPos.getZ() + 0.5D, 1, d3, d1, d2, (double) 0.15F);
					}
				}
			}
			++this.ticksSinceReachedGoal;
		}
	}

	@Nullable
	private BlockPos getPosWithBlock(BlockPos pPos, BlockGetter pLevel) {
		if (pLevel.getBlockState(pPos).is(this.blockToRemove)) {
			return pPos;
		} else {
			BlockPos[] ablockpos = new BlockPos[] { pPos.below(), pPos.west(), pPos.east(), pPos.north(), pPos.south(),
					pPos.below().below() };

			for (BlockPos blockpos : ablockpos) {
				if (pLevel.getBlockState(blockpos).is(this.blockToRemove)) {
					return blockpos;
				}
			}

			return null;
		}
	}

	@Override
	protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
		ChunkAccess chunkaccess = pLevel.getChunk(SectionPos.blockToSectionCoord(pPos.getX()),
				SectionPos.blockToSectionCoord(pPos.getZ()), ChunkStatus.FULL, false);
		if (chunkaccess == null) {
			return false;
		} else {
			return chunkaccess.getBlockState(pPos).is(this.blockToRemove);
		}
	}

}
