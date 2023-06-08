package com.workert.robotics.content.utility.extendoboots;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.base.lists.ItemList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			if (!context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getItem()
					.equals(ItemList.EXTEND_O_BOOTS.get())) return;
			if (context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag()
					.getDouble("currentHeight") == 0 && !context.get().getSender().isOnGround()) return;
			if (this.value > 0 && !context.get().getSender().getLevel()
					.isEmptyBlock(context.get().getSender().blockPosition().above().above())) return;
			context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag().putDouble("currentHeight",
					Mth.clamp(context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag()
							.getDouble("currentHeight") + this.value, 0, ExtendOBootsItem.MAX_HEIGHT));
		});
		context.get().setPacketHandled(true);
	}
}
