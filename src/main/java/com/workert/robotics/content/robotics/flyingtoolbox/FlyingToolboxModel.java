package com.workert.robotics.content.robotics.flyingtoolbox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.workert.robotics.Robotics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class FlyingToolboxModel<T extends FlyingToolbox> extends EntityModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(Robotics.MOD_ID, "clockcopter"), "clockcopter");

	private static final float gearTurnSpeed = 0.3f;

	private final ModelPart body;

	public FlyingToolboxModel(ModelPart root) {
		this.body = root.getChild("body");
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

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
