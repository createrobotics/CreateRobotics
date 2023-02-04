package com.workert.robotics.entities;

import com.workert.robotics.lists.ItemList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class Miner extends AbstractRobotEntity {

	private final SimpleContainer inventory = new SimpleContainer(36);

	public Miner(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
	}

	public static AttributeSupplier createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.MAX_HEALTH, 1.0D).build();
	}

	@Override
	protected void registerGoals() {
		//this.goalSelector.addGoal(1, new RobotFollowPlayerOwnerGoal(this, 1.2, 16, 5));
	}

	@Override
	public void calculateEntityAnimation(LivingEntity p_21044_, boolean p_21045_) {
		super.calculateEntityAnimation(p_21044_, p_21045_);
	}

	@Override
	public void remove(RemovalReason pReason) {
		ServerLevel crashlol = (ServerLevel) this.level;
		super.remove(pReason);
	}

	@Override
	public boolean canPickUpLoot() {
		return true;
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

	@Override
	public Item getRobotItem() {
		return ItemList.MINER.get();
	}

	@Override
	public boolean isProgrammable() {
		return false;
	}

	@Override
	public boolean hasInventory() {
		return true;
	}
}
