package com.workert.robotics.entities;

import com.workert.robotics.lists.EntityList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ExtendOBoots extends LivingEntity {
	public static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(ExtendOBoots.class,
			EntityDataSerializers.FLOAT);

	public ExtendOBoots(EntityType<?> pEntityType, Level pLevel) {
		super(EntityList.EXTEND_O_BOOTS.get(), pLevel);
	}

	public static AttributeSupplier createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0F).add(Attributes.MAX_HEALTH, 1.0D).build();
	}

	@Override
	public void tick() {
		super.tick();
		// TODO make ExtendOBoots dissapear automatically
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HEIGHT, 0f);
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

}
