package com.workert.robotics.blockentities;

import java.util.List;

import com.simibubi.create.content.logistics.block.depot.DepotBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.INamedIconOptions;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.workert.robotics.lists.BlockEntityList;
import com.workert.robotics.lists.ItemList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public class CodeEditorBlockEntity extends SmartTileEntity {

	public DepotBehaviour depotBehaviour;

	public CodeEditorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityList.CODE_EDITOR_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		behaviours.add(this.depotBehaviour = new DepotBehaviour(this)
				.onlyAccepts(stack -> stack.getItem().equals(ItemList.PROGRAM.get())));
		this.depotBehaviour.addSubBehaviours(behaviours);
		behaviours.add(new ScrollOptionBehaviour<>(CodingOptions.class, Component.literal("Test"), this,
				new CenteredSideValueBoxTransform((blockState,
						direction) -> ((direction.equals(Direction.UP) || direction.equals(Direction.DOWN))) ? false
								: true)));

	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return this.depotBehaviour.getItemCapability(cap, side);
		return super.getCapability(cap, side);
	}

	static enum CodingOptions implements INamedIconOptions {

		HOUR_FIRST(AllIcons.I_HOUR_HAND_FIRST), MINUTE_FIRST(AllIcons.I_MINUTE_HAND_FIRST),
		HOUR_FIRST_24(AllIcons.I_HOUR_HAND_FIRST_24);

		private String translationKey;
		private AllIcons icon;

		private CodingOptions(AllIcons icon) {
			this.icon = icon;
			this.translationKey = "contraptions.clockwork." + Lang.asId(this.name());
		}

		@Override
		public AllIcons getIcon() {
			return this.icon;
		}

		@Override
		public String getTranslationKey() {
			return this.translationKey;
		}

	}
}
