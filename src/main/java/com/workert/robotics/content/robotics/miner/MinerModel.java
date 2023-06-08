package com.workert.robotics.content.robotics.miner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.workert.robotics.Robotics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class MinerModel<T extends Miner> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(Robotics.MOD_ID, "miner"), "miner");
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart legs;

	public MinerModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.legs = root.getChild("legs");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 24)
						.addBox(0.7688F, -1.1563F, -1.9125F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(0, 52)
						.addBox(-0.2312F, -2.1563F, 2.0875F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(49, 24)
						.addBox(-0.2312F, -2.1563F, -3.9125F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(0.2688F, -2.1563F, -2.9125F, 1.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(49, 24)
						.addBox(-4.4813F, -3.1563F, -4.4125F, 6.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(6, 24)
						.addBox(0.5188F, 2.5938F, 2.3875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(46, 52)
						.addBox(-4.2313F, -2.1563F, -3.9125F, 4.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(69, 13)
						.addBox(-5.2313F, -5.1563F, -1.4125F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(1.2312F, -4.8438F, 0.4125F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition compartement = body.addOrReplaceChild("compartement",
				CubeListBuilder.create().texOffs(0, 39).mirror()
						.addBox(-8.0F, -23.0F, -40.0F, 16.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(35, 39).addBox(-8.0F, -23.0F, -25.0F, 16.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(23, 52).addBox(10.0F, -23.0F, -37.0F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 52).addBox(-11.0F, -23.0F, -37.0F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 1.0F, 32.0F));

		PartDefinition base = body.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0)
						.addBox(-10.0F, -23.0F, -7.0F, 20.0F, 9.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(0, 24)
						.addBox(-9.0F, -25.0F, -6.0F, 18.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = base.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(39, 48)
						.addBox(-9.0F, -1.4F, -1.5F, 18.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -23.8F, 6.95F, 0.6545F, 0.0F, 0.0F));

		PartDefinition cube_r2 = base.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(61, 34)
						.addBox(-6.0F, 9.9F, 13.7F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(8.9492F, -23.4971F, -0.0004F, 0.6545F, -1.5708F, 0.0F));

		PartDefinition cube_r3 = base.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 48)
						.addBox(-9.0F, -1.3F, -0.05F, 18.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -23.5F, -6.5F, -0.6545F, 0.0F, 0.0F));

		PartDefinition cube_r4 = base.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(62, 52)
						.addBox(-0.04F, -6.5359F, 6.6604F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(18.0F, -23.4641F, -5.9604F, -0.6545F, -1.5708F, 0.0F));

		PartDefinition legs = partdefinition.addOrReplaceChild("legs", CubeListBuilder.create(),
				PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition leg1 = legs.addOrReplaceChild("leg1", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, -3.0F, 0.4F, 0.0F, 0.7854F, 0.0F));

		PartDefinition front_right = leg1.addOrReplaceChild("front_right", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-30.3054F, -7.9929F, 11.1779F, 0.0F, -1.5708F, 0.0F));

		PartDefinition axis = front_right.addOrReplaceChild("axis", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r5 = axis.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(36, 52)
						.addBox(-1.5953F, -10.8056F, 30.9968F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, -0.7854F, 3.1416F));

		PartDefinition foot = front_right.addOrReplaceChild("foot", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r6 = foot.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(8, 0)
						.addBox(32.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(8, 0)
						.addBox(30.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -10.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r7 = foot.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(13, 52)
						.addBox(29.9004F, -0.3056F, -1.2369F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(61, 75)
						.addBox(29.9004F, 4.6944F, -1.2369F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(25, 52)
						.addBox(30.9004F, 3.6944F, -0.2369F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition up = front_right.addOrReplaceChild("up", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r8 = up.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0)
						.addBox(30.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(32.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r9 = up.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(65, 62)
						.addBox(-2.5F, -4.0F, -2.0F, 5.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-16.0942F, -19.25F, -53.4283F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cog = leg1.addOrReplaceChild("cog", CubeListBuilder.create().texOffs(62, 56)
						.addBox(27.25F, -1.05F, -36.73F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(91, 41)
						.addBox(28.55F, -1.05F, -38.03F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-31.3982F, -7.55F, 10.815F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r10 = cog.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r11 = cog.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r12 = cog.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leg2 = legs.addOrReplaceChild("leg2", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, -3.0F, 0.3F, 0.0F, 0.7854F, 0.0F));

		PartDefinition front_left = leg2.addOrReplaceChild("front_left", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-30.3054F, -7.9929F, 11.1779F, 0.0F, -1.5708F, 0.0F));

		PartDefinition up2 = front_left.addOrReplaceChild("up2", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r13 = up2.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(65, 62)
						.addBox(-14.5F, -4.0F, -2.0F, 5.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-16.0942F, -19.25F, -53.4283F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r14 = up2.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 0)
						.addBox(20.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(18.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition foot2 = front_left.addOrReplaceChild("foot2", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r15 = foot2.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(8, 0)
						.addBox(18.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(8, 0)
						.addBox(20.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -10.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r16 = foot2.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(61, 75)
						.addBox(17.9004F, 4.6944F, -1.2369F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(13, 52)
						.addBox(17.9004F, -0.3056F, -1.2369F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(25, 52)
						.addBox(18.9004F, 3.6944F, -0.2369F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition axis2 = front_left.addOrReplaceChild("axis2", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r17 = axis2.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(36, 52)
						.addBox(-1.5953F, -10.8056F, 18.9968F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, -0.7854F, 3.1416F));

		PartDefinition cog2 = leg2.addOrReplaceChild("cog2", CubeListBuilder.create().texOffs(62, 56)
						.addBox(27.25F, -1.05F, -24.73F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(91, 41)
						.addBox(28.55F, -1.05F, -26.03F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-31.3982F, -7.55F, 10.815F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r18 = cog2.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -20.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r19 = cog2.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -20.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r20 = cog2.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -20.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leg3 = legs.addOrReplaceChild("leg3", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-19.8F, -3.0F, 7.7F, 0.0F, -2.3562F, 0.0F));

		PartDefinition back_right = leg3.addOrReplaceChild("back_right", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-58.3054F, -7.9929F, 11.5779F, 0.0F, -1.5708F, 0.0F));

		PartDefinition axis3 = back_right.addOrReplaceChild("axis3", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r21 = axis3.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(36, 52)
						.addBox(-1.5953F, -10.8056F, 30.9968F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, -0.7854F, 3.1416F));

		PartDefinition up3 = back_right.addOrReplaceChild("up3", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r22 = up3.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(0, 0)
						.addBox(32.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(30.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r23 = up3.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(65, 62)
						.addBox(-2.5F, -4.0F, -2.0F, 5.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-16.0942F, -19.25F, -53.4283F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition foot3 = back_right.addOrReplaceChild("foot3", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r24 = foot3.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(8, 0)
						.addBox(32.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(8, 0)
						.addBox(30.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -10.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r25 = foot3.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(13, 52)
						.addBox(29.9004F, -0.3056F, -1.2369F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(25, 52)
						.addBox(30.9004F, 3.6944F, -0.2369F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(61, 75)
						.addBox(29.9004F, 4.6944F, -1.2369F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cog3 = leg3.addOrReplaceChild("cog3", CubeListBuilder.create().texOffs(62, 56)
						.addBox(27.25F, -1.05F, -36.73F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(91, 41)
						.addBox(28.55F, -1.05F, -38.03F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-59.3982F, -7.55F, 11.215F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r26 = cog3.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r27 = cog3.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r28 = cog3.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leg4 = legs.addOrReplaceChild("leg4", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-19.8F, -3.0F, 19.7F, 0.0F, -2.3562F, 0.0F));

		PartDefinition back_left = leg4.addOrReplaceChild("back_left", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-58.3054F, -7.9929F, 11.5779F, 0.0F, -1.5708F, 0.0F));

		PartDefinition axis4 = back_left.addOrReplaceChild("axis4", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r29 = axis4.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(36, 52)
						.addBox(-1.5953F, -10.8056F, 30.9968F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, -0.7854F, 3.1416F));

		PartDefinition foot4 = back_left.addOrReplaceChild("foot4", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r30 = foot4.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(61, 75)
						.addBox(29.9004F, 4.6944F, -1.2369F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(13, 52)
						.addBox(29.9004F, -0.3056F, -1.2369F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(25, 52)
						.addBox(30.9004F, 3.6944F, -0.2369F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r31 = foot4.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(8, 0)
						.addBox(32.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(8, 0)
						.addBox(30.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -10.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition up4 = back_left.addOrReplaceChild("up4", CubeListBuilder.create(),
				PartPose.offset(10.6571F, 13.6429F, 8.3571F));

		PartDefinition cube_r32 = up4.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(0, 0)
						.addBox(32.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(30.4004F, -3.3056F, -0.2369F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.2767F, -11.9444F, -30.5438F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r33 = up4.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(65, 62)
						.addBox(-2.5F, -4.0F, -2.0F, 5.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-16.0942F, -19.25F, -53.4283F, -3.1416F, 0.7854F, 3.1416F));

		PartDefinition cog4 = leg4.addOrReplaceChild("cog4", CubeListBuilder.create().texOffs(62, 56)
						.addBox(27.25F, -1.05F, -36.73F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(91, 41)
						.addBox(28.55F, -1.05F, -38.03F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-59.3982F, -7.55F, 11.215F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r34 = cog4.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r35 = cog4.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r36 = cog4.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(62, 56)
						.addBox(-1.75F, -0.5F, -32.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(29.0F, -0.55F, -4.23F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.legs.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

	}
}
