package com.workert.robotics.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.workert.robotics.Robotics;
import com.workert.robotics.entities.ExtendOBoots;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ExtendOBootsRenderer extends EntityRenderer<ExtendOBoots> {
	public ExtendOBootsRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}

	@Override
	public void render(ExtendOBoots pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
			MultiBufferSource pBuffer, int pPackedLight) {
		
		super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(ExtendOBoots pEntity) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/extend_o_boots.png");
	}

}
