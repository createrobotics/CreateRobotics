package com.workert.robotics.content.robotics.codedrone;

import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CodeDroneVisual extends AbstractEntityVisual<CodeDrone> implements SimpleDynamicVisual {
	public final TransformedInstance instance;

	public CodeDroneVisual(VisualizationContext ctx, CodeDrone entity, float partialTick) {
		super(ctx, entity, partialTick);

		PartialModel model = AllPartialModels.BLAZE_IDLE;

		this.instance = this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(model))
				.createInstance();

		this.animate(partialTick);
	}

	@Override
	public void beginFrame(Context ctx) {
		this.animate(ctx.partialTick());
	}

	private void animate(float partialTick) {
		float yaw = Mth.lerp(partialTick, this.entity.yRotO, this.entity.getYRot());

		Vec3 pos = CodeDroneVisual.this.entity.position();
		var renderOrigin = this.renderOrigin();
		var x = (float) (Mth.lerp(partialTick, this.entity.xo, pos.x) - renderOrigin.getX());
		var y = (float) (Mth.lerp(partialTick, this.entity.yo, pos.y) - renderOrigin.getY());
		var z = (float) (Mth.lerp(partialTick, this.entity.zo, pos.z) - renderOrigin.getZ());

		long randomBits = (long) this.entity.getId() * 31L * 493286711L;
		randomBits = randomBits * randomBits * 4392167121L + randomBits * 98761L;
		float xNudge = (((float) (randomBits >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float yNudge = (((float) (randomBits >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float zNudge = (((float) (randomBits >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;

		this.instance.setIdentityTransform()
				.translate(x - 0.5 + xNudge, y + yNudge, z - 0.5 + zNudge)
				.rotateYCenteredDegrees(-yaw - 90)
				.light(this.computePackedLight(partialTick))
				.setChanged();
	}

	@Override
	protected void _delete() {
		this.instance.delete();
	}
}
