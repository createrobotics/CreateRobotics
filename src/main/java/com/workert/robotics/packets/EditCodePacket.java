package com.workert.robotics.packets;

import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.client.screens.CodeEditorScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EditCodePacket extends SimplePacketBase {
	private final String code;

	public EditCodePacket(FriendlyByteBuf buffer) {
		this.code = buffer.readUtf();
	}

	public EditCodePacket(String code) {
		this.code = code;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.code);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			ScreenOpener.open(new CodeEditorScreen(this.code));
		});
		context.get().setPacketHandled(true);
	}
}
