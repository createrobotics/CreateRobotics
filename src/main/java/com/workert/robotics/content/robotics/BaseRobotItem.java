package com.workert.robotics.content.robotics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;
import java.util.function.Supplier;

public class BaseRobotItem extends Item {
	private Supplier<? extends EntityType<? extends AbstractRobotEntity>> entity;

	public BaseRobotItem(Properties pProperties) {
		super(pProperties);
	}

	public void setEntity(Supplier<? extends EntityType<? extends AbstractRobotEntity>> entity) {
		this.entity = entity;
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		Level level = pContext.getLevel();
		if (!(level instanceof ServerLevel)) {
			return InteractionResult.SUCCESS;
		} else {
			ItemStack itemstack = pContext.getItemInHand();
			BlockPos blockpos = pContext.getClickedPos();
			Direction direction = pContext.getClickedFace();
			BlockState blockstate = level.getBlockState(blockpos);

			BlockPos blockpos1;
			if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
				blockpos1 = blockpos;
			} else {
				blockpos1 = blockpos.relative(direction);
			}

			EntityType<?> entitytype = this.getType(itemstack.getTag());
			Entity entity = entitytype.spawn((ServerLevel) level, itemstack, pContext.getPlayer(), blockpos1,
					MobSpawnType.TRIGGERED, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
			if (entity != null) {
				CompoundTag savedCompound = itemstack.getOrCreateTag().getCompound("savedRobot");
				if (!savedCompound.isEmpty()) entity.load(savedCompound);

				entity.teleportTo(blockpos1.getX() + 0.5, blockpos1.getY(), blockpos1.getZ() + 0.5);

				itemstack.shrink(1);
				level.gameEvent(pContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		HitResult hitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
		if (hitresult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemstack);
		} else if (!(pLevel instanceof ServerLevel)) {
			return InteractionResultHolder.success(itemstack);
		} else {
			BlockHitResult blockhitresult = (BlockHitResult) hitresult;
			BlockPos blockpos = blockhitresult.getBlockPos();
			if (!(pLevel.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
				return InteractionResultHolder.pass(itemstack);
			} else if (pLevel.mayInteract(pPlayer, blockpos) && pPlayer.mayUseItemAt(blockpos,
					blockhitresult.getDirection(), itemstack)) {
				EntityType<?> entitytype = this.getType(itemstack.getTag());
				Entity entity = entitytype.spawn((ServerLevel) pLevel, itemstack, pPlayer, blockpos,
						MobSpawnType.TRIGGERED, false, false);
				if (entity == null) {
					return InteractionResultHolder.pass(itemstack);
				} else {
					CompoundTag savedCompound = itemstack.getOrCreateTag().getCompound("savedRobot");
					if (!savedCompound.isEmpty()) entity.load(savedCompound);

					entity.teleportTo(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5);

					if (!pPlayer.getAbilities().instabuild) {
						itemstack.shrink(1);
					}

					pPlayer.awardStat(Stats.ITEM_USED.get(this));
					pLevel.gameEvent(pPlayer, GameEvent.ENTITY_PLACE, entity.position());
					return InteractionResultHolder.consume(itemstack);
				}
			} else {
				return InteractionResultHolder.fail(itemstack);
			}
		}
	}

	private EntityType<?> getType(CompoundTag tag) {
		return this.entity.get();
	}
}
