package com.workert.robotics.entities.goals;

import java.util.EnumSet;

import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;

public class RobotFollowPlayerOwnerGoal extends Goal {

	private PathfinderMob robot;
	private double speedModifier;
	private PathNavigation navigation;
	private float startDistance;
	private float stopDistance;
	private Player owner;
	private int timeToRecalcPath;

	public RobotFollowPlayerOwnerGoal(PathfinderMob robot, double pSpeedModifier, float pStartDistance,
			float pStopDistance) {
		this.robot = robot;
		this.speedModifier = pSpeedModifier;
		this.navigation = robot.getNavigation();
		this.startDistance = pStartDistance;
		this.stopDistance = pStopDistance;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		Player livingentity = (Player) ((OwnableEntity) this.robot).getOwner();
		if (((livingentity == null) || livingentity.isSpectator())
				|| (this.robot.distanceToSqr(livingentity) < this.startDistance * this.startDistance)) {
			return false;
		} else {
			this.owner = livingentity;
			return true;
		}
	}

	@Override
	public boolean canContinueToUse() {
		if (this.navigation.isDone()) {
			return false;
		} else {
			return !(this.robot.distanceToSqr(this.owner) <= this.stopDistance * this.stopDistance);
		}
	}

	@Override
	public void tick() {
		this.robot.getLookControl().setLookAt(this.owner, 10.0F, this.robot.getMaxHeadXRot());
		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = this.adjustedTickDelay(10);
			this.navigation.moveTo(this.owner, this.speedModifier);

		}
	}

}
