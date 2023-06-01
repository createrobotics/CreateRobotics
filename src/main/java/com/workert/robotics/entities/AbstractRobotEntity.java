package com.workert.robotics.entities;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.utility.Couple;
import com.workert.robotics.client.screens.ConsoleScreen;
import com.workert.robotics.helpers.CodeHelper;
import com.workert.robotics.lists.ItemList;
import com.workert.robotics.roboscript.RoboScript;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractRobotEntity extends PathfinderMob implements InventoryCarrier {
	private static final int maxAir = BackTankUtil.maxAirWithoutEnchants() * 10;
	private int air;

	private final SimpleContainer inventory = new SimpleContainer(9);

	private RoboScript roboScript = null;
	public String code = "";
	private static final EntityDataAccessor<String> DATA_CONSOLE_OUTPUT_ID = SynchedEntityData.defineId(
			AbstractRobotEntity.class, EntityDataSerializers.STRING);

	private CodeHelper.RobotFrequencyEntry robotFrequencyEntry = null;

	public AbstractRobotEntity(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.air = maxAir;
		if (this.isProgrammable()) {
			this.roboScript = new RoboScript() {
				@Override
				public void handleReportMessage(String message) {
					AbstractRobotEntity.this.setConsoleOutput(
							AbstractRobotEntity.this.getConsoleOutput().concat(message + "\n"));
				}
			};
			this.fillInDefaultRoboScriptFunctions(this.roboScript);
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {
		return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (pSource == DamageSource.OUT_OF_WORLD) return super.hurt(pSource, pAmount);
		this.consumeAir((int) pAmount * 2);
		return false;
	}

	public abstract boolean hasInventory();

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.putInt("Air", this.air);
		if (this.hasInventory()) pCompound.put("Inventory", this.inventory.createTag());
		pCompound.putString("Code", this.code);
		pCompound.putString("ConsoleOutput", this.getConsoleOutput());
		super.addAdditionalSaveData(pCompound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		try {
			this.air = pCompound.getInt("Air");
			if (this.hasInventory()) this.inventory.fromTag(pCompound.getList("Inventory", 10));
			this.code = pCompound.getString("Code");
			this.setConsoleOutput(pCompound.getString("ConsoleOutput"));
			super.readAdditionalSaveData(pCompound);
		} catch (NullPointerException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_CONSOLE_OUTPUT_ID, "");
	}

	public int getAir() {
		return this.air;
	}

	public String getConsoleOutput() {
		return this.entityData.get(this.DATA_CONSOLE_OUTPUT_ID);
	}

	public void setConsoleOutput(String consoleOutput) {
		if (this.getConsoleOutput().length() > 4096)
			this.setConsoleOutput(this.getConsoleOutput().substring(this.getConsoleOutput().length() - 4096));
		else this.entityData.set(this.DATA_CONSOLE_OUTPUT_ID, consoleOutput);
	}

	public void consumeAir(int amount) {
		this.air = Math.max(this.air - amount, 0);
	}

	@Override
	public void tick() {
		if (this.isPathFinding()) this.consumeAir(1);
		if (this.air <= 0) {
			if (this.air < 0) this.air = 0;
			this.doWaterSplashEffect();
			this.navigation.stop();
			this.lookControl.setLookAt(this);
		}
		super.tick();
	}

	public abstract Item getRobotItem();

	public abstract boolean isProgrammable();

	public void fillInDefaultRoboScriptFunctions(RoboScript roboScript) {
		roboScript.defineFunction("getXPos", 0, (interpreter, arguments) -> AbstractRobotEntity.this.getX());
		roboScript.defineFunction("getYPos", 0, (interpreter, arguments) -> AbstractRobotEntity.this.getY());
		roboScript.defineFunction("getZPos", 0, (interpreter, arguments) -> AbstractRobotEntity.this.getZ());
		roboScript.defineFunction("getAir", 0, (interpreter, arguments) -> AbstractRobotEntity.this.air);
		roboScript.defineFunction("getMaxAir", 0, (interpreter, arguments) -> AbstractRobotEntity.maxAir);
		roboScript.defineFunction("print", 1, (interpreter, arguments) -> {
			AbstractRobotEntity.this.setConsoleOutput(
					AbstractRobotEntity.this.getConsoleOutput().concat(arguments.get(0).toString() + "\n"));
			return null;
		});
	}

	@Override
	protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		if (pPlayer.getItemInHand(pHand).is(AllItems.WRENCH.get().asItem()) && pPlayer.isCrouching()) {
			ItemStack stack = new ItemStack(this.getRobotItem());
			CompoundTag saveTag = new CompoundTag();
			this.save(saveTag);
			stack.getOrCreateTag().put("savedRobot", saveTag);
			stack.setHoverName(this.getCustomName());
			pPlayer.getInventory().add(stack);
			this.discard();
		} else if (this.isProgrammable() && pPlayer.getItemInHand(pHand)
				.is(AllItems.WRENCH.get().asItem()) && !pPlayer.isCrouching()) {
			if (!this.level.isClientSide) CompletableFuture.runAsync(() -> this.roboScript.run(this.code));
			return InteractionResult.SUCCESS;
		} else if (this.isProgrammable() && pPlayer.isCrouching()) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ScreenOpener.open(new ConsoleScreen(this)));
		} else if (this.isProgrammable() && pPlayer.getItemInHand(pHand)
				.is(ItemList.PROGRAM.get()) && !pPlayer.isCrouching()) {
			if (!this.level.isClientSide) this.code = pPlayer.getItemInHand(pHand).getOrCreateTag().getString("code");
			return InteractionResult.SUCCESS;
		}/* else if (this.isProgrammable() && (pPlayer.getItemInHand(pHand)
				.is(Items.WRITTEN_BOOK) || pPlayer.getItemInHand(pHand)
				.is(Items.WRITABLE_BOOK)) && !pPlayer.isCrouching()) {
			if (this.level.isClientSide) return InteractionResult.SUCCESS;
			WrittenBookItem.resolveBookComponents(pPlayer.getItemInHand(pHand),
					new CommandSourceStack(CommandSource.NULL, pPlayer.position(), Vec2.ZERO, (ServerLevel) this.level,
							2, pPlayer.getName().getString(), pPlayer.getDisplayName(), this.level.getServer(),
							pPlayer), pPlayer);
			CompoundTag compoundtag = pPlayer.getItemInHand(pHand).getOrCreateTag();
			this.code = "";
			compoundtag.getList("pages", 8).forEach(page -> {
				this.code = this.code.concat(page.getAsString());
			});
			this.code = this.code.replace("{\"text\":\"", "").replace("\"}", "\n").replace("\\n", "\n").trim();
			return InteractionResult.SUCCESS;
		}*/ else if (this.hasInventory() && !pPlayer.isCrouching()) {
			pPlayer.openMenu(new SimpleMenuProvider(
					(id, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_3x3, id, playerInventory,
							AbstractRobotEntity.this.inventory, 1), this.getDisplayName()));
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(pPlayer, pHand);
	}

	public CodeHelper.RobotFrequencyEntry getRobotFrequencyEntry() {
		if (!this.isProgrammable()) return null;
		if (this.robotFrequencyEntry == null) this.robotFrequencyEntry = new CodeHelper.RobotFrequencyEntry(this,
				Couple.create(RedstoneLinkNetworkHandler.Frequency.EMPTY, RedstoneLinkNetworkHandler.Frequency.EMPTY),
				0);
		return this.robotFrequencyEntry;
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

	@Nullable
	@Override
	public ItemStack getPickResult() {
		return this.getRobotItem().getDefaultInstance();
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

	@Override
	public boolean displayFireAnimation() {
		return false;
	}

	@Override
	public void die(DamageSource pDamageSource) {
		super.die(pDamageSource);
	}
}
