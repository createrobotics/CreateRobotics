package com.workert.robotics.content.robotics.drone_delivery.pathing;

import java.util.ArrayList;

public class SimpleWorldProvider implements IWorldProvider {

	private final ArrayList<Cell> walls = new ArrayList<>();

	public void addWall(Cell cell) {
		this.walls.add(cell);
	}

	@Override
	public boolean isBlocked(Cell cell) {
		return this.walls.contains(cell);
	}
}
