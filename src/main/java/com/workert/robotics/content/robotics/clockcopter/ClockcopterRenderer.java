package com.workert.robotics.content.robotics.clockcopter;

import com.workert.robotics.Robotics;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ClockcopterRenderer extends MobRenderer<Clockcopter, ClockcopterModel<Clockcopter>> {

	public ClockcopterRenderer(Context context) {
		super(context, new ClockcopterModel<>(context.bakeLayer(ClockcopterModel.LAYER_LOCATION)), 0.6F);
	}

	@Override
	public ResourceLocation getTextureLocation(Clockcopter pEntity) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/clockcopter.png");
	}

}
