package com.workert.robotics.entities;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class AbstractRobotEntity extends PathfinderMob implements OwnableEntity {
	public Player owner;

	public AbstractRobotEntity(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
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
}
