package com.workert.robotics.content.robotics.codeeditor;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.base.registries.ItemRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReturnEditedCodePacket extends SimplePacketBase {
	private final String code;

	public ReturnEditedCodePacket(FriendlyByteBuf buffer) {
		this.code = buffer.readUtf();
	}

	public ReturnEditedCodePacket(String code) {
		this.code = code;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.code);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().getItemInHand(InteractionHand.MAIN_HAND).is(ItemRegistry.PROGRAM.get()))
				context.get().getSender().getItemInHand(InteractionHand.MAIN_HAND).getOrCreateTag()
						.putString("code", this.code);
			else if (context.get().getSender().getItemInHand(InteractionHand.OFF_HAND).is(ItemRegistry.PROGRAM.get()))
				context.get().getSender().getItemInHand(InteractionHand.OFF_HAND).getOrCreateTag()
						.putString("code", this.code);
		});
		context.get().setPacketHandled(true);
	}
}
