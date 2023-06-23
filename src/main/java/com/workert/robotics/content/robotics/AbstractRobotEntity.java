package com.workert.robotics.content.robotics;

import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;
import com.workert.robotics.base.registries.ItemRegistry;
import com.workert.robotics.base.roboscript.RoboScript;
import com.workert.robotics.base.roboscript.RoboScriptArray;
import com.workert.robotics.base.roboscript.ingame.CompoundTagEnvironmentConversionHelper;
import com.workert.robotics.base.roboscript.ingame.LineLimitedString;
import com.workert.robotics.helpers.CodeHelper;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRobotEntity extends PathfinderMob implements InventoryCarrier {
	private static final int maxAir = BackTankUtil.maxAirWithoutEnchants() * 10;
	private int air;
	private final SimpleContainer inventory = new SimpleContainer(9);
	private CodeHelper.RobotFrequencyEntry robotFrequencyEntry = null;


	private final RoboScript roboScript;
	private String script = "";

	public static final int TERMINAL_LINE_LIMIT = 2048;
	private LineLimitedString terminal = new LineLimitedString(TERMINAL_LINE_LIMIT);


	public AbstractRobotEntity(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.air = maxAir;
		if (this.isProgrammable()) {
			this.roboScript = new RoboScript() {

				@Override
				public void print(String message) {
					AbstractRobotEntity.this.terminal.addLine(message);
					AbstractRobotEntity.this.saveWithoutId(new CompoundTag());
				}

				@Override
				public void reportCompileError(String error) {
					AbstractRobotEntity.this.terminal.addLine(error);
					AbstractRobotEntity.this.saveWithoutId(new CompoundTag());
				}

				@Override
				public void defineDefaultFunctions() {
					this.defineFunction("getXPos", 0,
							(interpreter, arguments) -> AbstractRobotEntity.this.position().x);
					this.defineFunction("getYPos", 0,
							(interpreter, arguments) -> AbstractRobotEntity.this.position().y);
					this.defineFunction("getZPos", 0,
							(interpreter, arguments) -> AbstractRobotEntity.this.position().z);
					this.defineFunction("getAirSupply", 0, (interpreter, arguments) -> AbstractRobotEntity.this.air);
					this.defineFunction("getMaxAirSupply", 0,
							(interpreter, arguments) -> AbstractRobotEntity.this.getMaxAirSupply());
					this.defineFunction("getInventorySupply", 0,
							(interpreter, arguments) -> {
								List<Object> itemList = new ArrayList<>();
								for (int inventoryIndex = 0; inventoryIndex < AbstractRobotEntity.this.inventory.getContainerSize(); inventoryIndex++) {
									ItemStack itemStack = AbstractRobotEntity.this.inventory.getItem(inventoryIndex);
									if (itemStack.isEmpty()) continue;
									itemList.add(new RoboScriptArray(
											List.of(ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString(),
													itemStack.getHoverName().getString(),
													(double) itemStack.getCount())));
								}
								return new RoboScriptArray(itemList);
							});
					super.defineDefaultFunctions();
				}
			};
		} else {
			this.roboScript = null;
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

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.putInt("Air", this.air);
		if (this.getInventory() != null) pCompound.put("Inventory", this.inventory.createTag());
		pCompound.putString("Script", this.script);
		pCompound.putString("Terminal", this.terminal.getString());
		if (this.isProgrammable()) {
			pCompound.put("Memory",
					CompoundTagEnvironmentConversionHelper.valuesToTag(this.roboScript.getPersistentVariables()));
		}
		super.addAdditionalSaveData(pCompound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		try {
			this.air = pCompound.getInt("Air");
			if (this.getInventory() != null) this.inventory.fromTag(pCompound.getList("Inventory", 10));
			this.script = pCompound.getString("Script");
			this.terminal = new LineLimitedString(TERMINAL_LINE_LIMIT, pCompound.getString("Terminal"));
			if (this.isProgrammable()) {
				this.roboScript.putVariables(
						CompoundTagEnvironmentConversionHelper.valuesFromCompoundTag(
								pCompound.getCompound("Memory")));
			}
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

	@Override
	protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		if (pPlayer.getItemInHand(pHand)
				.is(com.simibubi.create.AllItems.WRENCH.get().asItem()) && pPlayer.isCrouching()) {
			ItemStack stack = new ItemStack(this.getRobotItem());
			CompoundTag saveTag = new CompoundTag();
			this.save(saveTag);
			stack.getOrCreateTag().put("savedRobot", saveTag);
			stack.setHoverName(this.getCustomName());
			pPlayer.getInventory().add(stack);
			this.discard();
		} else if (this.isProgrammable() && pPlayer.getItemInHand(pHand)
				.is(com.simibubi.create.AllItems.WRENCH.get().asItem()) && !pPlayer.isCrouching()) {
			if (!this.level.isClientSide) this.roboScript.runString(this.script);
			return InteractionResult.SUCCESS;
		} /*else if (this.isProgrammable() && pPlayer.isCrouching()) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
					() -> () -> ScreenOpener.open(new ConsoleScreen(this.roboScript)));
		}*/ else if (this.isProgrammable() && pPlayer.getItemInHand(pHand)
				.is(ItemRegistry.PROGRAM.get()) && !pPlayer.isCrouching()) {
			if (!this.level.isClientSide)
				this.script = pPlayer.getItemInHand(pHand).getOrCreateTag().getString("code");
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
		}*/ else if (this.getInventory() != null && !pPlayer.isCrouching()) {
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
				Couple.create(RedstoneLinkNetworkHandler.Frequency.EMPTY,
						RedstoneLinkNetworkHandler.Frequency.EMPTY),
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
		return null;
	}

	@Override
	public boolean wantsToPickUp(ItemStack pStack) {
		return this.getInventory() != null && this.inventory.canAddItem(pStack);
	}

	@Override
	public boolean canPickUpLoot() {
		return this.getInventory() != null;
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
