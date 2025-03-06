package com.workert.robotics.content.utility.extendoboots;

import com.google.common.collect.Maps;
import com.workert.robotics.base.client.KeybindList;
import com.workert.robotics.base.registries.ArmorMaterialRegistry;
import com.workert.robotics.base.registries.EntityRegistry;
import com.workert.robotics.base.registries.ItemRegistry;
import com.workert.robotics.base.registries.PacketRegistry;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class ExtendOBootsItem extends ArmorItem {
	public static final float MAX_HEIGHT = 5;
	private static final Map<ItemStack, ExtendOBoots> ENTITIES = Maps.newIdentityHashMap();

	private static final Map<ItemStack, LerpedFloat> HEIGHT = Maps.newIdentityHashMap();

	private boolean clientSentOff; // Will always be false on server

	public ExtendOBootsItem(Properties pProperties) {
		super(ArmorMaterialRegistry.EXTEND_O_BOOTS, Type.BOOTS, pProperties);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);
		if (level.isClientSide()) {
			if (!this.clientSentOff && !KeybindList.changeExtendOBootsHeight.isDown()) {
				PacketRegistry.getChannel().sendToServer(new ChangeExtendOBootsHeightPacket(-MAX_HEIGHT));
				this.clientSentOff = true;
			}
			return;
		}
		if (stack.getOrCreateTag().getFloat("currentHeight") > 0) {
			if (HEIGHT.get(stack) == null) HEIGHT.put(stack, LerpedFloat.linear());
			if (stack.getOrCreateTag().getFloat("currentHeight") > HEIGHT.get(stack).getValue())
				HEIGHT.get(stack)
						.chase(stack.getOrCreateTag().getFloat("currentHeight"), 0.3, LerpedFloat.Chaser.LINEAR);
			else HEIGHT.get(stack)
					.chase(stack.getOrCreateTag().getFloat("currentHeight"), 0.55, LerpedFloat.Chaser.EXP);

			ExtendOBoots extendOBoots = ENTITIES.get(stack);
			if (extendOBoots == null || extendOBoots.isRemoved()) {
				extendOBoots = new ExtendOBoots(EntityRegistry.EXTEND_O_BOOTS.get(), entity.level());
				extendOBoots.setPos(entity.position());
				extendOBoots.setYRot(0);
				extendOBoots.setXRot(0);
				entity.level().addFreshEntity(extendOBoots);
				ENTITIES.put(stack, extendOBoots);
			}
			entity.teleportTo(extendOBoots.getX(), extendOBoots.getY() + HEIGHT.get(stack).getValue(), extendOBoots.getZ());
			entity.setYRot(extendOBoots.getYRot());
			if (entity.position().distanceTo(extendOBoots.position()
					.with(Direction.Axis.Y, extendOBoots.getY() + HEIGHT.get(stack).getValue())) > 0.1)
				stack.getOrCreateTag().putFloat("currentHeight", 0);
			extendOBoots.setHeight(stack.getOrCreateTag().getFloat("currentHeight"));

			HEIGHT.get(stack).tickChaser();
		} else if (ENTITIES.get(stack) != null) {
			ENTITIES.get(stack).discard();
			ENTITIES.put(stack, null);
			HEIGHT.put(stack, null);
		}
	}

	@SubscribeEvent
	public void detectScroll(InputEvent.MouseScrollingEvent mouseEvent) {
		if (KeybindList.changeExtendOBootsHeight.isDown()
				&& Minecraft.getInstance().player != null
				&& Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.FEET).getItem().equals(ItemRegistry.EXTEND_O_BOOTS.get())
		) {
			this.clientSentOff = false;
			PacketRegistry.getChannel().sendToServer(new ChangeExtendOBootsHeightPacket(mouseEvent.getScrollDelta() > 0 ? 0.5 : -0.5));
			mouseEvent.setCanceled(true);
		}
	}
}