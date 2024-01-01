package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.content.equipment.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Comparator;
import java.util.List;

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
	public boolean handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		List<ToolboxBlockEntity> toolboxes = ToolboxHandler.getNearest(player.level(), player, 8);
		toolboxes.sort(Comparator.comparing(ToolboxBlockEntity::getUniqueId));

		FlyingToolbox flyingToolbox = ((FlyingToolbox) player.level().getEntity(this.entityId));
		RadialToolboxMenu screen = new RadialToolboxMenu(toolboxes,
				RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP, flyingToolbox.fakeToolboxBlockEntity);
		screen.prevSlot(this.slot);
		ScreenOpener.open(screen);
		return true;
	}
}
