package com.workert.robotics.unused.smasher;

import com.workert.robotics.Robotics;
import com.workert.robotics.base.lists.BlockList;
import com.workert.robotics.base.lists.MenuList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class SmasherBlockMenu extends AbstractContainerMenu {

	private final SmasherBlockEntity blockEntity;
	private final Level level;

	private final ContainerData data;

	public SmasherBlockMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
		this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
	}

	public SmasherBlockMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
		super(MenuList.SMASHER_BLOCK_MENU.get(), pContainerId);
		AbstractContainerMenu.checkContainerSize(inv, 3);
		this.blockEntity = ((SmasherBlockEntity) entity);
		this.level = inv.player.level;
		this.data = data;

		this.addPlayerInventory(inv);
		this.addPlayerHotbar(inv);

		this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			this.addSlot(new SlotItemHandler(handler, 0, 56, 17));
			this.addSlot(new SlotItemHandler(handler, 1, 56, 53));
			// this.addSlot(new ModResultSlot(handler, 2, 116, 35));
		});
		this.addDataSlots(data);
	}

	public boolean isCrafting() {
		return this.data.get(0) > 0;
	}

	public int getScaledProgress() {
		int progress = this.data.get(0);
		int maxProgress = this.data.get(1); // Max Progress
		int progressArrowSize = 26; // This is the height in pixels of your arrow

		return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
	}

	// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
	// must assign a slot number to each of the slots used by the GUI.
	// For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
	// Each time we add a Slot to the container, it automatically increases the slotIndex, which means
	//  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
	//  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
	//  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
	private static final int HOTBAR_SLOT_COUNT = 9;
	private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
	private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	private static final int PLAYER_INVENTORY_SLOT_COUNT = SmasherBlockMenu.PLAYER_INVENTORY_COLUMN_COUNT * SmasherBlockMenu.PLAYER_INVENTORY_ROW_COUNT;
	private static final int VANILLA_SLOT_COUNT = SmasherBlockMenu.HOTBAR_SLOT_COUNT + SmasherBlockMenu.PLAYER_INVENTORY_SLOT_COUNT;
	private static final int VANILLA_FIRST_SLOT_INDEX = 0;
	private static final int TE_INVENTORY_FIRST_SLOT_INDEX = SmasherBlockMenu.VANILLA_FIRST_SLOT_INDEX + SmasherBlockMenu.VANILLA_SLOT_COUNT;

	// THIS YOU HAVE TO DEFINE!
	private static final int TE_INVENTORY_SLOT_COUNT = 3; // must be the number of slots you have!

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		Slot sourceSlot = this.slots.get(index);
		if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY; //EMPTY_ITEM
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (index < SmasherBlockMenu.VANILLA_FIRST_SLOT_INDEX + SmasherBlockMenu.VANILLA_SLOT_COUNT) {
			// This is a vanilla container slot so merge the stack into the tile inventory
			if (!this.moveItemStackTo(sourceStack, SmasherBlockMenu.TE_INVENTORY_FIRST_SLOT_INDEX,
					SmasherBlockMenu.TE_INVENTORY_FIRST_SLOT_INDEX + SmasherBlockMenu.TE_INVENTORY_SLOT_COUNT, false)) {
				return ItemStack.EMPTY; // EMPTY_ITEM
			}
		} else if (index < SmasherBlockMenu.TE_INVENTORY_FIRST_SLOT_INDEX + SmasherBlockMenu.TE_INVENTORY_SLOT_COUNT) {
			// This is a TE slot so merge the stack into the players inventory
			if (!this.moveItemStackTo(sourceStack, SmasherBlockMenu.VANILLA_FIRST_SLOT_INDEX,
					SmasherBlockMenu.VANILLA_FIRST_SLOT_INDEX + SmasherBlockMenu.VANILLA_SLOT_COUNT, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			Robotics.LOGGER.warn("Invalid slotIndex:" + index);
			return ItemStack.EMPTY;
		}
		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}
		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(this.level, this.blockEntity.getBlockPos()),
				pPlayer, BlockList.SMASHER.get());
	}

	private void addPlayerInventory(Inventory playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
			}
		}
	}

	private void addPlayerHotbar(Inventory playerInventory) {
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
		}
	}
}