package com.workert.robotics.content.computers.ioblocks.scanner;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class ScannerRenderer extends KineticBlockEntityRenderer<ScannerBlockEntity> {


	public ScannerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(ScannerBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(ScannerBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {
		super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

		if (Backend.canUseInstancing(blockEntity.getLevel()))
			return;

		BlockState blockState = blockEntity.getBlockState();
		ScannerBehaviour pressingBehaviour = blockEntity.processingBehaviour;
		float renderedHeadOffset = pressingBehaviour.getRenderedHeadOffset(partialTicks) * 19f / 16f;

		SuperByteBuffer headRender = CachedBufferer.partialFacing(AllPartialModels.MECHANICAL_PRESS_HEAD, blockState,
				blockState.getValue(HORIZONTAL_FACING));
		headRender.translate(-renderedHeadOffset, 0, 0)
				.light(light)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	@Override
	protected BlockState getRenderedBlockState(ScannerBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}
}
