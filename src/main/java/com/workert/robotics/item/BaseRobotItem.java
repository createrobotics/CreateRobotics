package com.workert.robotics.item;

import org.jetbrains.annotations.NotNull;

import com.workert.robotics.entities.AbstractRobotEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class BaseRobotItem extends Item {
	EntityType<? extends AbstractRobotEntity> robot;

	public BaseRobotItem(Properties pProperties, @NotNull EntityType<? extends AbstractRobotEntity> robot) {
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
				AbstractRobotEntity spawnedRobot = (AbstractRobotEntity) this.robot.spawn((ServerLevel) pLevel,
						itemstack, pPlayer, blockpos, MobSpawnType.SPAWN_EGG, false, false);
				spawnedRobot.owner = pPlayer;
				if (!pPlayer.getAbilities().instabuild)
					itemstack.shrink(1);

				pPlayer.awardStat(Stats.ITEM_USED.get(this));
				pLevel.gameEvent(GameEvent.ENTITY_PLACE, pPlayer);
				return InteractionResultHolder.consume(itemstack);
			} else {
				return InteractionResultHolder.fail(itemstack);
			}
		}
	}

}
