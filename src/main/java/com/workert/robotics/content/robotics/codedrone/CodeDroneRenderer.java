package com.workert.robotics.content.robotics.codedrone;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CodeDroneRenderer extends EntityRenderer<CodeDrone> {

	public CodeDroneRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(CodeDrone codeDrone) {
		return null;
	}
}
