package com.workert.robotics.lists;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.Robotics;
import com.workert.robotics.packets.ChangeExtendOBootsHeightPacket;
import com.workert.robotics.packets.EditCodePacket;
import com.workert.robotics.packets.ReturnEditedCodePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum PacketList {
	// Packets to Client
	EDIT_CODE(EditCodePacket.class, EditCodePacket::new, NetworkDirection.PLAY_TO_CLIENT),

	// Packets to Server
	CHANGE_EXTEND_O_BOOTS_HEIGHT(ChangeExtendOBootsHeightPacket.class, ChangeExtendOBootsHeightPacket::new,
			NetworkDirection.PLAY_TO_SERVER),
	RETURN_EDITED_CODE(ReturnEditedCodePacket.class, ReturnEditedCodePacket::new, NetworkDirection.PLAY_TO_SERVER);

	public static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	private final PacketList.LoadedPacket<?> packet;

	<T extends SimplePacketBase> PacketList(Class<T> type, Function<FriendlyByteBuf, T> factory,
											NetworkDirection direction) {
		this.packet = new PacketList.LoadedPacket<>(type, factory, direction);
	}

	public static void registerPackets() {
		CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Robotics.MOD_ID, "main"),
				() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
		for (PacketList packet : values())
			packet.packet.register();
	}

	private static class LoadedPacket<T extends SimplePacketBase> {
		private static int index = 0;

		private final BiConsumer<T, FriendlyByteBuf> encoder;
		private final Function<FriendlyByteBuf, T> decoder;
		private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
		private final Class<T> type;
		private final NetworkDirection direction;

		private LoadedPacket(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
			this.encoder = T::write;
			this.decoder = factory;
			this.handler = T::handle;
			this.type = type;
			this.direction = direction;
		}

		private void register() {
			CHANNEL.messageBuilder(this.type, index++, this.direction).encoder(this.encoder).decoder(this.decoder)
					.consumerNetworkThread(this.handler).add();
		}
	}
}
