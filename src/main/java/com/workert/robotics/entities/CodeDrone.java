package com.workert.robotics.entities;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;

public class CodeDrone extends AbstractRobotEntity implements FlyingAnimal {
	private final SimpleContainer inventory = new SimpleContainer(9);

	public String code = "";

	public CodeDrone(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.moveControl = new FlyingMoveControl(this, 128, true);
	}

	@Override
	public boolean isFlying() {
		return !this.isOnGround();
	}

	public static AttributeSupplier createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.MAX_HEALTH, 1.0D)
				.add(Attributes.FLYING_SPEED, 0.8F).build();
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
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

	@Override
	public boolean isProgrammable() {
		return true;
	}

	@Override
	public boolean hasInventory() {
		return true;
	}

}
