package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.workert.robotics.content.robotics.AbstractRobotEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class FlyingToolbox extends AbstractRobotEntity {
	Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers;

	FakeToolboxTileEntity fakeToolboxTileEntity;

	public FlyingToolbox(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.connectedPlayers = new HashMap<>();
		this.fakeToolboxTileEntity = new FakeToolboxTileEntity(this);
		this.fakeToolboxTileEntity.setLevel(this.level);
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.fakeToolboxTileEntity.isFullyInitialized()) {
			this.fakeToolboxTileEntity.setUniqueId(UUID.randomUUID());
			this.fakeToolboxTileEntity.initialize();
		}

		ToolboxHandler.onUnload(this.fakeToolboxTileEntity);
		ToolboxHandler.onLoad(this.fakeToolboxTileEntity);

		if (!this.level.isClientSide) {
			this.fakeToolboxTileEntity.tick();
		}
	}

	@Override
	public void die(DamageSource pDamageSource) {
		super.die(pDamageSource);
		this.fakeToolboxTileEntity.invalidate();
		this.fakeToolboxTileEntity = null;
	}

	public DyeColor getColor() {
		return DyeColor.BROWN; // TODO
	}

	void sendData() {
		this.saveWithoutId(new CompoundTag());
	}

	@Override
	public Item getRobotItem() {
		return null;
	}

	@Override
	public boolean isProgrammable() {
		return false;
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}
}
