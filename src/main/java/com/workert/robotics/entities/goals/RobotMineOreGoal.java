package com.workert.robotics.entities.goals;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

public class RobotMineOreGoal extends MoveToBlockGoal {

	private Block blockToRemove;
	private PathfinderMob removerMob;
	private int ticksSinceReachedGoal;

	public RobotMineOreGoal(PathfinderMob pMob, Block blockToRemove, double pSpeedModifier, int pSearchRange,
			int pVerticalSearchRange) {
		super(pMob, pSpeedModifier, pSearchRange, pVerticalSearchRange);
		this.blockToRemove = blockToRemove;
		this.removerMob = pMob;
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
		Level level = this.removerMob.level;
		BlockPos blockpos = this.removerMob.blockPosition();
		BlockPos blockpos1 = this.getPosWithBlock(blockpos, level);
		Random random = this.removerMob.getRandom();
		if (this.isReachedTarget() && blockpos1 != null) {
			if (this.ticksSinceReachedGoal > 60) {
				// TODO fix block not breaking
				level.destroyBlock(this.blockPos, true);
				if (!level.isClientSide) {
					for (int i = 0; i < 20; ++i) {
						double d3 = random.nextGaussian() * 0.02D;
						double d1 = random.nextGaussian() * 0.02D;
						double d2 = random.nextGaussian() * 0.02D;
						((ServerLevel) level).sendParticles(ParticleTypes.POOF, blockpos1.getX() + 0.5D,
								(double) blockpos1.getY(), blockpos1.getZ() + 0.5D, 1, d3, d1, d2, (double) 0.15F);
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
