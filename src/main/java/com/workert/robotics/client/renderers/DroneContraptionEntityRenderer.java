package com.workert.robotics.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.workert.robotics.entities.DroneContraptionEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DroneContraptionEntityRenderer extends EntityRenderer<DroneContraptionEntity> {

	protected DroneContraptionEntityRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(DroneContraptionEntity pEntity) {
		return null;
	}

	@Override
	public boolean shouldRender(DroneContraptionEntity entity, Frustum frustum, double cameraX, double cameraY,
								double cameraZ) {
		if (entity.getContraption() == null) return false;
		if (!entity.isAlive()) return false;
		return super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ);
	}

	@Override
	public void render(DroneContraptionEntity entity, float yaw, float partialTicks, PoseStack ms,
					   MultiBufferSource buffers, int overlay) {

		super.render(entity, yaw, partialTicks, ms, buffers, overlay);
		Contraption contraption = entity.getContraption();
		if (contraption != null) {
			ContraptionRenderDispatcher.renderFromEntity(entity, contraption, buffers);
		}
	}

}
