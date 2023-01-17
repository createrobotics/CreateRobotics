package com.workert.robotics.entities.goals;

import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class RobotFollowPlayerOwnerGoal extends Goal {

	private final PathfinderMob robot;
	private final double speedModifier;
	private final PathNavigation navigation;
	private final float startDistance;
	private final float stopDistance;
	private Player owner;
	private int timeToRecalcPath;

	public RobotFollowPlayerOwnerGoal(OwnableEntity robot, double pSpeedModifier, float pStartDistance,
									  float pStopDistance) {
		this.robot = (PathfinderMob) robot;
		this.speedModifier = pSpeedModifier;
		this.navigation = ((PathfinderMob) robot).getNavigation();
		this.startDistance = pStartDistance;
		this.stopDistance = pStopDistance;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		Player livingentity = (Player) ((OwnableEntity) this.robot).getOwner();
		if (((livingentity == null) || livingentity.isSpectator()) ||
				(this.robot.distanceToSqr(livingentity) < this.startDistance * this.startDistance)) {
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
