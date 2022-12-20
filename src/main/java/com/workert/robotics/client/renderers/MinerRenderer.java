package com.workert.robotics.client.renderers;

import com.workert.robotics.Robotics;
import com.workert.robotics.client.models.MinerModel;
import com.workert.robotics.entities.Miner;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MinerRenderer extends MobRenderer<Miner, MinerModel<Miner>> {

    public MinerRenderer(Context context) {
        super(context, new MinerModel<Miner>(context.bakeLayer(MinerModel.LAYER_LOCATION)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(Miner pEntity) {
        return new ResourceLocation(Robotics.MOD_ID, "textures/entity/miner.png");
    }

}