package com.workert.robotics.items;

import com.workert.robotics.Robotics;
import com.workert.robotics.client.KeybindList;
import com.workert.robotics.entities.ExtendOBoots;
import com.workert.robotics.lists.EntityList;
import com.workert.robotics.lists.PacketList;
import com.workert.robotics.packets.ChangeExtendOBootsHeightPacket;
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

public class ExtendOBootsItem extends ArmorItem {
	public static final double MAX_HEIGHT = 10;
	private ExtendOBoots extendOBootsEntity;
	private Player player;

	private boolean clientSentOff;

	public ExtendOBootsItem(ArmorMaterial pMaterial, EquipmentSlot pSlot, Properties pProperties) {
		super(pMaterial, pSlot, pProperties);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		super.onArmorTick(stack, level, player);
		if (level.isClientSide()) {
			if (!this.clientSentOff && !KeybindList.changeExtendOBootsHeight.isDown()) {
				PacketList.CHANNEL.sendToServer(new ChangeExtendOBootsHeightPacket(-MAX_HEIGHT));
				this.clientSentOff = true;
			}
			return;
		}
		this.player = player;
		if (stack.getOrCreateTag().getDouble("currentHeight") > 0) {
			if (this.extendOBootsEntity == null) {
				this.extendOBootsEntity = new ExtendOBoots(EntityList.EXTEND_O_BOOTS.get(), this.player.getLevel());
				this.extendOBootsEntity.setPos(this.player.position());
				this.extendOBootsEntity.setYRot(this.player.getYRot());
				this.player.getLevel().addFreshEntity(this.extendOBootsEntity);
			}
			player.teleportTo(player.getX(),
					this.extendOBootsEntity.getY() + stack.getOrCreateTag().getDouble("currentHeight"), player.getZ());
			this.player.setYRot(this.extendOBootsEntity.getYRot());
			if (this.player.position().distanceTo(this.extendOBootsEntity.position().with(Direction.Axis.Y,
					this.extendOBootsEntity.getY() + stack.getOrCreateTag().getDouble("currentHeight"))) > 0.1)
				stack.getOrCreateTag().putDouble("currentHeight", 0);
		} else if (this.extendOBootsEntity != null) {
			this.extendOBootsEntity.discard();
			this.extendOBootsEntity = null;
		}
	}

	@SubscribeEvent
	public void detectScroll(InputEvent.MouseScrollingEvent mouseEvent) {
		Robotics.LOGGER.info("Detected Scroll!");
		if (mouseEvent.getScrollDelta() > 0 && KeybindList.changeExtendOBootsHeight.isDown()) {
			Robotics.LOGGER.info("Detected positive Scroll!");
			this.clientSentOff = false;
			PacketList.CHANNEL.sendToServer(new ChangeExtendOBootsHeightPacket(0.5));
		} else if (mouseEvent.getScrollDelta() < 0 && KeybindList.changeExtendOBootsHeight.isDown()) {
			Robotics.LOGGER.info("Detected negative Scroll!");
			this.clientSentOff = false;
			PacketList.CHANNEL.sendToServer(new ChangeExtendOBootsHeightPacket(-0.5));
		}
	}

	@SubscribeEvent
	public void detectPlayerDamage(LivingDamageEvent event) {
		if (event.getEntity().equals(this.player))
			this.player.getItemBySlot(EquipmentSlot.FEET).getOrCreateTag().putDouble("currentHeight", 0);
	}
}