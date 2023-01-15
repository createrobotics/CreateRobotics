package com.workert.robotics.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.block.depot.DepotRenderer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.workert.robotics.blockentities.CodeEditorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CodeEditorRenderer extends SafeTileEntityRenderer<CodeEditorBlockEntity> {

	public CodeEditorRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(CodeEditorBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		DepotRenderer.renderItemsOf(te, partialTicks, ms, buffer, light, overlay, te.depotBehaviour);
	}

}
