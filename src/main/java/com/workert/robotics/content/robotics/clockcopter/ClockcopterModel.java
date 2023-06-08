package com.workert.robotics.content.robotics.clockcopter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.workert.robotics.Robotics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ClockcopterModel<T extends Clockcopter> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(Robotics.MOD_ID, "clockcopter"), "clockcopter");

	private static final float gearTurnSpeed = 0.3f;
	private static final float propellerTurnSpeed = 0.3f;

	private final ModelPart body;
	private final ModelPart gear0;
	private final ModelPart gear1;
	private final ModelPart arm0;
	private final ModelPart arm1;
	private final ModelPart propeller;

	public ClockcopterModel(ModelPart root) {
		this.body = root.getChild("body");
		this.gear0 = root.getChild("gear0");
		this.gear1 = root.getChild("gear1");
		this.arm0 = root.getChild("arm0");
		this.arm1 = root.getChild("arm1");
		this.propeller = root.getChild("propeller");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
						.addBox(-7.0F, -13.0F, -7.0F, 13.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)).texOffs(16, 26)
						.addBox(-5.0F, -5.09F, -7.7F, 9.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 26)
						.addBox(-5.0F, -11.0F, -7.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 26)
						.addBox(1.0F, -11.0F, -7.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 37)
						.addBox(-4.0F, -7.0F, 6.0F, 7.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition propellerBase = body.addOrReplaceChild("propellerBase", CubeListBuilder.create().texOffs(0, 11)
						.addBox(-3.0F, -29.5F, 3.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(34, 34)
						.addBox(1.0F, -29.5F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(34, 34)
						.addBox(-4.0F, -29.5F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 11)
						.addBox(-3.0F, -29.5F, -3.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(34, 34)
						.addBox(-4.0F, -29.5F, 2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(34, 34)
						.addBox(1.0F, -29.5F, 2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 16.0F, -1.0F));

		PartDefinition cubiertaparte2_r1 = propellerBase.addOrReplaceChild("cubiertaparte2_r1",
				CubeListBuilder.create().texOffs(0, 33)
						.addBox(-2.0F, -13.5F, -4.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 33)
						.addBox(-2.0F, -13.5F, 2.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -16.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition gear0 = partdefinition.addOrReplaceChild("gear0", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-8.0F, 16.5F, -0.5F));

		PartDefinition cube_r1 = gear0.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.8727F, 0.0F, 0.0F));

		PartDefinition cube_r2 = gear0.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r3 = gear0.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, 0.0F, 0.0F));

		PartDefinition gear1 = partdefinition.addOrReplaceChild("gear1", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offset(7.0F, 16.5F, -0.5F));

		PartDefinition cube_r4 = gear1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.8727F, 0.0F, 0.0F));

		PartDefinition cube_r5 = gear1.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r6 = gear1.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(29, 26)
						.addBox(-1.0F, -0.5F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, 0.0F, 0.0F));

		PartDefinition arm0 = partdefinition.addOrReplaceChild("arm0", CubeListBuilder.create(),
				PartPose.offset(-8.5F, 16.5F, -0.5F));

		PartDefinition axis2 = arm0.addOrReplaceChild("axis2", CubeListBuilder.create().texOffs(0, 31)
						.addBox(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)),
				PartPose.offset(-0.5F, 0.0F, 0.0F));

		PartDefinition arm2 = arm0.addOrReplaceChild("arm2", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-3.085F, 0.3535F, -10.5729F, 0.9163F, 0.0F, -3.0369F));

		PartDefinition ClawConnector_r1 = arm2.addOrReplaceChild("ClawConnector_r1",
				CubeListBuilder.create().texOffs(16, 32)
						.addBox(-1.53F, -1.0207F, 0.8835F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(36, 40)
						.addBox(-2.03F, -2.0207F, -1.1165F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 34)
						.mirror().addBox(-2.03F, -2.0207F, 1.8835F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
						.mirror(false), PartPose.offsetAndRotation(0.015F, -2.9328F, -0.0106F, -0.5672F, 0.0F, 0.0F));

		PartDefinition ConnectorMid_r1 = arm2.addOrReplaceChild("ConnectorMid_r1",
				CubeListBuilder.create().texOffs(39, 8)
						.addBox(-1.0F, -17.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(38, 34)
						.addBox(-2.5F, -18.0F, -7.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(40, 26)
						.addBox(-2.0F, -17.0F, -5.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.015F, 16.5465F, -2.1271F, -0.7854F, 0.0F, 0.0F));

		PartDefinition ConnectorR_r1 = arm2.addOrReplaceChild("ConnectorR_r1", CubeListBuilder.create().texOffs(40, 26)
						.addBox(-1.0F, 0.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.985F, 3.1115F, 8.4795F, -0.7854F, 0.0F, 0.0F));

		PartDefinition LowerBody_r1 = arm2.addOrReplaceChild("LowerBody_r1", CubeListBuilder.create().texOffs(39, 0)
						.addBox(-2.0F, -2.5F, -1.5F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.015F, 8.4148F, 6.0046F, -0.8552F, 0.0F, 0.0F));

		PartDefinition pinzas2 = arm2.addOrReplaceChild("pinzas2", CubeListBuilder.create(),
				PartPose.offset(22.385F, -2.8027F, -4.1108F));

		PartDefinition down2 = pinzas2.addOrReplaceChild("down2", CubeListBuilder.create(),
				PartPose.offset(-22.4F, -2.6049F, -0.4735F));

		PartDefinition ClawTop_r1 = down2.addOrReplaceChild("ClawTop_r1", CubeListBuilder.create().texOffs(11, 38)
						.addBox(-1.53F, -4.039F, -4.5027F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.03F, 3.539F, 2.0027F, -0.6981F, 0.0F, 0.0F));

		PartDefinition up2 = pinzas2.addOrReplaceChild("up2", CubeListBuilder.create(),
				PartPose.offset(-22.4F, 1.2691F, 0.9024F));

		PartDefinition ClawBottom_r1 = up2.addOrReplaceChild("ClawBottom_r1", CubeListBuilder.create().texOffs(11, 38)
						.addBox(-1.53F, 0.8992F, -5.6979F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.03F, -1.3992F, 3.1979F, -0.2618F, 0.0F, 0.0F));

		PartDefinition arm1 = partdefinition.addOrReplaceChild("arm1", CubeListBuilder.create(),
				PartPose.offset(7.5F, 16.5F, -0.5F));

		PartDefinition axis1 = arm1.addOrReplaceChild("axis1", CubeListBuilder.create().texOffs(0, 31)
						.addBox(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)),
				PartPose.offset(0.5F, 0.0F, 0.0F));

		PartDefinition arm = arm1.addOrReplaceChild("arm", CubeListBuilder.create(),
				PartPose.offsetAndRotation(3.315F, 0.1835F, -10.7229F, 0.9163F, 0.0F, 2.9409F));

		PartDefinition ClawConnector_r2 = arm.addOrReplaceChild("ClawConnector_r2",
				CubeListBuilder.create().texOffs(16, 32)
						.addBox(-1.53F, -1.0207F, 0.8835F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(36, 40)
						.addBox(-2.03F, -2.0207F, -1.1165F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 34)
						.addBox(-2.03F, -2.0207F, 1.8835F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.015F, -2.7628F, 0.1394F, -0.5672F, 0.0F, 0.0F));

		PartDefinition ConnectorMid_r2 = arm.addOrReplaceChild("ConnectorMid_r2",
				CubeListBuilder.create().texOffs(39, 8)
						.addBox(-1.0F, -17.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(38, 34)
						.addBox(-2.5F, -18.0F, -7.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(40, 26)
						.addBox(-2.0F, -17.0F, -5.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.015F, 16.7165F, -1.9771F, -0.7854F, 0.0F, 0.0F));

		PartDefinition ConnectorR_r2 = arm.addOrReplaceChild("ConnectorR_r2", CubeListBuilder.create().texOffs(40, 26)
						.addBox(-1.0F, 0.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.985F, 3.2815F, 8.6295F, -0.7854F, 0.0F, 0.0F));

		PartDefinition LowerBody_r2 = arm.addOrReplaceChild("LowerBody_r2", CubeListBuilder.create().texOffs(39, 0)
						.addBox(-1.9F, -2.5F, -0.5F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.015F, 8.5848F, 6.1546F, -0.8552F, 0.0F, 0.0F));

		PartDefinition pinzas = arm.addOrReplaceChild("pinzas", CubeListBuilder.create(),
				PartPose.offset(-0.015F, -2.6327F, -3.9608F));

		PartDefinition down = pinzas.addOrReplaceChild("down", CubeListBuilder.create(),
				PartPose.offset(0.0F, -3.3691F, 3.2976F));

		PartDefinition ClawTop_r2 = down.addOrReplaceChild("ClawTop_r2", CubeListBuilder.create().texOffs(11, 38)
						.addBox(-1.53F, -4.039F, -7.0027F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.03F, 5.539F, 0.5027F, -0.6981F, 0.0F, 0.0F));

		PartDefinition up = pinzas.addOrReplaceChild("up", CubeListBuilder.create(),
				PartPose.offset(0.0F, 1.2691F, 0.6024F));

		PartDefinition ClawBottom_r2 = up.addOrReplaceChild("ClawBottom_r2", CubeListBuilder.create().texOffs(11, 38)
						.addBox(-1.53F, 0.8992F, -5.9979F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.03F, -1.3992F, 3.4979F, -0.2618F, 0.0F, 0.0F));

		PartDefinition propeller = partdefinition.addOrReplaceChild("propeller", CubeListBuilder.create().texOffs(0, 26)
						.addBox(-1.5F, -4.0F, -11.5F, 3.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-1.5F, -5.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-0.5F, 9.0F, -0.5F));

		PartDefinition Blade2_r1 = propeller.addOrReplaceChild("Blade2_r1", CubeListBuilder.create().texOffs(0, 26)
						.addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.5F, -3.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition Blade1_r1 = propeller.addOrReplaceChild("Blade1_r1", CubeListBuilder.create().texOffs(0, 26)
						.addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -3.5F, 6.5F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition Blade3_r1 = propeller.addOrReplaceChild("Blade3_r1", CubeListBuilder.create().texOffs(0, 26)
						.addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-6.5F, -3.5F, 0.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.gear0.xRot += gearTurnSpeed;
		this.gear1.xRot -= gearTurnSpeed;
		if (entity.isFlying()) {
			this.propeller.yRot += propellerTurnSpeed;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.gear0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.gear1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.arm0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.arm1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.propeller.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}