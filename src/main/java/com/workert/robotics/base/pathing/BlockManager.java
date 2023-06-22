package com.workert.robotics.base.pathing;

/**
 * A BlockManager determines which Cells in the CellSpace are blocked. The
 * signature of isBlocked is left abstract so that the concrete implementation
 * can define its own.
 *
 * @version .9
 * @since .9
 */
public abstract class BlockManager {

	protected CellSpace space;

	/**
	 * Returns an implementation of BlockManager. All BlockManagers should take
	 * a CellSpace as a parameter, as the BlockManager will determine which
	 * Cells in the CellSpace are blocked.
	 */
	public BlockManager(CellSpace space) {
		super();
		this.space = space;
	}

	/**
	 * True if the cell is impassable by the PathFinder. False otherwise.
	 */
	public abstract boolean isBlocked(Cell cell);

	/**
	 * Get the CellSpace managed by this BlockManager.
	 */
	public CellSpace getSpace() {
		return this.space;
	}

}
