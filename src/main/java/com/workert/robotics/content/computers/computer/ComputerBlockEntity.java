package com.workert.robotics.content.computers.computer;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.utility.Iterate;
import com.workert.robotics.base.registries.BlockEntityRegistry;
import com.workert.robotics.base.roboscript.RoboScript;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import java.util.LinkedHashSet;

public class ComputerBlockEntity extends KineticTileEntity {

	private static final int NEIGHBOUR_CHECKING = 100;
	private int neighbourCheckCooldown;

	private String script = "";
	private String terminal = "";
	private boolean running = false;

	private LinkedHashSet<LazyOptional<IItemHandler>> attachedInventories;

	public RoboScript roboScript;

	public ComputerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(BlockEntityRegistry.COMPUTER.get(), blockPos, blockState);
		this.attachedInventories = new LinkedHashSet<>();
		this.roboScript = new RoboScript() {
			@Override
			public RunningState getRunningState() {
				if (!ComputerBlockEntity.this.isSpeedRequirementFulfilled())
					return RunningState.ENERGY_REQUIREMENT_NOT_MET;
				return super.getRunningState();
			}
		};
	}


	public void setScript(String script) {
		this.script = script;
	}

	public String getScript() {
		return this.script;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getTerminal() {
		return this.terminal;
	}


	public void findInventories() {
		this.attachedInventories.clear();

		for (Direction facing : Iterate.directions) {
			if (!this.level.isLoaded(this.worldPosition.relative(facing))) continue;
			BlockEntity tileEntity = this.level.getBlockEntity(this.worldPosition.relative(facing));

			if (tileEntity != null) {
				LazyOptional<IItemHandler> capability = tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER,
						facing.getOpposite());
				if (capability.isPresent()) {
					this.attachedInventories.add(capability);
				}
			}
		}
	}

	public Object readPlate(String identifier) {
		this.attachedInventories.removeIf(cap -> !cap.isPresent());
		for (LazyOptional<IItemHandler> cap : this.attachedInventories) {
			IItemHandler iItemHandler = cap.orElse(EmptyHandler.INSTANCE);
			for (int i = 0; i < iItemHandler.getSlots(); i++) {
				ItemStack currentStack = iItemHandler.getStackInSlot(i);
				if (currentStack.getItem() == AllItems.WRENCH.get() && currentStack.hasTag() && currentStack.getTag()
						.getString("Identifier").equals(identifier)) {
					CompoundTag compoundTag = currentStack.getTag();
					Object value;
					Byte b = compoundTag.getTagType("Value");
					if (b == Tag.TAG_DOUBLE) {
						value = compoundTag.getDouble("Value");
					} else if (b == Tag.TAG_STRING) {
						value = compoundTag.getString("Value");
					} else if (b == Tag.TAG_BYTE) {
						value = compoundTag.getBoolean("Value");
					} else {
						value = null;
					}
					return value;
				}
			}
		}
		return null;
	}

	public boolean findAndModifyPlate(String identifier, Object data) {
		this.attachedInventories.removeIf(cap -> !cap.isPresent());
		int firstEmptyAvailableSlot = -1;
		IItemHandler firstEmptyAvailableSlotIItemHandler = null;
		int matchingIdentifierSlot = -1;
		IItemHandler matchingIdentifierSlotIItemHandler = null;
		for (LazyOptional<IItemHandler> cap : this.attachedInventories) {
			IItemHandler iItemHandler = cap.orElse(EmptyHandler.INSTANCE);
			for (int i = 0; i < iItemHandler.getSlots(); i++) {
				ItemStack currentStack = iItemHandler.getStackInSlot(i);
				if (currentStack.getItem() == AllItems.WRENCH.get()) {
					if (!currentStack.hasTag()) {
						//iItemHandler.insertItem(i, writeToPlate(identifier,data,iItemHandler.extractItem(i,1, false)), false);
						if (firstEmptyAvailableSlot == -1) {
							firstEmptyAvailableSlot = i;
							firstEmptyAvailableSlotIItemHandler = iItemHandler;
						}
					} else if (currentStack.getTag().getString("Identifier").equals(identifier)) {
						//iItemHandler.insertItem(i, writeToPlate(identifier,data,iItemHandler.extractItem(i,1, false)), false);
						if (matchingIdentifierSlot == -1) {
							matchingIdentifierSlot = i;
							matchingIdentifierSlotIItemHandler = iItemHandler;
						}
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
		}
		if (matchingIdentifierSlot != -1) {
			matchingIdentifierSlotIItemHandler.insertItem(matchingIdentifierSlot, this.writeToPlate(identifier, data,
					matchingIdentifierSlotIItemHandler.extractItem(matchingIdentifierSlot, 1, false)), false);
			return true;
		} else if (firstEmptyAvailableSlot != -1) {
			firstEmptyAvailableSlotIItemHandler.insertItem(firstEmptyAvailableSlot, this.writeToPlate(identifier, data,
					firstEmptyAvailableSlotIItemHandler.extractItem(firstEmptyAvailableSlot, 1, false)), false);
			return true;
		}
		return false;
	}

	private ItemStack writeToPlate(String identifier, Object data, ItemStack modifiablePlate) {

		CompoundTag compoundTag = new CompoundTag();
		if (data instanceof Double d) {
			compoundTag.putString("Identifier", identifier);
			compoundTag.putDouble("Value", d);
			modifiablePlate.setTag(compoundTag);
			modifiablePlate.setHoverName(
					Component.literal(identifier).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC));
		} else if (data instanceof String s) {
			compoundTag.putString("Identifier", identifier);
			compoundTag.putString("Value", s);
			modifiablePlate.setTag(compoundTag);
			modifiablePlate.setHoverName(
					Component.literal(identifier).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC));
		} else if (data instanceof Boolean b) {
			compoundTag.putString("Identifier", identifier);
			compoundTag.putBoolean("Value", b);
			modifiablePlate.setTag(compoundTag);
			modifiablePlate.setHoverName(
					Component.literal(identifier).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC));
		}
		return modifiablePlate;
	}


	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		this.script = compound.getString("Script");
		this.terminal = compound.getString("Terminal");
		this.running = compound.getBoolean("Running");
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("Script", this.script);
		compound.putString("Terminal", this.terminal);
		compound.putBoolean("Running", this.running);

	}

	@Override
	public void tick() {
		super.tick();
		if (!this.isSpeedRequirementFulfilled()) {
			this.roboScript.requestStop();
			this.setTerminal(this.getTerminal() + "ERROR: Speed requirement not fulfilled, stopped program." + "\n");
		}

		if (this.neighbourCheckCooldown-- <= 0) {
			this.neighbourCheckCooldown = NEIGHBOUR_CHECKING;
			this.findInventories();
		}
	}
}
