package com.workert.robotics.client.flywheel;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.mojang.math.Vector3f;
import com.workert.robotics.entities.Clockcopter;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class ClockcopterInstance extends EntityInstance<Clockcopter> implements DynamicInstance {
	private final ModelData model;
	private Clockcopter entity;

	public ClockcopterInstance(MaterialManager materialManager, Clockcopter entity) {
		super(materialManager, entity);
		this.entity = entity;
		//this.model = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(Blocks.BRICKS.defaultBlockState()).createInstance();

		this.model = materialManager.defaultTransparent().material(Materials.TRANSFORMED)
				.getModel(Blocks.GLASS.defaultBlockState()).createInstance();

		this.model.loadIdentity().translate(this.getInstancePosition());
	}

	@Override
	public void updateLight() {
		this.relight(this.getWorldPosition(), this.model);
	}

	@Override
	public void beginFrame() {
		Vector3f translatePosition = this.getInstancePosition();
		translatePosition.sub(new Vector3f(new Vec3(0.5, 0, 0.5)));
		this.model.loadIdentity().translate(translatePosition);
	}

	@Override
	protected void remove() {
		this.model.delete();
	}

}
