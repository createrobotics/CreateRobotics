package com.workert.robotics.contraptions;

import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;

import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionType;
import com.simibubi.create.content.contraptions.components.structureMovement.NonStationaryLighter;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionLighter;
import com.simibubi.create.foundation.utility.UniqueLinkedList;
import com.workert.robotics.block.custom.DroneAssembler;
import com.workert.robotics.lists.EntityList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class DroneContraption extends Contraption {

	@Override
	public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
		if (!(world.getBlockState(pos).getBlock() instanceof DroneAssembler))
			return false;

		if (!this.searchMovedStructure(world, pos.above(), null))
			return false;

		Queue<BlockPos> frontier = new UniqueLinkedList<>();
		frontier.add(pos.above());

		if (world.getBlockState(pos.above()).getBlock() != Blocks.AIR)
			this.addBlock(pos.above(), Pair.of(
					new StructureTemplate.StructureBlockInfo(pos.above(), world.getBlockState(pos.above()), null),
					world.getBlockEntity(pos.above())));

		this.startMoving(world);

		if (this.blocks.isEmpty()) {
			System.out.println("blocks EMPTY!!!");
			return false;
		}

		return true;
	}

	@Override
	protected ContraptionType getType() {
		return EntityList.DRONE_CONTRAPTION;
	}

	@Override
	public boolean canBeStabilized(Direction facing, BlockPos localPos) {
		return false;
	}

	@Override
	protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
		return true;
	}

	@Override
	public ContraptionLighter<?> makeLighter() {
		return new NonStationaryLighter<Contraption>(this);
	}

}
