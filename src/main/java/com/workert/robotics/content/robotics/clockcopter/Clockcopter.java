package com.workert.robotics.content.robotics.clockcopter;

import com.workert.robotics.base.registries.ItemRegistry;
import com.workert.robotics.content.robotics.AbstractRobotEntity;
import com.workert.robotics.content.robotics.clockcopter.goals.MineBlockAndDropGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class Clockcopter extends AbstractRobotEntity implements FlyingAnimal {

	public Clockcopter(EntityType<? extends AbstractRobotEntity> entity, Level world) {
		super(entity, world);
		this.moveControl = new FlyingMoveControl(this, 10, false);
	}

	@Override
	public boolean isFlying() {
		return !this.isOnGround();
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
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

	@Override
	public Item getRobotItem() {
		return ItemRegistry.CLOCKCOPTER.get();
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
