package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.AllBlocks;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class FlyingToolbox extends AbstractRobotEntity {
	Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers;

	FakeToolboxTileEntity fakeToolboxTileEntity;

	DyeColor color = DyeColor.BROWN;

	public FlyingToolbox(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.connectedPlayers = new HashMap<>();
		this.fakeToolboxTileEntity = new FakeToolboxTileEntity(this);
		FlyingToolboxHandler.onLoad(this);
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level.isClientSide && this.fakeToolboxTileEntity != null) {
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
	public void remove(@NotNull RemovalReason pReason) {
		FlyingToolboxHandler.onUnload(this);
		this.fakeToolboxTileEntity = null;
		super.remove(pReason);
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

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		if (this.fakeToolboxTileEntity != null) {
			pCompound.put("FakeToolboxTileEntityNBT", this.fakeToolboxTileEntity.serializeNBT());
		}
		super.addAdditionalSaveData(pCompound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		if (this.fakeToolboxTileEntity != null) {
			this.fakeToolboxTileEntity.deserializeNBT(pCompound.getCompound("FakeToolboxTileEntityNBT"));
		}
		super.readAdditionalSaveData(pCompound);
	}

	@Override
	public Item getRobotItem() {
		return AllBlocks.TOOLBOXES.get(this.color).get().asItem();
	}

	@Override
	public boolean isProgrammable() {
		return false;
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
		return false;
	}
}
