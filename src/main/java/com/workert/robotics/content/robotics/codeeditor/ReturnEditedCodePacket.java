package com.workert.robotics.content.robotics.codeeditor;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.base.registries.ItemRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

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
	public boolean handle(NetworkEvent.Context context) {
		if (context.getSender().getItemInHand(InteractionHand.MAIN_HAND).is(ItemRegistry.PROGRAM.get()))
			context.getSender().getItemInHand(InteractionHand.MAIN_HAND).getOrCreateTag()
					.putString("code", this.code);
		else if (context.getSender().getItemInHand(InteractionHand.OFF_HAND).is(ItemRegistry.PROGRAM.get()))
			context.getSender().getItemInHand(InteractionHand.OFF_HAND).getOrCreateTag()
					.putString("code", this.code);
		return true;
	}
}
