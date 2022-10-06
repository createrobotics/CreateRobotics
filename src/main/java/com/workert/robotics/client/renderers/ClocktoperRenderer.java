package com.workert.robotics.client.renderers;

import com.workert.robotics.Robotics;
import com.workert.robotics.client.model.ClocktoperModel;
import com.workert.robotics.entities.Clocktoper;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ClocktoperRenderer extends MobRenderer<Clocktoper, ClocktoperModel<Clocktoper>> {

	public ClocktoperRenderer(Context context) {
		super(context, new ClocktoperModel<Clocktoper>(context.bakeLayer(ClocktoperModel.LAYER_LOCATION)), 0.15F);
	}

	@Override
	public ResourceLocation getTextureLocation(Clocktoper pEntity) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/clocktoper.png");
	}

}
