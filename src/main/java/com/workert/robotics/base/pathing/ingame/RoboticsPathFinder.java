package com.workert.robotics.base.pathing.ingame;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;

public class RoboticsPathFinder extends PathFinder {
	public RoboticsPathFinder(NodeEvaluator pNodeEvaluator, int pMaxVisitedNodes) {
		super(pNodeEvaluator, pMaxVisitedNodes);
	}
}
