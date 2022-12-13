package com.workert.robotics.entities;

import java.util.UUID;

import com.simibubi.create.content.curiosities.armor.BackTankUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractRobotEntity extends PathfinderMob implements OwnableEntity {
	public Player owner;

	private int air;
	public AbstractRobotEntity(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.air = BackTankUtil.maxAirWithoutEnchants() * 10;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
			MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {
		this.owner = pLevel.getNearestPlayer(this, 100); // This is just temporarily until there is a proper spawning system. TODO Spawning system
		return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (pSource == DamageSource.GENERIC || pSource == DamageSource.OUT_OF_WORLD)
			return super.hurt(pSource, pAmount);
		this.consumeAir((int) pAmount * 20);
		return false;
	}

	public static AttributeSupplier createAttributes;

	@Override
	public void animateHurt() {
	}

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		if (this.owner == null)
			this.owner = this.level.getNearestPlayer(this, 100);
		if (this.owner != null)
			pCompound.putUUID("Owner", this.owner.getUUID());
		pCompound.putInt("Air", this.air);
		super.addAdditionalSaveData(pCompound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		this.owner = this.level.getPlayerByUUID(pCompound.getUUID("Owner"));
		this.air = pCompound.getInt("Air");
		super.readAdditionalSaveData(pCompound);
	}

	@Override
	public UUID getOwnerUUID() {
		return this.owner.getUUID();
	}

	@Override
	public Entity getOwner() {
		return this.owner;
	}

	public int getAir() {
		return this.air;
	}

	public void consumeAir(int amount) {
		this.air = Math.max(this.air -= amount, 0);
		if (this.air <= 0)
			this.navigation.stop();
	}

	@Override
	public void tick() {
		if (this.isPathFinding())
			this.consumeAir(1);
		if (this.air <= 0) {
			this.navigation.stop();
			this.lookControl.setLookAt(this);
		}
		super.tick();
	}

	@Override
	public boolean requiresCustomPersistence() {
		return true;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance pPotioneffect) {
		return false;
	}

	@Override
	public boolean canBeLeashed(Player pPlayer) {
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
