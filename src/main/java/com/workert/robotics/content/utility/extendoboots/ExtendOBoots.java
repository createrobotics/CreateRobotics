package com.workert.robotics.content.utility.extendoboots;

import com.workert.robotics.base.lists.EntityList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ExtendOBoots extends LivingEntity {
	private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(ExtendOBoots.class,
			EntityDataSerializers.FLOAT);

	public ExtendOBoots(EntityType<?> pEntityType, Level pLevel) {
		super(EntityList.EXTEND_O_BOOTS.get(), pLevel);
	}

	@Override
	public void tick() {
		super.tick();
		Player nearestPlayer = this.level.getNearestPlayer(this, ExtendOBootsItem.MAX_HEIGHT + 1);
		if (nearestPlayer == null) this.discard();
		else {
			if (nearestPlayer.position()
					.distanceTo(this.position().with(Direction.Axis.Y, nearestPlayer.position().y)) > 0.2)
				this.discard();
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HEIGHT, 0.5f);
	}

	public float getHeight() {
		return this.entityData.get(HEIGHT);
	}

	public void setHeight(float height) {
		this.entityData.set(HEIGHT, height);
	}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return List.of();
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlot pSlot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
	}

	@Override
	public HumanoidArm getMainArm() {
		return null;
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		return pSource.isBypassInvul();
	}

	@Override
	public void animateHurt() {
	}

	@Override
	public boolean canBeAffected(MobEffectInstance pPotioneffect) {
		return false;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
		return false;
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return this.getBoundingBox().expandTowards(1, 8, 1);
	}
}
