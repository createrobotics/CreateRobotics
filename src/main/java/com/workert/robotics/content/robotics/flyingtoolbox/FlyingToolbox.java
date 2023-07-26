package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.workert.robotics.content.robotics.AbstractRobotEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class FlyingToolbox extends AbstractRobotEntity {
	Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers;

	FakeToolboxTileEntity fakeToolboxTileEntity;
	CompoundTag fakeToolboxTileEntityCompound;

	DyeColor color = DyeColor.BROWN;

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
			// TODO Testing
			ToolboxInventory inventory = new ToolboxInventory(this.fakeToolboxTileEntity);
			inventory.setStackInSlot(0, new ItemStack(Items.STONE, 50));
			this.fakeToolboxTileEntity.readInventory(inventory.serializeNBT());
		}

		ToolboxHandler.onUnload(this.fakeToolboxTileEntity);
		ToolboxHandler.onLoad(this.fakeToolboxTileEntity);

		if (!this.level.isClientSide) {
			this.fakeToolboxTileEntity.tick();
		}
	}

	@Override
	protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		if (pPlayer == null)
			return InteractionResult.PASS;
		if (pPlayer.isCrouching())
			return super.mobInteract(pPlayer, pHand);

		ItemStack stack = pPlayer.getItemInHand(pHand);
		DyeColor color = DyeColor.getColor(stack);
		if (color != null && color != this.color) {
			if (this.level.isClientSide)
				return InteractionResult.SUCCESS;
			this.color = color;
			return InteractionResult.SUCCESS;
		}

		if (pPlayer instanceof FakePlayer)
			return InteractionResult.PASS;
		if (this.level.isClientSide)
			return InteractionResult.SUCCESS;

		if (this.fakeToolboxTileEntity != null)
			NetworkHooks.openScreen((ServerPlayer) pPlayer, this.fakeToolboxTileEntity,
					this.fakeToolboxTileEntity::sendToContainer);
		return InteractionResult.SUCCESS;
	}

	@Override
	public void die(DamageSource pDamageSource) {
		super.die(pDamageSource);
		this.fakeToolboxTileEntity.invalidate();
		this.fakeToolboxTileEntity = null;
	}

	public DyeColor getColor() {
		return this.color;
	}

	public FakeToolboxTileEntity getFakeToolboxTileEntity() {
		return this.fakeToolboxTileEntity;
	}

	void sendData() {
		this.saveWithoutId(new CompoundTag());
	}

	void writeFakeToolboxTileEntityCompound(CompoundTag compound) {
		this.fakeToolboxTileEntityCompound = compound;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		this.fakeToolboxTileEntity.saveWithoutMetadata(); // Saves fakeToolboxTileEntity's Compound and forces writeFakeToolboxTileEntityCompound to trigger
		pCompound.put("FakeToolboxTileEntityCompound", this.fakeToolboxTileEntityCompound);
		super.addAdditionalSaveData(pCompound);
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
