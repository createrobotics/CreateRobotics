package com.workert.robotics.content.computing.computer;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class ComputerRenderer extends KineticTileEntityRenderer {
	public ComputerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticTileEntity te, BlockState state) {
		return CachedBufferer.partial(AllBlockPartials.SHAFTLESS_COGWHEEL, state);
	}
}
