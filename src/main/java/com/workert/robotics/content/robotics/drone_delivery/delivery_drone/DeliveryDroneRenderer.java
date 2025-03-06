package com.workert.robotics.content.robotics.drone_delivery.delivery_drone;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DeliveryDroneRenderer extends EntityRenderer<DeliveryDroneEntity> {

	public DeliveryDroneRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(DeliveryDroneEntity codeDrone) {
		return null;
	}
}
