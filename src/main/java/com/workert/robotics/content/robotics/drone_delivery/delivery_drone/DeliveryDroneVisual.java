package com.workert.robotics.content.robotics.drone_delivery.delivery_drone;

import com.simibubi.create.AllPartialModels;
import com.workert.robotics.base.registries.PartialModelRegistry;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDroneVisual extends AbstractEntityVisual<DeliveryDroneEntity> implements SimpleDynamicVisual {
	public TransformedInstance droneInstance;
	public List<TransformedInstance> droneFans;

	public TransformedInstance packageRiggingInstance;
	public TransformedInstance packageInstance;

	public DeliveryDroneVisual(VisualizationContext ctx, DeliveryDroneEntity entity, float partialTick) {
		super(ctx, entity, partialTick);
		this.initializePackagePartialModels();
	}


	private void initializePackagePartialModels() {
		if (!this.entity.getBox().isEmpty()) {
			ResourceLocation key = ForgeRegistries.ITEMS.getKey(this.entity.getBox().getItem());
			this.packageRiggingInstance = this.instancerProvider()
					.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.PACKAGE_RIGGING.get(key)))
					.createInstance();
			this.packageInstance = this.instancerProvider()
					.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.PACKAGES.get(key)))
					.createInstance();
		}
	}

	@Override
	public void beginFrame(Context ctx) {
		float partialTick = ctx.partialTick();

		if (this.droneInstance == null)
			this.droneInstance = this.instancerProvider()
					.instancer(InstanceTypes.TRANSFORMED, Models.partial(PartialModelRegistry.DELIVERY_DRONE))
					.createInstance();

		if (this.droneFans == null || this.droneFans.size() < 4) {
			if (this.droneFans != null)
				this.droneFans.forEach(AbstractInstance::delete);
			this.droneFans = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				this.droneFans.add(this.instancerProvider()
						.instancer(InstanceTypes.TRANSFORMED, Models.partial(PartialModelRegistry.DELIVERY_DRONE_FAN))
						.createInstance());
			}
		}

		if (this.packageRiggingInstance == null || this.packageInstance == null)
			this.initializePackagePartialModels();

		int light = this.computePackedLight(partialTick);

		this.droneInstance.setIdentityTransform()
				.light(light)
				.setChanged();
		if (this.packageRiggingInstance != null && this.packageInstance != null) {
			this.packageRiggingInstance.setIdentityTransform()
					.light(light)
					.setChanged();
			this.packageInstance.setIdentityTransform()
					.light(light)
					.setChanged();
		}
		this.animate(partialTick, light);
	}

	private void animate(float partialTick, int light) {
		float yaw = Mth.lerp(partialTick, this.entity.yRotO, this.entity.getYRot());

		Vec3 pos = this.entity.position();
		var renderOrigin = this.renderOrigin();
		var x = (float) (Mth.lerp(partialTick, this.entity.xo, pos.x) - renderOrigin.getX());
		var y = (float) (Mth.lerp(partialTick, this.entity.yo, pos.y) - renderOrigin.getY());
		var z = (float) (Mth.lerp(partialTick, this.entity.zo, pos.z) - renderOrigin.getZ());

		long randomBits = (long) this.entity.getId() * 31L * 493286711L;
		randomBits = randomBits * randomBits * 4392167121L + randomBits * 98761L;
		float xNudge = (((float) (randomBits >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float zNudge = (((float) (randomBits >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;

		double droneYTransform = 3.5d / 16d;

		this.droneInstance.setIdentityTransform()
				.translate(x - 0.5, y - droneYTransform, z - 0.5)
				.rotateYCenteredDegrees(-yaw - 90)
				.light(light)
				.setChanged();

		float fanRotation = -(this.entity.tickCount * 32);

		float fanXZTranslation = 6.5f / 16f;
		float fanYTranslation = 9.5f / 16f;

		this.droneFans.get(0).setIdentityTransform()
				.translate(x - 0.5 + fanXZTranslation, y - droneYTransform + fanYTranslation, z - 0.5 + fanXZTranslation)
				.rotateYCenteredDegrees(-yaw - 90 + fanRotation)
				.light(light)
				.setChanged();
		this.droneFans.get(1).setIdentityTransform()
				.translate(x - 0.5 + fanXZTranslation, y - droneYTransform + fanYTranslation, z - 0.5 - fanXZTranslation)
				.rotateYCenteredDegrees(-yaw - 90 + fanRotation)
				.light(light)
				.setChanged();
		this.droneFans.get(2).setIdentityTransform()
				.translate(x - 0.5 - fanXZTranslation, y - droneYTransform + fanYTranslation, z - 0.5 + fanXZTranslation)
				.rotateYCenteredDegrees(-yaw - 90 + fanRotation)
				.light(light)
				.setChanged();
		this.droneFans.get(3).setIdentityTransform()
				.translate(x - 0.5 - fanXZTranslation, y - droneYTransform + fanYTranslation, z - 0.5 - fanXZTranslation)
				.rotateYCenteredDegrees(-yaw - 90 + fanRotation)
				.light(light)
				.setChanged();

		double packageYTransform = 23d / 16d;

		if (this.packageRiggingInstance != null && this.packageInstance != null) {
			this.packageRiggingInstance.setIdentityTransform()
					.translate(x - 0.5 + xNudge, y - droneYTransform - packageYTransform, z - 0.5 + zNudge)
					.rotateYCenteredDegrees(-yaw - 90)
					.light(light)
					.setChanged();
			this.packageInstance.setIdentityTransform()
					.translate(x - 0.5 + xNudge, y - droneYTransform - packageYTransform, z - 0.5 + zNudge)
					.rotateYCenteredDegrees(-yaw - 90)
					.light(light)
					.setChanged();
		}
	}

	@Override
	protected void _delete() {
		this.droneInstance.delete();
		this.droneFans.forEach(AbstractInstance::delete);
		if (this.packageRiggingInstance != null)
			this.packageRiggingInstance.delete();
		if (this.packageInstance != null)
			this.packageInstance.delete();
	}
}
