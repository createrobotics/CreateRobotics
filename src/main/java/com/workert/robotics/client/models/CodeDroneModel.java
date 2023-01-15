package com.workert.robotics.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.workert.robotics.Robotics;
import com.workert.robotics.entities.CodeDrone;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class CodeDroneModel<T extends CodeDrone> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(Robotics.MOD_ID, "code_drone"), "code_drone");
	private final ModelPart body;
	private final ModelPart fan0;
	private final ModelPart fan1;
	private final ModelPart fan2;
	private final ModelPart fan3;

	public CodeDroneModel(ModelPart root) {
		this.body = root.getChild("body");
		this.fan0 = root.getChild("fan0");
		this.fan1 = root.getChild("fan1");
		this.fan2 = root.getChild("fan2");
		this.fan3 = root.getChild("fan3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
						.addBox(-2.5F, -2.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 21.0F, 0.0F));

		PartDefinition fan0 = partdefinition.addOrReplaceChild("fan0", CubeListBuilder.create().texOffs(0, 0)
						.addBox(-5.5F, -0.5F, -5.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-6.5F, -1.0F, -6.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-6.5F, -1.0F, -5.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-6.5F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, -5.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.5F, 19.5F, -2.5F));

		PartDefinition fan1 = partdefinition.addOrReplaceChild("fan1", CubeListBuilder.create().texOffs(0, 0)
						.addBox(-5.5F, -0.5F, 0.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-6.5F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-6.5F, -1.0F, 0.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-6.5F, -1.0F, 5.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, 0.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.5F, 19.5F, 2.5F));

		PartDefinition fan2 = partdefinition.addOrReplaceChild("fan2", CubeListBuilder.create().texOffs(0, 0)
						.addBox(0.5F, -0.5F, -5.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, -6.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, -5.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(5.5F, -1.0F, -5.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(2.5F, 19.5F, -2.5F));

		PartDefinition fan3 = partdefinition.addOrReplaceChild("fan3", CubeListBuilder.create().texOffs(0, 0)
						.addBox(0.5F, -0.5F, 0.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, 0.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.5F, -1.0F, 5.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(5.5F, -1.0F, 0.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(2.5F, 19.5F, 2.5F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fan0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fan1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fan2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.fan3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}
