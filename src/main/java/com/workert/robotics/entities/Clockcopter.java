package com.workert.robotics.entities;

import com.workert.robotics.entities.goals.MineBlockAndDropGoal;
import com.workert.robotics.lists.ItemList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class Clockcopter extends AbstractRobotEntity implements FlyingAnimal {
	private final SimpleContainer inventory = new SimpleContainer(13);

	public static final EntityDataAccessor<Boolean> IS_FLYING = SynchedEntityData.defineId(Clockcopter.class,
			EntityDataSerializers.BOOLEAN);

	public Clockcopter(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.moveControl = new FlyingMoveControl(this, 10, false);
	}

	@Override
	public boolean isFlying() {
		return !this.isOnGround();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_FLYING, false);
	}

	public static AttributeSupplier createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.MAX_HEALTH, 1.0D)
				.add(Attributes.FLYING_SPEED, 0.8F).build();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0,
				new MineBlockAndDropGoal(this, ForgeRegistries.BLOCKS.tags().getTag(Tags.Blocks.ORES).stream().toList(),
						0.9, 16, 4));
		// this.goalSelector.addGoal(1, new RobotFollowPlayerOwnerGoal(this, 1.2, 16, 5));
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
	public void calculateEntityAnimation(LivingEntity p_21044_, boolean p_21045_) {
		super.calculateEntityAnimation(p_21044_, p_21045_);
		this.entityData.set(IS_FLYING, this.isFlying());
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

	@Override
	public Item getRobotItem() {
		return ItemList.CLOCKCOPTER.get();
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
