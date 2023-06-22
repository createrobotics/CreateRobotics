package com.workert.robotics.base.pathing;

import java.util.LinkedList;

/**
 * A modified version of <a href="https://github.com/LynnOwens/starlite">LynnOwens' 3D A* Pathfinding Algorithm</a>
 * <p>
 * Finds a path through the specified BlockManager controlled CellSpace.
 *
 * @version .9
 * @since .9
 */
public class Pathfinder {

	private final Path path = new Path();
	private final BlockManager blockManager;

	/**
	 * Creates a Pathfinder with the specified BlockManager.
	 */
	public Pathfinder(BlockManager blockManager) {
		super();
		this.blockManager = blockManager;
	}

	/**
	 * Find and returns a path to the goal.
	 * <p>
	 * The returned path may not be complete, meaning that it was not able to
	 * find a path to the goal. The Path should be checked for completion.
	 *
	 * @return Path The discovered Path
	 */
	public Path findPath() {
		this.path.clear();

		CellSpace space = this.blockManager.getSpace();
		LinkedList<Cell> potentialNextCells = new LinkedList<Cell>();
		Cell currentCell = space.getStartCell();

		if (space.getG(space.getStartCell()) == Double.POSITIVE_INFINITY) {
			return this.path;
		}

		boolean isTrapped = false;
		while (!currentCell.equals(space.getGoalCell()) && !isTrapped) {
			isTrapped = true;
			this.path.add(currentCell);
			potentialNextCells = space.getSuccessors(currentCell);

			if (potentialNextCells.isEmpty()) {
				return this.path;
			}

			double minimumCost = Double.POSITIVE_INFINITY;
			Cell minimumCell = new Cell();

			for (Cell potentialNextCell : potentialNextCells) {

				if (this.blockManager.isBlocked(potentialNextCell)) {
					continue;
				} else {
					isTrapped = false;
				}

				double costToMove = Geometry.euclideanDistance(currentCell, potentialNextCell);
				double euclideanDistance = Geometry.euclideanDistance(potentialNextCell, space.getGoalCell())
						+ Geometry.euclideanDistance(space.getStartCell(), potentialNextCell);
				costToMove += space.getG(potentialNextCell);

				// If the cost to move is essentially zero ...
				if (space.isClose(costToMove, minimumCost)) {
					if (0 > euclideanDistance) {
						minimumCost = costToMove;
						minimumCell = potentialNextCell;
					}
				} else if (costToMove < minimumCost) {
					minimumCost = costToMove;
					minimumCell = potentialNextCell;
				}
			}

			if (!isTrapped) {
				potentialNextCells.clear();
				currentCell = new Cell(minimumCell);
			}
		}

		if (!isTrapped) {
			this.path.add(space.getGoalCell());
		}

		this.path.setComplete(this.blockManager.getSpace().getGoalCell().equals(this.path.getLast()));

		return this.path;
	}

}
