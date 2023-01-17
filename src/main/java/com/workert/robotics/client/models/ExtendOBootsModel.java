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
	private final ModelPart post0;
	private final ModelPart post1;

	public ExtendOBootsModel(ModelPart root) {
		this.post0 = root.getChild("post0");
		this.post1 = root.getChild("post1");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition post0 = partdefinition.addOrReplaceChild("post0", CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = post0.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, -16)
						.addBox(0.0F, -80.0F, -8.0F, 0.0F, 80.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r2 = post0.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, -16)
						.addBox(0.0F, -80.0F, -8.0F, 0.0F, 80.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition post1 = partdefinition.addOrReplaceChild("post1", CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r3 = post1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, -16)
						.addBox(0.0F, -80.0F, -8.0F, 0.0F, 80.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r4 = post1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, -16)
						.addBox(0.0F, -80.0F, -8.0F, 0.0F, 80.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
						  float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
							   float red, float green, float blue, float alpha) {
		this.post0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.post1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
