package com.workert.robotics.content.robotics.flyingtoolbox;
import com.workert.robotics.Robotics;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FlyingToolboxRenderer extends MobRenderer<FlyingToolbox, FlyingToolboxModel<FlyingToolbox>> {

	public FlyingToolboxRenderer(EntityRendererProvider.Context context) {
		super(context, new FlyingToolboxModel<>(context.bakeLayer(FlyingToolboxModel.LAYER_LOCATION)), 0.6F);
	}

	@Override
	public ResourceLocation getTextureLocation(FlyingToolbox pEntity) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/flying_toolbox.png");
	}
}
