package com.workert.robotics.base.client;

import com.workert.robotics.base.registries.EntityRegistry;
import com.workert.robotics.base.registries.ItemRegistry;
import com.workert.robotics.content.robotics.codedrone.CodeDrone;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class PonderList {
	public static void programming(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("programming", "Programming using a Code Editor");
		scene.configureBasePlate(0, 0, 3);
		scene.showBasePlate();

		BlockPos codeEditorPos = util.grid().at(1, 1, 1);
		Vec3 dronePos = new Vec3(1.5, 1, 1.5);

		scene.idle(10);

		scene.overlay().showText(80).independent(60).attachKeyFrame().placeNearTarget()
				.text("To change the contents of a Program you need a code editor.");

		scene.idle(30);

		scene.world().showSection(util.select().position(codeEditorPos), Direction.DOWN);

		scene.idle(70);

		scene.overlay().showText(80).pointAt(util.vector().topOf(codeEditorPos)).placeNearTarget()
				.text("Right click the code editor with the program to get started!");

		scene.idle(20);

		scene.overlay().showControls(util.vector().blockSurface(codeEditorPos, Direction.UP), Pointing.UP, 40)
				.rightClick().withItem(ItemRegistry.PROGRAM.asStack());

		scene.idle(60);

		scene.world().hideSection(util.select().position(codeEditorPos), Direction.UP);

		scene.idle(20);

		scene.world().createEntity(world -> {
			CodeDrone entity = EntityRegistry.CODE_DRONE.create(world);
			entity.setPos(dronePos.x, dronePos.y, dronePos.z);
			entity.xo = dronePos.x;
			entity.yo = dronePos.y;
			entity.zo = dronePos.z;
			return entity;
		});

		scene.overlay().showText(120).pointAt(dronePos.add(0, 0.5, 0)).attachKeyFrame().placeNearTarget()
				.text("When you're finished coding right click with the program on a programmable robot to apply your code to it.");

		scene.idle(20);

		scene.overlay().showControls(dronePos.add(0, 0.5, 0), Pointing.DOWN, 40).rightClick()
				.withItem(ItemRegistry.PROGRAM.asStack());

		scene.idle(100);
	}
}
