package com.workert.robotics.entities;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.workert.robotics.client.screens.CodeDroneScreen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CodeDrone extends AbstractRobotEntity implements FlyingAnimal, InventoryCarrier {
	private final SimpleContainer inventory = new SimpleContainer(9);

	public String droneCode = "";

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
	public Container getInventory() {
		return this.inventory;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.put("Inventory", this.inventory.createTag());
		pCompound.putString("Code", this.droneCode);
		super.addAdditionalSaveData(pCompound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		this.inventory.fromTag(pCompound.getList("Inventory", 10));
		this.droneCode = pCompound.getString("Code");
		super.readAdditionalSaveData(pCompound);
	}

	@Override
	protected void pickUpItem(ItemEntity pItemEntity) {
		ItemStack itemstack = pItemEntity.getItem();
		if (this.wantsToPickUp(itemstack)) {
			this.onItemPickup(pItemEntity);
			ItemStack itemstack1 = this.inventory.addItem(itemstack);
			this.take(pItemEntity, 64 - itemstack1.getCount());
			if (itemstack1.isEmpty()) {
				pItemEntity.discard();
			} else {
				itemstack.setCount(itemstack1.getCount());
			}
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (player.getItemInHand(hand).getItem().equals(AllItems.WRENCH.get().asItem()) && !player.isCrouching()) {
			this.droneCode.lines().forEach(command -> {
				command = command.replace(" ", "");
				if (command.startsWith("robot.goTo(")) {
					String[] coordinateList = command.replace("robot.goTo(", "").replace(")", "").split(",");
					try {
						CodeDrone.this.navigation.moveTo(Double.valueOf(coordinateList[0]),
								Double.valueOf(coordinateList[1]), Double.valueOf(coordinateList[2]), 1);
					} catch (Exception exception) {
						throw new IllegalArgumentException(
								"\"robot.goTo\" takes three arguments from the type \"Double\".\nException message: \""
										+ exception.getLocalizedMessage() + "\"");
					}
				}
			});
			return InteractionResult.SUCCESS;
		} else if (player.getItemInHand(hand).getItem().equals(Items.REDSTONE) && !player.isCrouching()) {
			ScreenOpener.open(new CodeDroneScreen(this));
			return InteractionResult.SUCCESS;
		}

		player.openMenu(new SimpleMenuProvider(new MenuConstructor() {

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
				return new ChestMenu(MenuType.GENERIC_3x3, id, playerInventory, CodeDrone.this.inventory, 1);
			}
		}, this.getDisplayName()));

		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean wantsToPickUp(ItemStack pStack) {
		return this.inventory.canAddItem(pStack);
	}

	@Override
	public boolean canPickUpLoot() {
		return true;
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

}
