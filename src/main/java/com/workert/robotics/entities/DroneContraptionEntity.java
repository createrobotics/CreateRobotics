package com.workert.robotics.entities;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.workert.robotics.lists.EntityList;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DroneContraptionEntity extends OrientedContraptionEntity {

	public DroneContraptionEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	public static DroneContraptionEntity create(Level world, Contraption contraption) {
		DroneContraptionEntity entity = new DroneContraptionEntity(EntityList.DRONE.get(), world);
		entity.setContraption(contraption);
		return entity;
	}

	@Override
	public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements,
			boolean pTeleport) {
	}

	@Override
	public boolean canCollideWith(Entity e) {
		return false;
	}

}
