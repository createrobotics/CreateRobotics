package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.base.registries.PacketRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class FlyingToolboxGetSelectedToolboxEntityIdPacket extends SimplePacketBase {
	private final String slotKey;

	public FlyingToolboxGetSelectedToolboxEntityIdPacket(String slotKey) {
		this.slotKey = slotKey;
	}

	public FlyingToolboxGetSelectedToolboxEntityIdPacket(FriendlyByteBuf buffer) {
		this.slotKey = buffer.readUtf();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.slotKey);
	}

	@Override
	public boolean handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData")
				.getCompound(this.slotKey);
		Entity flyingToolboxEntity = player.server.overworld().getEntity(compound.getUUID("EntityUUID"));

		if (!(flyingToolboxEntity instanceof FlyingToolbox))
			throw new IllegalStateException("Entity with ID is not a FlyingToolbox Entity");

		PacketRegistry.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
				new FlyingToolboxReplySelectedToolboxEntityIdPacket(flyingToolboxEntity.getId(),
						compound.getInt("Slot")));
		return true;
	}
}
