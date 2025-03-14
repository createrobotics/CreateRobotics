package com.workert.robotics.content.robotics.drone_delivery.pathing;

public class Cell {

	public final int x;
	public final int y;
	public final int z;

	/** Cost so far */
	public int g = 0;
	/** Heuristic cost */
	public int h = 0;
	/** Total estimated cost */
	public int f = 0;

	public Cell direction;

	public Cell parent;

	public Cell(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Cell(int x, int y, int z, Cell direction) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Cell) {
			Cell c = (Cell) o;
			return c.x == this.x && c.y == this.y && c.z == this.z;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.x * 31 + this.y * 31 + this.z * 31;
	}

	@Override
	public String toString() {
		return "Cell(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}