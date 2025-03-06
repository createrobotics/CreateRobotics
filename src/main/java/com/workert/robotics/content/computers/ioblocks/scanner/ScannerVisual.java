package com.workert.robotics.content.computers.ioblocks.scanner;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class ScannerVisual extends ShaftVisual<ScannerBlockEntity> implements SimpleDynamicVisual {
	private final OrientedInstance pressHead;

	public ScannerVisual(VisualizationContext context, ScannerBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);

		this.pressHead = this.instancerProvider()
				.instancer(InstanceTypes.ORIENTED, Models.partial(AllPartialModels.MECHANICAL_PRESS_HEAD))
				.createInstance();

		Quaternionf q = Axis.YP
				.rotationDegrees(
						AngleHelper.horizontalAngle(this.blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));

		this.pressHead.rotation(q);

		this.transformModels(partialTick);
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		this.transformModels(ctx.partialTick());
	}

	private void transformModels(float pt) {
		float renderedHeadOffset = this.getRenderedHeadOffset(pt);

		this.pressHead.position(this.getVisualPosition())
				.translatePosition(0, -renderedHeadOffset, 0)
				.setChanged();
	}

	private float getRenderedHeadOffset(float pt) {
		ScannerBehaviour pressingBehaviour = this.blockEntity.getScannerBehaviour();
		return pressingBehaviour.getRenderedHeadOffset(pt);
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		this.relight(this.pressHead);
	}

	@Override
	protected void _delete() {
		super._delete();
		this.pressHead.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		consumer.accept(this.pressHead);
	}

}
