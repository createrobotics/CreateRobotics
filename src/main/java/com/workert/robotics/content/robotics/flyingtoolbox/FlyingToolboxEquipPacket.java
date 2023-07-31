package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.content.curiosities.toolbox.ItemReturnInvWrapper;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.mixin.ToolboxInventoryAccessor;
import com.workert.robotics.mixin.ToolboxTileEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlyingToolboxEquipPacket extends SimplePacketBase {
	private Integer toolboxEntityId;
	private BlockPos pos;
	private int slot;
	private int hotbarSlot;

	public FlyingToolboxEquipPacket(Integer toolboxEntityId, BlockPos pos, int slot, int hotbarSlot) {
		this.toolboxEntityId = toolboxEntityId;
		this.pos = pos;
		this.slot = slot;
		this.hotbarSlot = hotbarSlot;
	}

	public FlyingToolboxEquipPacket(FriendlyByteBuf buffer) {
		this.toolboxEntityId = buffer.readInt();
		this.pos = buffer.readBlockPos();
		this.slot = buffer.readVarInt();
		this.hotbarSlot = buffer.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.toolboxEntityId);
		buffer.writeBlockPos(this.pos);
		buffer.writeVarInt(this.slot);
		buffer.writeVarInt(this.hotbarSlot);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			Level world = player.level;

			if (this.toolboxEntityId == null) {
				ToolboxHandler.unequip(player, this.hotbarSlot, false);
				ToolboxHandler.syncData(player);
				return;
			}

			if (!(world.getEntity(this.toolboxEntityId) instanceof FlyingToolbox flyingToolbox))
				return;

			double maxRange = ToolboxHandler.getMaxRange(player);
			if (player.distanceToSqr(flyingToolbox.getX() + 0.5, flyingToolbox.getY(),
					flyingToolbox.getZ() + 0.5) > maxRange * maxRange)
				return;


			ToolboxHandler.unequip(player, this.hotbarSlot, false);

			if (this.slot < 0 || this.slot >= 8) {
				ToolboxHandler.syncData(player);
				return;
			}

			ToolboxTileEntity toolboxTileEntity = flyingToolbox.getFakeToolboxTileEntity();

			ItemStack playerStack = player.getInventory().getItem(this.hotbarSlot);
			if (!playerStack.isEmpty() && !ToolboxInventory.canItemsShareCompartment(playerStack,
					((ToolboxInventoryAccessor) ((ToolboxTileEntityAccessor) toolboxTileEntity).getInventory()).getFilters()
							.get(this.slot))) {
				((ToolboxTileEntityAccessor) toolboxTileEntity).getInventory().inLimitedMode(inventory -> {
					ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, playerStack, false);
					if (!remainder.isEmpty())
						remainder = ItemHandlerHelper.insertItemStacked(new ItemReturnInvWrapper(player.getInventory()),
								remainder, false);
					if (remainder.getCount() != playerStack.getCount())
						player.getInventory().setItem(this.hotbarSlot, remainder);
				});
			}

			CompoundTag compound = player.getPersistentData()
					.getCompound("CreateToolboxData");
			String key = String.valueOf(this.hotbarSlot);

			CompoundTag data = new CompoundTag();
			data.putInt("Slot", this.slot);
			data.putUUID("EntityUUID", flyingToolbox.getUUID());
			data.put("Pos", NbtUtils.writeBlockPos(this.pos));
			compound.put(key, data);

			player.getPersistentData()
					.put("CreateToolboxData", compound);

			toolboxTileEntity.connectPlayer(this.slot, player, this.hotbarSlot);
			ToolboxHandler.syncData(player);
		});
		ctx.setPacketHandled(true);
	}
}
