package com.workert.robotics.item;

import java.util.Objects;

import com.workert.robotics.entities.AbstractRobotEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class AbstractRobotItem extends Item {
	EntityType<? extends AbstractRobotEntity> robot;

	public AbstractRobotItem(Properties pProperties, EntityType<? extends AbstractRobotEntity> robot) {
		super(pProperties);
		this.robot = robot;
	}

	@Override
	public void readShareTag(ItemStack stack, CompoundTag nbt) {
		this.robot = (EntityType<? extends AbstractRobotEntity>) EntityType.byString(nbt.getString("Robot"))
				.orElseThrow();
		super.readShareTag(stack, nbt);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		nbt.putString("Robot", EntityType.getKey(this.robot).toString());
		return super.initCapabilities(stack, nbt);
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

			EntityType<?> robot = this.getType(itemstack.getTag());
			if (robot.spawn((ServerLevel) level, itemstack, pContext.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG,
					true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
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
			} else if (pLevel.mayInteract(pPlayer, blockpos)
					&& pPlayer.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
				EntityType<?> robot = this.getType(itemstack.getTag());
				if (robot.spawn((ServerLevel) pLevel, itemstack, pPlayer, blockpos, MobSpawnType.SPAWN_EGG, false,
						false) == null) {
					return InteractionResultHolder.pass(itemstack);
				} else {
					if (!pPlayer.getAbilities().instabuild)
						itemstack.shrink(1);

					pPlayer.awardStat(Stats.ITEM_USED.get(this));
					pLevel.gameEvent(GameEvent.ENTITY_PLACE, pPlayer);
					return InteractionResultHolder.consume(itemstack);
				}
			} else {
				return InteractionResultHolder.fail(itemstack);
			}
		}
	}

}
