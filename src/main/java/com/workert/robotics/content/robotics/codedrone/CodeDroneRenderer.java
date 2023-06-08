package com.workert.robotics.content.robotics.codedrone;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CodeDroneRenderer extends GeoEntityRenderer<CodeDrone> {

	public CodeDroneRenderer(Context renderManager) {
		super(renderManager, new CodeDroneModel());
	}
}
