package com.workert.robotics.content.computers.computer;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogInstance;
import com.simibubi.create.foundation.render.AllMaterialSpecs;

public class ComputerInstance extends EncasedCogInstance {


	public ComputerInstance(MaterialManager modelManager, ComputerBlockEntity blockEntity) {
		super(modelManager, blockEntity, false);
	}


	@Override
	protected Instancer<RotatingData> getCogModel() {
		return this.materialManager.defaultSolid()
				.material(AllMaterialSpecs.ROTATING)
				.getModel(AllPartialModels.SHAFTLESS_COGWHEEL, this.blockEntity.getBlockState());
	}
}
