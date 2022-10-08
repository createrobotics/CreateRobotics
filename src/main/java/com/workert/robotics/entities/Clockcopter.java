package com.workert.robotics.entities;

import java.util.UUID;

import com.workert.robotics.entities.goals.RobotFollowPlayerOwnerGoal;
import com.workert.robotics.entities.goals.RobotMineOreGoal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class Clockcopter extends PathfinderMob implements FlyingAnimal, OwnableEntity {

	public Clockcopter(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.moveControl = new FlyingMoveControl(this, 10, false);
	}

	@Override
	public boolean isFlying() {
		return !this.isOnGround();
	}

	public static AttributeSupplier createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.MAX_HEALTH, 6.0D)
				.add(Attributes.FLYING_SPEED, 0.8F).build();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new RobotMineOreGoal(this, Blocks.COBBLESTONE, 0.9, 12, 4));
		this.goalSelector.addGoal(2, new RobotFollowPlayerOwnerGoal(this, 1.2, 5, 5));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
		flyingpathnavigation.setCanOpenDoors(false);
		flyingpathnavigation.setCanFloat(true);
		flyingpathnavigation.setCanPassDoors(false);
		return flyingpathnavigation;
	}

	@Override
	public UUID getOwnerUUID() {
		return this.level.players().get(0).getUUID();
		// TODO make real owner selector
	}

	@Override
	public Entity getOwner() {
		return this.level.getPlayerByUUID(this.getOwnerUUID());
	}

}
