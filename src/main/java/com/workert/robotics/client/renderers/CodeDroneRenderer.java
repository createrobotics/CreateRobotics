package com.workert.robotics.client.renderers;

import com.workert.robotics.Robotics;
import com.workert.robotics.client.models.CodeDroneModel;
import com.workert.robotics.entities.CodeDrone;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CodeDroneRenderer extends MobRenderer<CodeDrone, CodeDroneModel<CodeDrone>> {

	public CodeDroneRenderer(Context context) {
		super(context, new CodeDroneModel<CodeDrone>(context.bakeLayer(CodeDroneModel.LAYER_LOCATION)), 0.6F);
	}

	@Override
	public ResourceLocation getTextureLocation(CodeDrone pEntity) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/code_drone.png");
	}

}
