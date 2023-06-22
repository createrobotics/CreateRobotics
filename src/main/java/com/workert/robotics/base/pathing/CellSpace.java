package com.workert.robotics.base.pathing;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Holds all Cells; the start Cell, the blocked Cells, other Cells, and the goal
 * Cell.
 *
 * @version .9
 * @since .9
 */
public class CellSpace {

	private final HashMap<Cell, CellInfo> cellHash = new HashMap<Cell, CellInfo>();
	private Cell startCell;
	private Cell goalCell;

	/**
	 * Returns an empty CellSpace
	 */
	public CellSpace() {
		super();
	}

	/**
	 * Returns the specified Cell's CellInfo
	 */
	public CellInfo getInfo(Cell cell) {
		return this.cellHash.get(cell);
	}

	/**
	 * Update the specified Cell's cost using the specified double.
	 */
	public void updateCellCost(Cell cell, double cost) {
		if (cell == null) {
			return;
		}

		this.cellHash.get(cell).setCost(cost);
	}

	/**
	 * Get the g value of the specified Cell.
	 * <p>
	 * The g value, as specified by
	 * <a href="http://idm-lab.org/bib/abstracts/papers/aaai02b.pdf">Sven
	 * Koenig</a>, is the cost of the path from the start Cell to this Cell.
	 */
	public double getG(Cell cell) {
		if (cell == null) {
			return 0.0;
		}

		CellInfo info = this.cellHash.get(cell);

		if (info == null) {
			return 0.0;
		}

		return info.getG();
	}

	/**
	 * Build a Cell in the CellSpace using the specified x, y, z coordinates.
	 */
	public Cell makeNewCell(int x, int y, int z) {
		return this.makeNewCell(x, y, z, null);
	}

	/**
	 * Build a Cell in the CellSpace using the specified x, y, and z coordinates
	 * plus the specified Costs.
	 */
	public Cell makeNewCell(int x, int y, int z, Costs k) {
		Cell state = new Cell();
		state.setX(x);
		state.setY(y);
		state.setZ(z);
		state.setKey(k);

		return this.makeNewCell(state);
	}

	/**
	 * Build a Cell in the CellSpace which is a copy of the specified Cell.
	 */
	public Cell makeNewCell(Cell cell) {
		if (this.cellHash.get(cell) != null) {
			return cell;
		}

		CellInfo cellInfo = new CellInfo();

		if (this.goalCell == null) {
			throw new RuntimeException("Goal cell not set");
		}

		double costToGoal = Geometry.euclideanDistance(cell, this.goalCell);
		cellInfo.setRhs(costToGoal);
		cellInfo.setG(costToGoal);
		this.cellHash.put(cell, cellInfo);

		Costs key = cell.getKey();
		if (key != null && !key.equals(new Costs(-1.0, -1.0))) {
			this.updateVertex(cell);
		}

		this.calculateKey(cell);

		return cell;
	}

	/**
	 * Set this CellSpace's start Cell.
	 */
	public void setStartCell(int x, int y, int z) {
		Cell cell = new Cell();
		cell.setX(x);
		cell.setY(y);
		cell.setZ(z);
		this.startCell = cell;

		CellInfo startCellInfo = new CellInfo();
		double totalPathCost = Geometry.euclideanDistance(this.startCell, this.goalCell);
		startCellInfo.setRhs(totalPathCost);
		startCellInfo.setG(totalPathCost);
		this.cellHash.put(this.startCell, startCellInfo);

		this.startCell = this.calculateKey(this.startCell);
	}

	/**
	 * Get this CellSpace's start Cell
	 */
	public Cell getStartCell() {
		return this.startCell;
	}

	/**
	 * Set this CellSpace's goal Cell.
	 */
	public void setGoalCell(int x, int y, int z) {
		Cell cell = new Cell();
		cell.setX(x);
		cell.setY(y);
		cell.setZ(z);

		this.goalCell = cell;
		this.cellHash.put(this.goalCell, new CellInfo());
	}

	/**
	 * Get this CellSpace's goal Cell.
	 */
	public Cell getGoalCell() {
		return this.goalCell;
	}

	protected boolean isClose(double var1, double var2) {
		if (var1 == Double.POSITIVE_INFINITY && var2 == Double.POSITIVE_INFINITY) {
			return true;
		}

		return (Math.abs(var1 - var2) < 0.00001);
	}

	private void updateVertex(Cell cell) {
		LinkedList<Cell> successors = new LinkedList<Cell>();

		if (!cell.equals(this.getGoalCell())) {
			successors = this.getSuccessors(cell);
			double tmp = Double.POSITIVE_INFINITY;
			double tmp2;

			for (Cell successor : successors) {
				tmp2 = this.getG(successor) + Geometry.euclideanDistance(cell, successor);
				if (tmp2 < tmp) {
					tmp = tmp2;
				}
			}

			if (!this.isClose(this.getRHS(cell), tmp)) {
				this.setRHS(cell, tmp);
			}
		}

		if (!this.isClose(this.getG(cell), this.getRHS(cell))) {
			this.insertCell(cell);
		}
	}

	private void setRHS(Cell state, double rhs) {
		this.makeNewCell(state);
		this.cellHash.get(state).setRhs(rhs);
	}

	private double getRHS(Cell state) {
		if (this.goalCell == null) {
			throw new RuntimeException("Goal cell not set");
		}

		if (state == this.goalCell) {
			return 0;
		}

		if (this.cellHash.get(state) == null) {
			return Geometry.euclideanDistance(state, this.goalCell);
		}

		return this.cellHash.get(state).getRhs();
	}

	private void insertCell(Cell cell) {
		cell = this.calculateKey(cell);
	}
	
	public LinkedList<Cell> getSuccessors(Cell state) {
		LinkedList<Cell> successors = new LinkedList<Cell>();
		Cell tempState;

		// Generate the successors, starting at the immediate right and moving
		// in a clockwise manner
		tempState = this.makeNewCell(state.getX() + 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX(), state.getY() + 1, state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX() - 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX(), state.getY() - 1, state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		// Up one z level
		tempState = this.makeNewCell(state.getX(), state.getY(), state.getZ() + 1, new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		// Down one z level
		tempState = this.makeNewCell(state.getX(), state.getY(), state.getZ() - 1, new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		return successors;
	}

	public LinkedList<Cell> getPredecessors(Cell state) {
		LinkedList<Cell> predecessors = new LinkedList<Cell>();
		Cell tempState;

		tempState = this.makeNewCell(state.getX() + 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX(), state.getY() + 1, state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX() - 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX(), state.getY() - 1, state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX(), state.getY(), state.getZ() + 1, new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = this.makeNewCell(state.getX(), state.getY(), state.getZ() - 1, new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		return predecessors;
	}

	private Cell calculateKey(Cell state) {
		Cell startCell = this.getStartCell();

		if (startCell == null) {
			throw new RuntimeException("Start cell not set");
		}

		double cost = Math.min(this.getRHS(state), this.getG(state));

		Costs key = state.getKey();

		if (key == null) {
			key = new Costs(0.0, 0.0);
		}

		double kM = 0.0;
		key.setCostPlusHeuristic(cost + Geometry.euclideanDistance(state, startCell) + kM);
		key.setCost(cost);

		return state;
	}
}
