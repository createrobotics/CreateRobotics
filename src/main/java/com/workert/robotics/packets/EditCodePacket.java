package com.workert.robotics.packets;

import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationBlock;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationEditPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.Robotics;
import com.workert.robotics.client.screens.CodeEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
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
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::displayScreen);
		});
		context.get().setPacketHandled(true);
	}

	@OnlyIn(value = Dist.CLIENT)
	protected void displayScreen() {
		ScreenOpener.open(new CodeEditorScreen(this.code));
	}
}
