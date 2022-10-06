package com.workert.robotics.contraptions;

import org.apache.commons.lang3.tuple.Pair;

import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionType;
import com.workert.robotics.entities.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class DroneContraption extends Contraption {

	@Override
	public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
		if (!this.searchMovedStructure(world, pos.above(), null))
			return false;

		if (world.getBlockState(pos.above()).getBlock() != Blocks.AIR)
			this.addBlock(pos.above(), Pair.of(
					new StructureTemplate.StructureBlockInfo(pos.above(), world.getBlockState(pos.above()), null),
					world.getBlockEntity(pos.above())));

		if (this.blocks.isEmpty())
			return false;

		this.startMoving(world);
		return true;
	}

	@Override
	protected ContraptionType getType() {
		return ModEntities.DRONE_CONTRAPTION;
	}

	@Override
	public boolean canBeStabilized(Direction facing, BlockPos localPos) {
		return false;
	}

	@Override
	protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
		return true;
	}

}
