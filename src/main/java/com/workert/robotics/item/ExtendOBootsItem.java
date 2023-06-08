package com.workert.robotics.item;

import com.google.common.collect.Maps;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.workert.robotics.client.KeybindList;
import com.workert.robotics.entity.ExtendOBoots;
import com.workert.robotics.lists.EntityList;
import com.workert.robotics.lists.PacketList;
import com.workert.robotics.packets.ChangeExtendOBootsHeightPacket;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class ExtendOBootsItem extends ArmorItem {
	public static final float MAX_HEIGHT = 5;
	private static final Map<ItemStack, ExtendOBoots> ENTITIES = Maps.newIdentityHashMap();

	private static final Map<ItemStack, LerpedFloat> HEIGHT = Maps.newIdentityHashMap();
	private Player player;

	private boolean clientSentOff;

	public ExtendOBootsItem(Properties pProperties) {
		super(ArmorMaterialList.EXTEND_O_BOOTS, EquipmentSlot.FEET, pProperties);
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
		if (stack.getOrCreateTag().getFloat("currentHeight") > 0) {
			if (this.HEIGHT.get(stack) == null) this.HEIGHT.put(stack, LerpedFloat.linear());
			if (stack.getOrCreateTag().getFloat("currentHeight") > this.HEIGHT.get(stack).getValue())
				this.HEIGHT.get(stack)
						.chase(stack.getOrCreateTag().getFloat("currentHeight"), 0.2, LerpedFloat.Chaser.LINEAR);
			else this.HEIGHT.get(stack)
					.chase(stack.getOrCreateTag().getFloat("currentHeight"), 0.55, LerpedFloat.Chaser.EXP);
			this.HEIGHT.get(stack).tickChaser();

			ExtendOBoots extendOBoots = this.ENTITIES.get(stack);
			if (extendOBoots == null || extendOBoots.isRemoved()) {
				extendOBoots = new ExtendOBoots(EntityList.EXTEND_O_BOOTS.get(), this.player.getLevel());
				extendOBoots.setPos(this.player.position());
				this.player.getLevel().addFreshEntity(extendOBoots);
				this.ENTITIES.put(stack, extendOBoots);
			}
			player.teleportTo(player.getX(), extendOBoots.getY() + this.HEIGHT.get(stack).getValue(), player.getZ());
			this.player.setYRot(extendOBoots.getYRot());
			if (this.player.position().distanceTo(extendOBoots.position()
					.with(Direction.Axis.Y, extendOBoots.getY() + this.HEIGHT.get(stack).getValue())) > 0.1)
				stack.getOrCreateTag().putFloat("currentHeight", 0);
			extendOBoots.setHeight(stack.getOrCreateTag().getFloat("currentHeight"));
		} else if (this.ENTITIES.get(stack) != null) {
			this.ENTITIES.get(stack).discard();
			this.ENTITIES.put(stack, null);
		}
	}

	@SubscribeEvent
	public void detectScroll(InputEvent.MouseScrollingEvent mouseEvent) {
		if (mouseEvent.getScrollDelta() > 0 && KeybindList.changeExtendOBootsHeight.isDown()) {
			this.clientSentOff = false;
			PacketList.CHANNEL.sendToServer(new ChangeExtendOBootsHeightPacket(0.5));
		} else if (mouseEvent.getScrollDelta() < 0 && KeybindList.changeExtendOBootsHeight.isDown()) {
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