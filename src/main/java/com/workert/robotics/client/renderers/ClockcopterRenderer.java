package com.workert.robotics.client.renderers;

import com.workert.robotics.Robotics;
import com.workert.robotics.client.models.ClockcopterModel;
import com.workert.robotics.entities.Clockcopter;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ClockcopterRenderer extends MobRenderer<Clockcopter, ClockcopterModel<Clockcopter>> {

	public ClockcopterRenderer(Context context) {
		super(context, new ClockcopterModel<Clockcopter>(context.bakeLayer(ClockcopterModel.LAYER_LOCATION)), 0.6F);
	}

	@Override
	public ResourceLocation getTextureLocation(Clockcopter pEntity) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/clockcopter.png");
	}

}
