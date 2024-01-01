package com.workert.robotics.content.utility.extendoboots;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.base.registries.ItemRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent;

public class ChangeExtendOBootsHeightPacket extends SimplePacketBase {
	private final double value;

	public ChangeExtendOBootsHeightPacket(FriendlyByteBuf buffer) {
		this.value = buffer.readDouble();
	}

	public ChangeExtendOBootsHeightPacket(double scrollValue) {
		this.value = scrollValue;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeDouble(this.value);
	}

	@Override
	public boolean handle(NetworkEvent.Context context) {
		if (!context.getSender().getItemBySlot(EquipmentSlot.FEET).getItem()
				.equals(ItemRegistry.EXTEND_O_BOOTS.get())) return true;
		if (context.getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag()
				.getDouble("currentHeight") == 0 && !context.getSender().onGround()) return true;
		if (this.value > 0 && !context.getSender().level()
				.isEmptyBlock(context.getSender().blockPosition().above().above())) return true;
		context.getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag().putDouble("currentHeight",
				Mth.clamp(context.getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag()
						.getDouble("currentHeight") + this.value, 0, ExtendOBootsItem.MAX_HEIGHT));

		return true;
	}
}
