package com.workert.robotics.content.robotics.miner;

import com.workert.robotics.Robotics;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MinerRenderer extends MobRenderer<Miner, MinerModel<Miner>> {

	public MinerRenderer(Context context) {
		super(context, new MinerModel<>(context.bakeLayer(MinerModel.LAYER_LOCATION)), 0.6F);
	}

	@Override
	public ResourceLocation getTextureLocation(Miner pEntity) {
		return new ResourceLocation(Robotics.MOD_ID, "textures/entity/miner.png");
	}

}