package com.workert.robotics.blockentities;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.workert.robotics.blocks.DroneAssembler;
import com.workert.robotics.contraptions.DroneContraption;
import com.workert.robotics.entities.DroneContraptionEntity;
import com.workert.robotics.lists.BlockEntityList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DroneAssemblerBlockEntity extends BlockEntity {

	public DroneAssemblerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(BlockEntityList.DRONE_ASSEMBLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
	}

	public void tryAssemble() {

		if (!(this.level instanceof ServerLevel)) return;

		BlockState blockState = this.getBlockState();
		if (!(blockState.getBlock() instanceof DroneAssembler)) return;

		DroneContraption contraption = new DroneContraption();

		try {
			if (!contraption.assemble(this.level, this.worldPosition)) return;
		} catch (AssemblyException e) {
			return;
		}

		contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);
		DroneContraptionEntity movedContraption = DroneContraptionEntity.create(this.level, contraption);

		BlockPos anchor = this.worldPosition.above();
		movedContraption.setPos(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5);

		AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.level, this.worldPosition);

		this.level.addFreshEntity(movedContraption);
	}
}
