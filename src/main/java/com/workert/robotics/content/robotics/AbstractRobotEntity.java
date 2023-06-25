package com.workert.robotics.content.robotics;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerHandler;
import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;
import com.workert.robotics.base.registries.ItemRegistry;
import com.workert.robotics.base.roboscript.RoboScript;
import com.workert.robotics.base.roboscript.RoboScriptArray;
import com.workert.robotics.base.roboscript.RoboScriptRuntimeError;
import com.workert.robotics.base.roboscript.ingame.CompoundTagEnvironmentConversionHelper;
import com.workert.robotics.base.roboscript.ingame.LineLimitedString;
import com.workert.robotics.unused.CodeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
							(interpreter, arguments, errorToken) -> AbstractRobotEntity.this.position().x);
					this.defineFunction("getYPos", 0,
							(interpreter, arguments, errorToken) -> AbstractRobotEntity.this.position().y);
					this.defineFunction("getZPos", 0,
							(interpreter, arguments, errorToken) -> AbstractRobotEntity.this.position().z);

					this.defineFunction("getAirSupply", 0,
							(interpreter, arguments, errorToken) -> AbstractRobotEntity.this.air);
					this.defineFunction("getMaxAirSupply", 0,
							(interpreter, arguments, errorToken) -> AbstractRobotEntity.this.getMaxAirSupply());

					this.defineFunction("getInventory", 0,
							(interpreter, arguments, errorToken) -> {
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

					this.defineFunction("goTo", 3, (interpreter, arguments, errorToken) -> {
						AbstractRobotEntity robot = AbstractRobotEntity.this;

						if (!(arguments.get(0) instanceof Double xPos && arguments.get(
								1) instanceof Double yPos && arguments.get(2) instanceof Double zPos))
							throw new RoboScriptRuntimeError(errorToken,
									"The xPos, yPos and zPos arguments must be a number.");

						robot.getNavigation().moveTo(xPos, yPos, zPos, 1);

						int tryTimer = 0;
						while (robot.getNavigation().isInProgress()) {
							try {
								Thread.sleep(200);
								tryTimer++;
								if (tryTimer >= 10) {
									robot.getNavigation().recomputePath();
									tryTimer = 0;
								}
								if (robot.getNavigation().isStuck()) {
									interpreter.roboScriptInstance.print("Robot is stuck! Trying to recompute path.");
									robot.getNavigation().recomputePath();
								}
							} catch (InterruptedException exception) {
								exception.printStackTrace();
							}
						}
						robot.getNavigation().stop();
						robot.setDeltaMovement(robot.getDeltaMovement().x, 0, robot.getDeltaMovement().z);
						return null;
					});

					this.defineFunction("getItems", 4, (interpreter, arguments, errorToken) -> {
						AbstractRobotEntity robot = AbstractRobotEntity.this;

						if (!(arguments.get(0) instanceof Double xPos && arguments.get(
								1) instanceof Double yPos && arguments.get(2) instanceof Double zPos))
							throw new RoboScriptRuntimeError(errorToken,
									"The xPos, yPos and zPos arguments must be a number.");

						if (!(arguments.get(3) == null || arguments.get(3) instanceof String))
							throw new RoboScriptRuntimeError(errorToken,
									"The itemId argument must be a String or null.");

						BlockPos pos = new BlockPos(xPos, yPos, zPos);

						if (!pos.closerToCenterThan(robot.position(), 5))
							interpreter.roboScriptInstance.print("The container is too far away. Ignored the command.");

						if (robot.getLevel().getExistingBlockEntity(pos) == null)
							interpreter.roboScriptInstance.print(
									"The block at the specified coordinates has no tile entity (is no container). Ignored the command.");

						Item itemToGet = arguments.get(3) == null ? Items.AIR : Registry.ITEM.get(
								new ResourceLocation(((String) arguments.get(3)).split(":")[0],
										((String) arguments.get(3)).trim().split(":")[1]));

						robot.getLevel().getExistingBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER)
								.ifPresent(handler -> {
									for (int slot = 0; slot < handler.getSlots(); slot++) {
										while (!handler.getStackInSlot(slot).isEmpty()
												&& (itemToGet == Items.AIR
												|| itemToGet.equals(handler.getStackInSlot(slot).getItem()))
												&& robot.wantsToPickUp(handler.extractItem(slot, 1, true))) {
											robot.getInventory().addItem(handler.extractItem(slot, 1, false));
										}
									}
								});
						robot.getLevel().blockUpdated(pos, robot.getLevel().getBlockState(pos).getBlock());
						return null;
					});
					this.defineFunction("getItems", 4, (interpreter, arguments, errorToken) -> {
						AbstractRobotEntity robot = AbstractRobotEntity.this;

						if (!(arguments.get(0) instanceof Double xPos && arguments.get(
								1) instanceof Double yPos && arguments.get(2) instanceof Double zPos))
							throw new RoboScriptRuntimeError(errorToken,
									"The xPos, yPos and zPos arguments must be a number.");

						if (!(arguments.get(3) == null || arguments.get(3) instanceof String))
							throw new RoboScriptRuntimeError(errorToken,
									"The itemId argument must be a String or null.");

						BlockPos pos = new BlockPos(xPos, yPos, zPos);

						if (!pos.closerToCenterThan(robot.position(), 5))
							interpreter.roboScriptInstance.print("The container is too far away. Ignored the command.");

						if (robot.getLevel().getExistingBlockEntity(pos) == null)
							interpreter.roboScriptInstance.print(
									"The block at the specified coordinates has no tile entity (is no container). Ignored the command.");

						Item itemToPush = arguments.get(3) == null ? Items.AIR : Registry.ITEM.get(
								new ResourceLocation(((String) arguments.get(3)).split(":")[0],
										((String) arguments.get(3)).trim().split(":")[1]));

						robot.getLevel().getExistingBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER)
								.ifPresent(handler -> {
									for (int slot = 0; slot < robot.getInventory().getContainerSize(); slot++) {
										if (itemToPush.equals(Items.AIR) || (robot.getInventory()
												.countItem(itemToPush) > 0 && robot.getInventory().getItem(slot)
												.is(itemToPush))) {
											for (int containerSlot = 0; containerSlot < handler.getSlots(); containerSlot++) {
												robot.getInventory().setItem(slot,
														handler.insertItem(containerSlot,
																robot.getInventory().getItem(slot),
																false));
											}
										}
									}
								});
						robot.getLevel().blockUpdated(pos, robot.getLevel().getBlockState(pos).getBlock());
						return null;
					});

					this.defineFunction("punch", 5, (interpreter, arguments, errorToken) -> {
						AbstractRobotEntity robot = AbstractRobotEntity.this;

						if (!(arguments.get(0) instanceof Double xPos && arguments.get(
								1) instanceof Double yPos && arguments.get(2) instanceof Double zPos))
							throw new RoboScriptRuntimeError(errorToken,
									"The xPos, yPos and zPos arguments must be a number.");

						if (!(arguments.get(3) == null || arguments.get(3) instanceof String))
							throw new RoboScriptRuntimeError(errorToken,
									"The itemId argument must be a String or null.");

						if (!(arguments.get(4) == null || arguments.get(4) instanceof String))
							throw new RoboScriptRuntimeError(errorToken,
									"The direction argument must be a String or null.");

						BlockPos clickPos = new BlockPos(xPos, yPos, zPos);

						if (!clickPos.closerToCenterThan(robot.position(), 5))
							interpreter.roboScriptInstance.print(
									"The click position is too far away. Ignored the command.");

						Item item = arguments.get(3) == null ? Items.AIR : Registry.ITEM.get(
								new ResourceLocation(((String) arguments.get(3)).split(":")[0],
										((String) arguments.get(3)).trim().split(":")[1]));

						Direction direction = null;
						if (arguments.get(4) instanceof String directionString)
							direction = Direction.valueOf(directionString);

						try {
							click(robot, clickPos, direction, false, item);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return null;
					});
					this.defineFunction("use", 5, (interpreter, arguments, errorToken) -> {
						AbstractRobotEntity robot = AbstractRobotEntity.this;

						if (!(arguments.get(0) instanceof Double xPos && arguments.get(
								1) instanceof Double yPos && arguments.get(2) instanceof Double zPos))
							throw new RoboScriptRuntimeError(errorToken,
									"The xPos, yPos and zPos arguments must be a number.");

						if (!(arguments.get(3) == null || arguments.get(3) instanceof String))
							throw new RoboScriptRuntimeError(errorToken,
									"The itemId argument must be a String or null.");

						if (!(arguments.get(4) == null || arguments.get(4) instanceof String))
							throw new RoboScriptRuntimeError(errorToken,
									"The direction argument must be a String or null.");

						BlockPos clickPos = new BlockPos(xPos, yPos, zPos);

						if (!clickPos.closerToCenterThan(robot.position(), 5))
							interpreter.roboScriptInstance.print(
									"The click position is too far away. Ignored the command.");

						Item item = arguments.get(3) == null ? Items.AIR : Registry.ITEM.get(
								new ResourceLocation(((String) arguments.get(3)).split(":")[0],
										((String) arguments.get(3)).trim().split(":")[1]));

						Direction direction = null;
						if (arguments.get(4) instanceof String directionString)
							direction = Direction.valueOf(directionString);

						try {
							click(robot, clickPos, direction, true, item);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return null;
					});

					this.defineFunction("waitForRedstoneLink", 2, (interpreter, arguments, errorToken) -> {
						if (!(arguments.get(0) instanceof String itemId1))
							throw new RoboScriptRuntimeError(errorToken,
									"The first itemId argument must be a String.");

						if (!(arguments.get(1) == null || arguments.get(1) instanceof String))
							throw new RoboScriptRuntimeError(errorToken,
									"The second itemId argument must be a String or null.");

						Item item1 = Registry.ITEM.get(
								new ResourceLocation(((String) arguments.get(3)).split(":")[0],
										((String) arguments.get(3)).trim().split(":")[1]));

						if (item1 == Items.AIR)
							throw new RoboScriptRuntimeError(errorToken, "Invalid first itemId.");

						Item item2 = null;
						if (arguments.get(1) != null)
							item2 = Registry.ITEM.get(
									new ResourceLocation(((String) arguments.get(3)).split(":")[0],
											((String) arguments.get(3)).trim().split(":")[1]));

						if (item2 == Items.AIR)
							throw new RoboScriptRuntimeError(errorToken, "Invalid second itemId.");


						RedstoneLinkNetworkHandler.Frequency secondFrequency = RedstoneLinkNetworkHandler.Frequency.EMPTY;
						if (item2 != null)
							secondFrequency = RedstoneLinkNetworkHandler.Frequency.of(item2.getDefaultInstance());

						while (!Create.REDSTONE_LINK_NETWORK_HANDLER.hasAnyLoadedPower(
								Couple.create(RedstoneLinkNetworkHandler.Frequency.of(item1.getDefaultInstance()),
										secondFrequency))) {
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}
						return null;
					});
					super.defineDefaultFunctions();
				}
			};
		} else {
			this.roboScript = null;
		}
	}

	public static void click(AbstractRobotEntity robot, BlockPos posToClick, Direction direction, boolean use, @javax.annotation.Nullable Item itemToClickWith) throws ClassNotFoundException, NoSuchMethodException, InterruptedException, InvocationTargetException, IllegalAccessException {
		DeployerFakePlayer fakePlayer = new DeployerFakePlayer((ServerLevel) robot.getLevel());

		if (itemToClickWith != null) {
			if (robot.getInventory().countItem(itemToClickWith) <= 0) return;
			for (int slot = 0; slot < robot.getInventory().getContainerSize(); slot++) {
				if (robot.getInventory().getItem(slot).is(itemToClickWith)) {
					ItemStack stackToAddToPlayer = robot.getInventory().removeItem(slot, 1);
					if (stackToAddToPlayer.isEmpty()) break;
					stackToAddToPlayer.setCount(fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount() + 1);
					fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stackToAddToPlayer);

				}

			}
		}

		if (direction == null) {
			direction = Direction.DOWN;
		}

		fakePlayer.setXRot(direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0);
		fakePlayer.setYRot(direction.toYRot());

		Method method = DeployerHandler.class.getDeclaredMethod("activate", DeployerFakePlayer.class, Vec3.class,
				BlockPos.class, Vec3.class,
				Class.forName("com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode"));
		method.setAccessible(true);

		method.invoke(DeployerHandler.class, fakePlayer, robot.position(), posToClick,
				Vec3.atLowerCornerOf(direction.getNormal()),
				Class.forName("com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode")
						.getEnumConstants()[use ? 0 : 1]);

		fakePlayer.getInventory().items.forEach(itemStack -> {
			if (robot.wantsToPickUp(itemStack)) {
				robot.getInventory().addItem(itemStack);
			} else {
				fakePlayer.drop(itemStack, true);
			}
		});
		fakePlayer.discard();

		Thread.sleep(200);
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
