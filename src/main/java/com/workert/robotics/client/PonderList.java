package com.workert.robotics.client;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import com.workert.robotics.entities.CodeDrone;
import com.workert.robotics.lists.EntityList;
import com.workert.robotics.lists.ItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class PonderList {
	public static void programming(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("programming", "Programming using a Code Editor");
		scene.configureBasePlate(0, 0, 3);
		scene.showBasePlate();
		scene.rotateCameraY(85);

		BlockPos codeEditorPos = util.grid.at(1, 1, 1);
		Vec3 dronePos = new Vec3(1, 1, 0);

		scene.idle(10);

		scene.world.showSection(util.select.position(codeEditorPos), Direction.DOWN);

		scene.overlay.showText(80)
				.pointAt(util.vector.topOf(codeEditorPos)).attachKeyFrame().placeNearTarget()
				.text("To change the contents of a Program you need a code editor.");

		scene.idle(100);

		scene.overlay.showText(60)
				.pointAt(util.vector.topOf(codeEditorPos)).attachKeyFrame().placeNearTarget()
				.text("Right click the code editor with the program to get started!");

		scene.idle(20);

		scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(codeEditorPos, Direction.UP),
				Pointing.DOWN).rightClick().withItem(ItemList.PROGRAM.asStack()), 40);

		scene.idle(80);

		scene.world.createEntity(world -> {
			CodeDrone entity = EntityList.CODE_DRONE.create(world);
			entity.setPos(dronePos.x, dronePos.y, dronePos.z);
			entity.xo = dronePos.x;
			entity.yo = dronePos.y;
			entity.zo = dronePos.z;
			entity.animationPosition = 0;
			entity.yRotO = 210;
			entity.setYRot(210);
			entity.yHeadRotO = 210;
			entity.yHeadRot = 210;
			return entity;
		});

		scene.overlay.showText(100).pointAt(dronePos.add(0, 0.5, 0)).attachKeyFrame().placeNearTarget()
				.text("Right click with the program on a programmable robot to apply your code to it.");

		scene.idle(20);

		scene.overlay.showControls(
				new InputWindowElement(dronePos.add(0, 0.5, 0), Pointing.DOWN).rightClick()
						.withItem(ItemList.PROGRAM.asStack()), 40);

	}
}
