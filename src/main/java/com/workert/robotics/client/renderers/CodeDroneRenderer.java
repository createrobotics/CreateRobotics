package com.workert.robotics.client.renderers;

import com.workert.robotics.client.models.CodeDroneModel;
import com.workert.robotics.entities.CodeDrone;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CodeDroneRenderer extends GeoEntityRenderer<CodeDrone> {

	public CodeDroneRenderer(Context renderManager) {
		super(renderManager, new CodeDroneModel());
	}
}
