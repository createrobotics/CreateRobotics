package com.workert.robotics.base.pathing;

import java.io.Serial;
import java.util.LinkedList;

/**
 * A LinkedList of Cells which represents the path the Pathfinder used to get
 * from the start Cell to the goal Cell. If it found a path all the way to the
 * CellSpace's goal Cell, the path is complete.
 */
public class Path extends LinkedList<Cell> {

	@Serial
	private static final long serialVersionUID = -5572661613938583005L;
	private boolean isComplete = false;

	/**
	 * Return true if the path concludes at the CellSpace's goal Cell.
	 */
	public boolean isComplete() {
		return this.isComplete;
	}

	protected void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

}
