package com.workert.robotics.base.registries;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.computers.inputs.ConfigureInputSignalPacket;
import com.workert.robotics.content.computers.inputs.InputPlacementPacket;
import com.workert.robotics.content.robotics.codeeditor.ReturnEditedCodePacket;
import com.workert.robotics.content.utility.extendoboots.ChangeExtendOBootsHeightPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum AllPackets {
	// Packets to Server
	CHANGE_EXTEND_O_BOOTS_HEIGHT(ChangeExtendOBootsHeightPacket.class, ChangeExtendOBootsHeightPacket::new,
			PLAY_TO_SERVER),
	RETURN_EDITED_CODE(ReturnEditedCodePacket.class, ReturnEditedCodePacket::new, PLAY_TO_SERVER),
	CONFIGURE_INPUT_SIGNAL(ConfigureInputSignalPacket.class, ConfigureInputSignalPacket::new, PLAY_TO_SERVER),
	PLACE_INPUT(InputPlacementPacket.class, InputPlacementPacket::new, PLAY_TO_SERVER),


	//Packets to client
	S_PLACE_INPUT(InputPlacementPacket.ClientBoundRequest.class, InputPlacementPacket.ClientBoundRequest::new,
			PLAY_TO_CLIENT);

	public static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	private final AllPackets.LoadedPacket<?> packet;

	<T extends SimplePacketBase> AllPackets(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
		this.packet = new AllPackets.LoadedPacket<>(type, factory, direction);
	}

	public static void registerPackets() {
		CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Robotics.MOD_ID, "main"),
				() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
		for (AllPackets packet : values()) {
			packet.packet.register();
		}
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
