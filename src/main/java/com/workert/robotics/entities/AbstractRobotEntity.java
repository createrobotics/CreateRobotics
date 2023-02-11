package com.workert.robotics.entities;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;
import com.workert.robotics.helpers.CodeHelper;
import com.workert.robotics.lists.ItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractRobotEntity extends PathfinderMob implements InventoryCarrier {
	private int air;

	private final SimpleContainer inventory = new SimpleContainer(9);

	public HashMap<String, Function<AbstractRobotEntity, String>> privateVariableLookupMap = new HashMap<>();
	public String code = "";
	private CodeHelper.RobotFrequencyEntry robotFrequencyEntry = null;

	public AbstractRobotEntity(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.air = BackTankUtil.maxAirWithoutEnchants() * 10;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {
		return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (pSource == DamageSource.GENERIC || pSource == DamageSource.OUT_OF_WORLD)
			return super.hurt(pSource, pAmount);
		this.consumeAir((int) pAmount * 2);
		return false;
	}

	public abstract boolean hasInventory();

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.putInt("Air", this.air);
		if (this.hasInventory()) pCompound.put("Inventory", this.inventory.createTag());
		pCompound.putString("Code", this.code);
		super.addAdditionalSaveData(pCompound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		try {
			this.air = pCompound.getInt("Air");
			if (this.hasInventory()) this.inventory.fromTag(pCompound.getList("Inventory", 10));
			this.code = pCompound.getString("Code");
			super.readAdditionalSaveData(pCompound);
		} catch (NullPointerException exception) {
			exception.printStackTrace();
		}
	}

	public int getAir() {
		return this.air;
	}

	public void consumeAir(int amount) {
		this.air = Math.max(this.air - amount, 0);
	}

	@Override
	public void tick() {
		if (this.isPathFinding()) this.consumeAir(1);
		/*if (this.air <= 0) {
			this.navigation.stop();
			this.lookControl.setLookAt(this);
		}*/
		super.tick();
	}

	public abstract Item getRobotItem();

	public abstract boolean isProgrammable();

	public CodeHelper.RobotFrequencyEntry getRobotFrequencyEntry() {
		if (!this.isProgrammable())
			return null;
		if (this.robotFrequencyEntry == null)
			this.robotFrequencyEntry = new CodeHelper.RobotFrequencyEntry(this, Couple.create(
					RedstoneLinkNetworkHandler.Frequency.EMPTY, RedstoneLinkNetworkHandler.Frequency.EMPTY), 0);
		return this.robotFrequencyEntry;
	}

	@Override
	protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		if (pPlayer.getItemInHand(pHand)
				.is(AllItems.WRENCH.get().asItem()) && pPlayer.isCrouching()) {
			ItemStack stack = new ItemStack(this.getRobotItem());
			CompoundTag saveTag = new CompoundTag();
			this.save(saveTag);
			stack.getOrCreateTag().put("savedRobot", saveTag);
			stack.setHoverName(this.getCustomName());
			pPlayer.getInventory().add(stack);
			this.discard();
		} else if (this.isProgrammable() && pPlayer.getItemInHand(pHand)
				.is(AllItems.WRENCH.get().asItem()) && !pPlayer.isCrouching()) {
			if (!this.level.isClientSide)
				CompletableFuture.runAsync(() -> CodeHelper.runCode(this, this.code));
			return InteractionResult.SUCCESS;
		} else if (this.isProgrammable() && pPlayer.getItemInHand(pHand)
				.is(ItemList.PROGRAM.get()) && !pPlayer.isCrouching()) {
			if (!this.level.isClientSide)
				this.code = pPlayer.getItemInHand(pHand).getOrCreateTag().getString("code");
			return InteractionResult.SUCCESS;
		} else if (this.hasInventory() && !pPlayer.isCrouching()) {
			pPlayer.openMenu(new SimpleMenuProvider(new MenuConstructor() {

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
					return new ChestMenu(MenuType.GENERIC_3x3, id, playerInventory, AbstractRobotEntity.this.inventory,
							1);
				}
			}, this.getDisplayName()));

			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(pPlayer, pHand);
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
	public SimpleContainer getInventory() {
		return this.hasInventory() ? this.inventory : null;
	}

	@Override
	public boolean wantsToPickUp(ItemStack pStack) {
		return this.hasInventory() && this.inventory.canAddItem(pStack);
	}

	@Override
	public boolean canPickUpLoot() {
		return this.hasInventory();
	}

	@Override
	public boolean requiresCustomPersistence() {
		return true;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance pPotioneffect) {
		return false;
	}

	@Override
	public boolean canBeLeashed(Player pPlayer) {
		return false;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public void animateHurt() {
	}

	@Override
	public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
		return false;
	}

}
