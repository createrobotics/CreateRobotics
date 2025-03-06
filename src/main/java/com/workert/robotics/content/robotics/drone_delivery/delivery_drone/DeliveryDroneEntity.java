package com.workert.robotics.content.robotics.drone_delivery.delivery_drone;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.EntityRegistry;
import com.workert.robotics.content.robotics.drone_delivery.drone_port.DronePortBlockEntity;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DeliveryDroneEntity extends LivingEntity {
	private ItemStack box;

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
		deliveryDroneEntity.setBox(boxStack);
		deliveryDroneEntity.startBlockPos = startBlockPos;
		deliveryDroneEntity.destinationBlockPos = destinationBlockPos;
		return deliveryDroneEntity;
	}

	/*@Override
	public EntityDimensions getDimensions(Pose pPose) {
		return EntityDimensions.fixed(0.4f, 0.4f);
	}*/

	@Override
	public void tick() {
		super.tick();

		if (this.path == null || this.path.isEmpty()) {
			this.path = Robotics.DRONE_NETWORK.savedPaths.computeIfAbsent(this.level().dimension().toString(), key -> new HashMap<>())
					.get(Couple.create(this.startBlockPos, this.destinationBlockPos));
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
					this.moveTo(nearestBlockPos.getCenter().add(0, 0.2, 0));
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

			double droneSpeed = 0.12;

			if (this.position().distanceTo(this.path.get(this.pathProgress).getCenter()) < droneSpeed * 2.5) {
				this.moveTo(this.path.get(this.pathProgress).getCenter().add(0, 0.2, 0));
				if (this.pathProgress != (this.path.size() - 1))
					this.pathProgress++;
			}


			if (!this.path.isEmpty()) {
				this.setDeltaMovement(
						this.path.get(this.pathProgress).getCenter()
								.subtract(this.position())
								.add(0, 0.2, 0)
								.normalize()
								.multiply(new Vec3(droneSpeed, droneSpeed, droneSpeed))
				);
			}
		}

		// Why do I have to check destinationBlockPos != null here?! It should never be null if Minecraft doesn't mess up readAdditionalSaveData.
		if (this.path != null && this.pathProgress == (this.path.size() - 1)) {
			if (!this.box.isEmpty() && this.destinationBlockPos != null
					&& this.level().getBlockEntity(this.destinationBlockPos) != null
					&& this.level().getBlockEntity(this.destinationBlockPos) instanceof DronePortBlockEntity dronePortBlockEntity) {
				if (ItemHandlerHelper.insertItem(dronePortBlockEntity.inventory, this.box, false).isEmpty())
					this.discard();
			} else {
				this.dropAllDeathLoot(this.level().damageSources().generic());
				this.discard();
			}
		}
	}

	// Copy-pasted from Entity#getInputVector
	private Vec3 getInputVector(Vec3 relative, float motionScaler) {
		float facing = this.getYRot();
		double d0 = relative.lengthSqr();
		if (d0 < 1.0E-7) {
			return Vec3.ZERO;
		} else {
			Vec3 vec3 = (d0 > (double) 1.0F ? relative.normalize() : relative).scale((double) motionScaler);
			float f = Mth.sin(facing * ((float) Math.PI / 180F));
			float f1 = Mth.cos(facing * ((float) Math.PI / 180F));
			return new Vec3(vec3.x * (double) f1 - vec3.z * (double) f, vec3.y, vec3.z * (double) f1 + vec3.x * (double) f);
		}
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
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected void dropAllDeathLoot(DamageSource pDamageSource) {
		super.dropAllDeathLoot(pDamageSource);
		if (this.box.isEmpty())
			return;
		ItemStackHandler contents = PackageItem.getContents(this.box);
		for (int i = 0; i < contents.getSlots(); i++) {
			ItemStack itemstack = contents.getStackInSlot(i);

			if (itemstack.getItem() instanceof SpawnEggItem sei && this.level() instanceof ServerLevel sl) {
				EntityType<?> entitytype = sei.getType(itemstack.getTag());
				Entity entity =
						entitytype.spawn(sl, itemstack, null, this.blockPosition(), MobSpawnType.SPAWN_EGG, false, false);
				if (entity != null)
					itemstack.shrink(1);
			}

			if (itemstack.isEmpty())
				continue;
			ItemEntity entityIn = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemstack);
			this.level().addFreshEntity(entityIn);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.put("StartBlockPos", NbtUtils.writeBlockPos(this.startBlockPos));
		compound.put("DestinationBlockPos", NbtUtils.writeBlockPos(this.destinationBlockPos));
		compound.putInt("PathProgress", this.pathProgress);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.startBlockPos = NbtUtils.readBlockPos(compound.getCompound("StartBlockPos"));
		this.destinationBlockPos = NbtUtils.readBlockPos(compound.getCompound("DestinationBlockPos"));
		this.pathProgress = compound.getInt("PathProgress");
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
