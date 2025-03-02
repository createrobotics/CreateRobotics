package com.workert.robotics.content.computers.ioblocks.scanner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
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
	protected void renderSafe(ScannerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		BlockState blockState = be.getBlockState();
		ScannerBehaviour pressingBehaviour = be.getScannerBehaviour();
		float renderedHeadOffset =
				pressingBehaviour.getRenderedHeadOffset(partialTicks);

		SuperByteBuffer headRender = CachedBuffers.partialFacing(AllPartialModels.MECHANICAL_PRESS_HEAD, blockState,
				blockState.getValue(HORIZONTAL_FACING));
		headRender.translate(0, -renderedHeadOffset, 0)
				.light(light)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	@Override
	protected BlockState getRenderedBlockState(ScannerBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}
}
