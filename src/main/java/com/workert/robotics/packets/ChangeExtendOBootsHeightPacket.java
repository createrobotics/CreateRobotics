package com.workert.robotics.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.Robotics;
import com.workert.robotics.items.ExtendOBootsItem;
import com.workert.robotics.lists.ItemList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeExtendOBootsHeightPacket extends SimplePacketBase {
	private final double value;

	public ChangeExtendOBootsHeightPacket(FriendlyByteBuf buffer) {
		Robotics.LOGGER.info("Constructing ChangeExtendOBootsHeightPacket with buffer!");
		this.value = buffer.readDouble();
		Robotics.LOGGER.info("Value: " + this.value);
	}

	public ChangeExtendOBootsHeightPacket(double scrollValue) {
		Robotics.LOGGER.info("Constructing ChangeExtendOBootsHeightPacket with value!");
		this.value = scrollValue;
		Robotics.LOGGER.info("Value: " + this.value);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		Robotics.LOGGER.info("Writing ChangeExtendOBootsHeightPacket!");
		buffer.writeDouble(this.value);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			Robotics.LOGGER.info("Handling ChangeExtendOBootsHeightPacket!");
			if (!context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getItem()
					.equals(ItemList.EXTEND_O_BOOTS.get()))
				return;
			if (context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag().getDouble("currentHeight")
					== 0 && !context.get().getSender().isOnGround())
				return;
			context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag().putDouble("currentHeight",
					Mth.clamp(context.get().getSender().getItemBySlot(EquipmentSlot.FEET).getOrCreateTag()
							.getDouble("currentHeight") + this.value, 0, ExtendOBootsItem.MAX_HEIGHT));
		});
		context.get().setPacketHandled(true);
	}
}
