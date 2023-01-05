package com.workert.robotics.items;

import com.mojang.blaze3d.platform.InputConstants;
import com.workert.robotics.entities.ExtendOBoots;
import com.workert.robotics.lists.EntityList;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ExtendOBootsItem extends ArmorItem {
	private boolean activated = false;
	private double currentHeight = 0;
	private static final double MAX_HEIGHT = 10;
	private ExtendOBoots extendOBootsEntity;
	private Player player;

	public ExtendOBootsItem(ArmorMaterial pMaterial, EquipmentSlot pSlot, Properties pProperties) {
		super(pMaterial, pSlot, pProperties);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		super.onArmorTick(stack, level, player);
		if (level.isClientSide())
			return;
		this.player = player;
		if (this.activated) {
			player.teleportTo(player.getX(), this.extendOBootsEntity.getY() + this.currentHeight, player.getZ());
			this.player.setYRot(this.extendOBootsEntity.getYRot());
			if (this.player.position().distanceTo(this.extendOBootsEntity.position()
					.with(Direction.Axis.Y, this.extendOBootsEntity.getY() + this.currentHeight)) > 0.1)
				this.activated = false;
		} else if (this.extendOBootsEntity != null) {
			this.extendOBootsEntity.discard();
			this.extendOBootsEntity = null;
		}
	}

	@SubscribeEvent
	public void detectScroll(InputEvent.MouseScrollingEvent mouseEvent) {
		if (this.player == null)
			return;
		if (mouseEvent.getScrollDelta() > 0 && this.currentHeight < MAX_HEIGHT && this.activated) {
			this.currentHeight += 0.5;
		} else if (mouseEvent.getScrollDelta() < 0 && this.currentHeight > 0 && this.activated) {
			this.currentHeight -= 0.5;
		}
	}

	@SubscribeEvent
	public void detectInput(InputEvent.Key event) {
		if (this.player == null)
			return;
		if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.PRESS
				&& this.player.isOnGround()) {
			this.currentHeight = 0;
			this.extendOBootsEntity = new ExtendOBoots(EntityList.EXTEND_O_BOOTS.get(), this.player.getLevel());
			this.extendOBootsEntity.setPos(this.player.position());
			this.extendOBootsEntity.setYRot(this.player.getYRot());
			this.player.getLevel().addFreshEntity(this.extendOBootsEntity);
			this.activated = true;
		} else if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.RELEASE) {
			this.activated = false;
		}
	}

	@SubscribeEvent
	public void detectPlayerDamage(LivingDamageEvent event) {
		if (event.getEntity().equals(this.player) && this.activated)
			this.activated = false;
	}
}