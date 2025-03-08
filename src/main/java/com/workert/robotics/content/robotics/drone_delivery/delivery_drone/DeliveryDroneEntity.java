package com.workert.robotics.content.robotics.drone_delivery.delivery_drone;

import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.EntityRegistry;
import com.workert.robotics.content.robotics.drone_delivery.drone_port.DronePortBlockEntity;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DeliveryDroneEntity extends LivingEntity {
	private ItemStack box;

	private boolean isReturning = false;
	private BlockPos startBlockPos;
	private BlockPos destinationBlockPos;

	private List<BlockPos> path;
	private int pathProgress = 0;

	public DeliveryDroneEntity(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
		this.box = ItemStack.EMPTY;
		this.setNoGravity(true);
	}

	public static DeliveryDroneEntity fromItemStack(Level world, Vec3 position, ItemStack boxStack, BlockPos startBlockPos, BlockPos destinationBlockPos) {
		DeliveryDroneEntity deliveryDroneEntity = EntityRegistry.DELIVERY_DRONE.get().create(world);
		deliveryDroneEntity.setPos(position);
		deliveryDroneEntity.setRot(0, 0);
		deliveryDroneEntity.setBox(boxStack);
		deliveryDroneEntity.startBlockPos = startBlockPos;
		deliveryDroneEntity.destinationBlockPos = destinationBlockPos;
		return deliveryDroneEntity;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide())
			return;

		for (int x = -3; x <= 3; x++) {
			for (int z = -3; z <= 3; z++) {
				ForgeChunkManager.forceChunk(
						(ServerLevel) this.level(),
						Robotics.MOD_ID,
						this,
						this.chunkPosition().x + x,
						this.chunkPosition().z + z,
						!(Math.abs(x) == 3 || Math.abs(z) == 3),
						false
				);
			}
		}

		if (this.path == null || this.path.isEmpty()) {
			this.path = Robotics.DRONE_NETWORK.savedPaths.computeIfAbsent(this.level().dimension().toString(), key -> new HashMap<>())
					.get(this.isReturning
							? Couple.create(this.destinationBlockPos, this.startBlockPos)
							: Couple.create(this.startBlockPos, this.destinationBlockPos)
					);
			if (this.path != null) {
				double smallestCurrentDistance = Double.MAX_VALUE;
				BlockPos nearestBlockPos = null;
				for (int i = 0; i < this.path.size(); i++) {
					double thisDistance = this.position().distanceTo(this.path.get(i).getCenter());
					if (thisDistance < smallestCurrentDistance) {
						smallestCurrentDistance = thisDistance;
						nearestBlockPos = this.path.get(i);
						this.pathProgress = i;
					}
				}
				if (nearestBlockPos != null)
					this.moveTo(nearestBlockPos.getCenter().subtract(0, 0.2, 0));
			}
		}


		if (this.path != null && !this.path.isEmpty()) {
			if (!this.level()
					.isEmptyBlock(this.path.get(this.pathProgress)) || (this.pathProgress != (this.path.size() - 1) && !this.level()
					.isEmptyBlock(this.path.get(this.pathProgress + 1)))) {
				this.setDeltaMovement(Vec3.ZERO);
				Robotics.DRONE_NETWORK.recalculatePath(this.level(), this.startBlockPos, this.destinationBlockPos);
				this.path = null;
				this.pathProgress = 0;
				return;
			}

			double droneSpeed = 0.16;

			if (this.position().distanceTo(this.path.get(this.pathProgress).getCenter()) < droneSpeed * 2.5) {
				this.moveTo(this.path.get(this.pathProgress).getCenter().subtract(0, 0.2, 0));
				if (this.pathProgress != (this.path.size() - 1)) {
					this.pathProgress++;
				} else {
					if (!this.box.isEmpty() && this.destinationBlockPos != null
							&& this.level().getBlockEntity(this.destinationBlockPos) != null
							&& this.level().getBlockEntity(this.destinationBlockPos) instanceof DronePortBlockEntity dronePortBlockEntity) {
						if (this.isReturning) {
							this.dropBox();
							this.discard();
							return;
						}
						if (ItemHandlerHelper.insertItem(dronePortBlockEntity.inventory, this.box, false).isEmpty()) {
							this.box = ItemStack.EMPTY;
							this.isReturning = true;
							this.path = null;
							this.pathProgress = 0;
							this.setDeltaMovement(0, -droneSpeed, 0);
							return;
						}
					} else {
						this.dropBox();
						this.discard();
					}
				}
			}


			if (this.path != null && !this.path.isEmpty()) {
				this.setDeltaMovement(
						this.path.get(this.pathProgress).getCenter()
								.subtract(this.position())
								.subtract(0, 0.2, 0)
								.normalize()
								.multiply(new Vec3(droneSpeed, droneSpeed, droneSpeed))
				);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!ForgeHooks.onLivingAttack(this, source, amount))
			return false;

		if (this.level().isClientSide || !this.isAlive())
			return false;

		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			this.remove(RemovalReason.KILLED);
		}

		if (source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().mayBuild) {
			this.dropBox();
			this.remove(RemovalReason.KILLED);
		}

		return false;
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		if (!this.box.isEmpty())
			return this.box.copy();
		return super.getPickedResult(target);
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		if (entity instanceof DeliveryDroneEntity)
			return false;
		return super.canCollideWith(entity);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	private void dropBox() {
		if (this.box.isEmpty())
			return;
		ItemEntity entityIn = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.box);
		this.level().addFreshEntity(entityIn);
	}

	@Override
	protected void dropAllDeathLoot(DamageSource pDamageSource) {
		super.dropAllDeathLoot(pDamageSource);
		this.dropBox();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("IsReturning", this.isReturning);
		compound.put("StartBlockPos", NbtUtils.writeBlockPos(this.startBlockPos));
		compound.put("DestinationBlockPos", NbtUtils.writeBlockPos(this.destinationBlockPos));
		compound.putInt("PathProgress", this.pathProgress);

		CompoundTag boxTag = new CompoundTag();
		this.box.save(boxTag);
		compound.put("Package", boxTag);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.isReturning = compound.getBoolean("IsReturning");
		this.startBlockPos = NbtUtils.readBlockPos(compound.getCompound("StartBlockPos"));
		this.destinationBlockPos = NbtUtils.readBlockPos(compound.getCompound("DestinationBlockPos"));
		this.pathProgress = compound.getInt("PathProgress");
		this.box = ItemStack.of(compound.getCompound("Package"));
	}

	public ItemStack getBox() {
		return this.box;
	}

	public void setBox(ItemStack box) {
		this.box = box;
	}

	public String getAddress() {
		if (this.box.isEmpty())
			return "";
		return this.box.getTag().getString("Address");
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
	}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return Collections.emptyList();
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlot pSlot) {
		if (pSlot == EquipmentSlot.MAINHAND)
			return this.getBox();
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
		if (pSlot == EquipmentSlot.MAINHAND)
			this.setBox(pStack);
	}

	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.RIGHT;
	}
}
