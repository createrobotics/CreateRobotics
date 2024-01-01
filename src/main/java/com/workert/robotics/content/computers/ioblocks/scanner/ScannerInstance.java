package com.workert.robotics.content.computers.ioblocks.scanner;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import org.joml.Quaternionf;

public class ScannerInstance extends ShaftInstance<ScannerBlockEntity> implements DynamicInstance {
	private final OrientedData pressHead;

	public ScannerInstance(MaterialManager materialManager, ScannerBlockEntity blockEntity) {
		super(materialManager, blockEntity);

		this.pressHead = materialManager.defaultSolid().material(Materials.ORIENTED)
				.getModel(AllPartialModels.MECHANICAL_PRESS_HEAD, this.blockState).createInstance();

		Quaternionf q = Axis.YP.rotationDegrees(
				AngleHelper.horizontalAngle(this.blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));

		this.pressHead.setRotation(q);

		this.transformModels();
	}

	@Override
	public void beginFrame() {
		this.transformModels();
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
	}
}
