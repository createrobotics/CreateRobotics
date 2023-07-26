package com.workert.robotics.unused.drone;
import com.workert.robotics.content.robotics.AbstractRobotEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class DroneEntity extends AbstractRobotEntity implements FlyingAnimal {

	private final SimpleContainer inventory = new SimpleContainer(9);

	public int last_chunk_x;
	public int last_chunk_z;


	public DroneEntity(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.setNoGravity(true);
		this.last_chunk_x = this.chunkPosition().x;
		this.last_chunk_z = this.chunkPosition().z;
	}

	@Override
	public Item getRobotItem() {
		return Items.ACACIA_BOAT; //this is temporary
	}

	@Override
	public boolean isProgrammable() {
		return true;
	}

	@Override
	public boolean isFlying() {
		return true;
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
}
