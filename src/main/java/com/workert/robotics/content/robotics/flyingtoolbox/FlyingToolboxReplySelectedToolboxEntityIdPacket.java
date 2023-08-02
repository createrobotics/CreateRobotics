package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.content.curiosities.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class FlyingToolboxReplySelectedToolboxEntityIdPacket extends SimplePacketBase {
	private final int entityId;
	private final int slot;

	public FlyingToolboxReplySelectedToolboxEntityIdPacket(int entityId, int slot) {
		this.entityId = entityId;
		this.slot = slot;
	}

	public FlyingToolboxReplySelectedToolboxEntityIdPacket(FriendlyByteBuf buffer) {
		this.entityId = buffer.readInt();
		this.slot = buffer.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.entityId);
		buffer.writeInt(this.slot);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			List<ToolboxTileEntity> toolboxes = ToolboxHandler.getNearest(player.level, player, 8);
			toolboxes.sort(Comparator.comparing(ToolboxTileEntity::getUniqueId));

			FlyingToolbox flyingToolbox = ((FlyingToolbox) player.level.getEntity(this.entityId));
			RadialToolboxMenu screen = new RadialToolboxMenu(toolboxes,
					RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP, flyingToolbox.fakeToolboxTileEntity);
			screen.prevSlot(this.slot);
			ScreenOpener.open(screen);
		});
		ctx.setPacketHandled(true);
	}
}
