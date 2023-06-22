package com.workert.robotics.base.pathing;

/**
 * A utility which does the basic Euclidean calculation of distance between two
 * 3D points.
 */
public class Geometry {

	/**
	 * Get the Euclidean distance between the two specified Cells.
	 */
	public static Double euclideanDistance(Cell cellA, Cell cellB) {
		if (cellA == null || cellB == null) {
			return null;
		}

		float x = cellA.getX() - cellB.getX();
		float y = cellA.getY() - cellB.getY();
		float z = cellA.getZ() - cellB.getZ();
		return Math.sqrt(x * x + y * y + z * z);
	}

}
