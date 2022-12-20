package com.workert.robotics.blockentities;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.workert.robotics.client.screens.SmasherBlockMenu;
import com.workert.robotics.lists.BlockEntityList;
import com.workert.robotics.recipes.SmasherBlockRecipe;

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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

	protected final ContainerData data;
	private int progress = 0;
	private int maxProgress = 72;

	public SmasherBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(BlockEntityList.SMASHER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
		this.data = new ContainerData() {
			@Override
			public int get(int index) {
				switch (index) {
					case 0:
						return SmasherBlockEntity.this.progress;
					case 1:
						return SmasherBlockEntity.this.maxProgress;
					default:
						return 0;
				}
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0:
						SmasherBlockEntity.this.progress = value;
						break;
					case 1:
						SmasherBlockEntity.this.maxProgress = value;
						break;
				}

			}

			@Override
			public int getCount() {
				return 2;
			}
		};
	}

	@Override
	public Component getDisplayName() {
		return new TextComponent("Smasher");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new SmasherBlockMenu(pContainerId, pInventory, this, this.data);
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
		tag.putInt("smasher.progress", this.progress);
		super.saveAdditional(tag);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.itemHandler.deserializeNBT(nbt.getCompound("inventory"));
		this.progress = nbt.getInt("smasher.progress");
	}

	public void drops() {
		SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
		for (int i = 0; i < this.itemHandler.getSlots(); i++) {
			inventory.setItem(i, this.itemHandler.getStackInSlot(i));
		}

		Containers.dropContents(this.level, this.worldPosition, inventory);
	}

	public static void tick(Level pLevel, BlockPos pPos, BlockState pState, SmasherBlockEntity pBlockEntity) {
		if (hasRecipe(pBlockEntity)) {
			pBlockEntity.progress++;
			setChanged(pLevel, pPos, pState);
			if (pBlockEntity.progress > pBlockEntity.maxProgress) {
				craftItem(pBlockEntity);
			}
		} else {
			pBlockEntity.resetProgress();
			setChanged(pLevel, pPos, pState);
		}
	}

	private static boolean hasRecipe(SmasherBlockEntity entity) {
		Level level = entity.level;
		SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
		for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
			inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
		}

		Optional<SmasherBlockRecipe> match = level.getRecipeManager().getRecipeFor(SmasherBlockRecipe.Type.INSTANCE,
				inventory, level);

		return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
				&& canInsertItemIntoOutputSlot(inventory, match.get().getResultItem()) && hasFuelInFuelSlot(entity);
	}

	private static boolean hasFuelInFuelSlot(SmasherBlockEntity entity) {
		return entity.itemHandler.getStackInSlot(0).getItem() == Items.COAL;
	}

	private static void craftItem(SmasherBlockEntity entity) {
		Level level = entity.level;
		SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
		for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
			inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
		}

		Optional<SmasherBlockRecipe> match = level.getRecipeManager().getRecipeFor(SmasherBlockRecipe.Type.INSTANCE,
				inventory, level);

		if (match.isPresent()) {
			entity.itemHandler.extractItem(0, 1, false);
			entity.itemHandler.extractItem(1, 1, false);

			entity.itemHandler.setStackInSlot(2, new ItemStack(match.get().getResultItem().getItem(),
					entity.itemHandler.getStackInSlot(2).getCount() + 1));

			entity.resetProgress();
		}
	}

	private void resetProgress() {
		this.progress = 0;
	}

	private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
		return inventory.getItem(3).getItem() == output.getItem() || inventory.getItem(3).isEmpty();
	}

	private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
		return inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount();
	}
}