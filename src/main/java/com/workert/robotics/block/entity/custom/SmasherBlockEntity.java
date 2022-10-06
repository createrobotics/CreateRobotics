package com.workert.robotics.block.entity.custom;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.workert.robotics.block.entity.ModBlockEntities;
import com.workert.robotics.item.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SmasherBlockEntity extends BlockEntity implements MenuProvider {

	private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
		@Override
		protected void onContentsChanged(int slot) {
			SmasherBlockEntity.this.setChanged();
		}
	};

	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	public SmasherBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(ModBlockEntities.SMASHER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
	}

	@Override
	public Component getDisplayName() {
		return new TextComponent("Smasher");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return null;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return this.lazyItemHandler.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		this.lazyItemHandler = LazyOptional.of(() -> this.itemHandler);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		this.lazyItemHandler.invalidate();
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		tag.put("inventory", this.itemHandler.serializeNBT());
		super.saveAdditional(tag);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.itemHandler.deserializeNBT(nbt.getCompound("inventory"));
	}

	public void drops() {
		SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
		for (int i = 0; i < this.itemHandler.getSlots(); i++) {
			inventory.setItem(i, this.itemHandler.getStackInSlot(i));
		}

		Containers.dropContents(this.level, this.worldPosition, inventory);
	}

	public static void tick(Level pLevel, BlockPos pPos, BlockState pState, SmasherBlockEntity pBlockEntity) {
		if (hasRecipe(pBlockEntity) && hasNotReachedStackLimit(pBlockEntity)) {
			craftItem(pBlockEntity);
		}
	}

	private static void craftItem(SmasherBlockEntity entity) {
		entity.itemHandler.extractItem(0, 1, false);
		entity.itemHandler.extractItem(1, 1, false);

		entity.itemHandler.setStackInSlot(2,
				new ItemStack(ModItems.BRONZE_INGOT.get(), entity.itemHandler.getStackInSlot(2).getCount() + 1));
	}

	private static boolean hasRecipe(SmasherBlockEntity entity) {
		boolean hasItemInItemSlot = entity.itemHandler.getStackInSlot(0).getItem() == ModItems.TIN_NUGGET.get();
		boolean hasItemInFuelSlot = entity.itemHandler.getStackInSlot(1).getItem() == ModItems.TIN_INGOT.get();

		return hasItemInItemSlot && hasItemInFuelSlot;
	}

	private static boolean hasNotReachedStackLimit(SmasherBlockEntity entity) {
		if (entity.itemHandler.getStackInSlot(2).isEmpty())
			return true;
		return entity.itemHandler.getStackInSlot(2).getCount() < entity.itemHandler.getStackInSlot(2).getMaxStackSize();
	}
}