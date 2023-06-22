package com.workert.robotics.base.pathing;

/**
 * A Cell is a 3D Cartesian point plus a Costs key.
 *
 * @version .9
 * @since .9
 */
public class Cell {

	public static final double DEFAULT_COST = 1.0;

	private int x = 0;
	private int y = 0;
	private int z = 0;
	private Costs key = new Costs(0.0, 0.0);

	/**
	 * Returns a Cell with no defined position or Costs.
	 */
	public Cell() {
		super();
	}

	/**
	 * Returns a Cell which is a clone of the specified Cell.
	 */
	public Cell(Cell other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.key = other.key;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return this.z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Costs getKey() {
		return this.key;
	}

	public void setKey(Costs key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.x;
		result = prime * result + this.y;
		result = prime * result + this.z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return this.z == other.z;
	}

	@Override
	public String toString() {
		return "Cell [x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", key=" + this.key + "]";
	}

}
