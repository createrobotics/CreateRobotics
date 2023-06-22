package com.workert.robotics.base.pathing;

/**
 * Costs is a tuple of Doubles which represent a Cell cost and its cost plus the
 * heuristically calculated cost.
 *
 * @version .9
 * @since .9
 */
public class Costs {

	private Double costPlusHeuristic;
	private Double cost;

	/**
	 * Return a Costs of the two provided Doubles.
	 */
	public Costs(Double costPlusHeuristic, Double cost) {
		this.costPlusHeuristic = costPlusHeuristic;
		this.cost = cost;
	}

	public Double getCostPlusHeuristic() {
		return this.costPlusHeuristic;
	}

	public Double getCost() {
		return this.cost;
	}

	public void setCostPlusHeuristic(Double object1) {
		this.costPlusHeuristic = object1;
	}

	public void setCost(Double object2) {
		this.cost = object2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.costPlusHeuristic == null) ? 0 : this.costPlusHeuristic.hashCode());
		result = prime * result + ((this.cost == null) ? 0 : this.cost.hashCode());
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
		Costs other = (Costs) obj;
		if (this.costPlusHeuristic == null) {
			if (other.costPlusHeuristic != null)
				return false;
		} else if (!this.costPlusHeuristic.equals(other.costPlusHeuristic))
			return false;
		if (this.cost == null) {
			return other.cost == null;
		} else return this.cost.equals(other.cost);
	}

	@Override
	public String toString() {
		return "Costs [costPlusHeuristic=" + this.costPlusHeuristic + ", cost=" + this.cost + "]";
	}

}
