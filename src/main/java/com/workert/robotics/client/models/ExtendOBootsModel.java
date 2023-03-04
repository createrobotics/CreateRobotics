package com.workert.robotics.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.workert.robotics.entities.ExtendOBoots;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ExtendOBootsModel<T extends ExtendOBoots> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "extend_o_boots"), "extend_o_boots");
	private final ModelPart posts;
	private final ModelPart base;

	public ExtendOBootsModel(ModelPart root) {
		this.posts = root.getChild("posts");
		this.base = root.getChild("base");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition posts = partdefinition.addOrReplaceChild("posts", CubeListBuilder.create().texOffs(0, -16)
						.addBox(-8.0F, -80.0F, -8.0F, 0.0F, 80.0F, 16.0F, new CubeDeformation(0.0F))
						.texOffs(0, -16).addBox(8.0F, -80.0F, -8.0F, 0.0F, 80.0F, 16.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-8.0F, -80.0F, -8.0F, 16.0F, 80.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-8.0F, -80.0F, 8.0F, 16.0F, 80.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(16, 0)
						.addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.base.y = -(entity.getHeight() * 16) + 24;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.posts.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
