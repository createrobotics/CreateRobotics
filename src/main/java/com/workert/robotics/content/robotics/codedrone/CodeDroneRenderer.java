package com.workert.robotics.content.robotics.codedrone;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class CodeDroneRenderer extends GeoEntityRenderer<CodeDrone> {

	public CodeDroneRenderer(Context renderManager) {
		super(renderManager, new CodeDroneModel());
	}
}
