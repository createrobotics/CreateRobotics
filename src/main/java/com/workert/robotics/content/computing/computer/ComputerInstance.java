package com.workert.robotics.content.computing.computer;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.content.contraptions.relays.encased.EncasedCogInstance;
import com.simibubi.create.foundation.render.AllMaterialSpecs;

public class ComputerInstance extends EncasedCogInstance {


	public ComputerInstance(MaterialManager modelManager, ComputerBlockEntity blockEntity) {
		super(modelManager, blockEntity, false);
	}


	@Override
	protected Instancer<RotatingData> getCogModel() {
		return this.materialManager.defaultSolid()
				.material(AllMaterialSpecs.ROTATING)
				.getModel(AllBlockPartials.SHAFTLESS_COGWHEEL, this.blockEntity.getBlockState());
	}
}
