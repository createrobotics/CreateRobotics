package com.workert.robotics.base.registries;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.computers.computer.ComputerClearTerminalPacket;
import com.workert.robotics.content.computers.computer.ComputerToggleRunningPacket;
import com.workert.robotics.content.computers.computer.ConfigureComputerScriptPacket;
import com.workert.robotics.content.computers.inputs.ConfigureInputSignalPacket;
import com.workert.robotics.content.robotics.codeeditor.ReturnEditedCodePacket;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxEquipPacket;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxGetSelectedToolboxEntityIdPacket;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxReplySelectedToolboxEntityIdPacket;
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

public enum PacketRegistry {
	// Packets to Server
	CHANGE_EXTEND_O_BOOTS_HEIGHT(ChangeExtendOBootsHeightPacket.class, ChangeExtendOBootsHeightPacket::new,
			PLAY_TO_SERVER),
	RETURN_EDITED_CODE(ReturnEditedCodePacket.class, ReturnEditedCodePacket::new, PLAY_TO_SERVER),
	CONFIGURE_INPUT_SIGNAL(ConfigureInputSignalPacket.class, ConfigureInputSignalPacket::new, PLAY_TO_SERVER),
	CONFIGURE_COMPUTER_SCRIPT(ConfigureComputerScriptPacket.class, ConfigureComputerScriptPacket::new, PLAY_TO_SERVER),
	CLEAR_COMPUTER_TERMINAL(ComputerClearTerminalPacket.class, ComputerClearTerminalPacket::new, PLAY_TO_SERVER),
	COMPUTER_SET_RUNNING(ComputerToggleRunningPacket.class, ComputerToggleRunningPacket::new, PLAY_TO_SERVER),
	FLYING_TOOLBOX_EQUIP(FlyingToolboxEquipPacket.class, FlyingToolboxEquipPacket::new, PLAY_TO_SERVER),
	FLYING_TOOLBOX_GET_SELECTED_TOOLBOX_ENTITY_ID(FlyingToolboxGetSelectedToolboxEntityIdPacket.class,
			FlyingToolboxGetSelectedToolboxEntityIdPacket::new, PLAY_TO_SERVER),

	// Packets to Client
	FLYING_TOOLBOX_REPLY_SELECTED_TOOLBOX_ENTITY_ID(FlyingToolboxReplySelectedToolboxEntityIdPacket.class,
			FlyingToolboxReplySelectedToolboxEntityIdPacket::new, PLAY_TO_CLIENT);


	public static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	private final PacketRegistry.LoadedPacket<?> packet;

	<T extends SimplePacketBase> PacketRegistry(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
		this.packet = new PacketRegistry.LoadedPacket<>(type, factory, direction);
	}

	public static void registerPackets() {
		CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Robotics.MOD_ID, "main"),
				() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
		for (PacketRegistry packet : values()) {
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
