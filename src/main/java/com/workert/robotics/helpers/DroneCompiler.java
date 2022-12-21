package com.workert.robotics.helpers;

import com.workert.robotics.entities.CodeDrone;

public class DroneCompiler {

	public static void runCode(CodeDrone drone, String code) {
		code.lines().forEach(command -> {
			command = command.replace(" ", "");
			if (command.startsWith("robot.goTo(")) {
				String[] coordinateList = command.replace("robot.goTo(", "").replace(")", "").split(",");
				try {
					drone.getNavigation().moveTo(Double.valueOf(coordinateList[0]), Double.valueOf(coordinateList[1]),
							Double.valueOf(coordinateList[2]), 1);
				} catch (Exception exception) {
					throw new IllegalArgumentException(
							"\"robot.goTo\" takes three arguments from the type \"Double\".\nException message: \""
									+ exception.getLocalizedMessage() + "\"");
				}
			}
		});
	}
}
