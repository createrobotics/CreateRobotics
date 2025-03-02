package com.workert.robotics.content.computers.ioblocks.scanner;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

public class ScannerVisual extends ShaftVisual<ScannerBlockEntity> {
	//private final OrientedData pressHead;

	public ScannerVisual(VisualizationContext context, ScannerBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);

		/*this.pressHead = context.defaultSolid().material(Materials.ORIENTED)
				.getModel(AllPartialModels.MECHANICAL_PRESS_HEAD, this.blockState).createInstance();

		Quaternionf q = Axis.YP.rotationDegrees(
				AngleHelper.horizontalAngle(this.blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));

		this.pressHead.setRotation(q);

		this.transformModels();*/
	}

	/*@Override
	public Plan<Context> planFrame() {
		this.transformModels();
		return null;
	}

	private void transformModels() {
		float renderedHeadOffset = this.getRenderedHeadOffset((ScannerBlockEntity) this.blockEntity);

		this.pressHead.setPosition(this.getInstancePosition()).nudge(0, -renderedHeadOffset, 0);
	}

	private float getRenderedHeadOffset(ScannerBlockEntity press) {
		ScannerBehaviour pressingBehaviour = press.processingBehaviour;
		return pressingBehaviour.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks()) * 19f / 16f;
	}

	@Override
	public void updateLight() {
		super.updateLight();

		this.relight(this.pos, this.pressHead);
	}

	@Override
	public void remove() {
		super.remove();
		this.pressHead.delete();
	}*/

}
