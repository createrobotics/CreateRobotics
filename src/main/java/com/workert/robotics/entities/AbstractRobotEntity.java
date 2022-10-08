package com.workert.robotics.entities;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class AbstractRobotEntity extends PathfinderMob implements OwnableEntity {
	Player owner;

	public AbstractRobotEntity(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
			MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {
		if (pSpawnData instanceof OwnerSpawnGroupData)
			this.owner = ((OwnerSpawnGroupData) pSpawnData).owner;
		return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.putUUID("Owner", this.owner.getUUID());
		super.addAdditionalSaveData(pCompound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		this.owner = this.level.getPlayerByUUID(pCompound.getUUID("Owner"));
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

	public static class OwnerSpawnGroupData implements SpawnGroupData {
		public final Player owner;

		public OwnerSpawnGroupData(Player owner) {
			this.owner = owner;
		}
	}
}
