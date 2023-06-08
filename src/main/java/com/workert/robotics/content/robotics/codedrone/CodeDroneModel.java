package com.workert.robotics.content.robotics.codedrone;

import com.workert.robotics.Robotics;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CodeDroneModel extends AnimatedGeoModel<CodeDrone> {

	@Override
	public ResourceLocation getModelResource(CodeDrone animatable) {
		return new ResourceLocation(Robotics.MOD_ID, "geo/code_drone.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CodeDrone animatable) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/code_drone.png");
	}

	@Override
	public ResourceLocation getAnimationResource(CodeDrone animatable) {
		return new ResourceLocation(Robotics.MOD_ID, "animations/code_drone.animation.json");
	}
}
