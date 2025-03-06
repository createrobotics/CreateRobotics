package com.workert.robotics.content.robotics.drone_delivery.pathing;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Based on <a href="https://github.com/hax0r31337/Astar3d">hax0r31337's A-Star implementation</a> (MIT License)
 */
public record Pathfinder(Cell start, Cell end, Cell[] neighbours, IWorldProvider world) {

	public static final Cell[] COMMON_NEIGHBORS = new Cell[] {
			new Cell(1, 0, 0),
			new Cell(-1, 0, 0),
			new Cell(0, 1, 0),
			new Cell(0, -1, 0),
			new Cell(0, 0, 1),
			new Cell(0, 0, -1)
	};
	public static final Cell[] DIAGONAL_NEIGHBORS = new Cell[] {
			new Cell(1, 1, 0),
			new Cell(-1, -1, 0),
			new Cell(1, -1, 0),
			new Cell(-1, 1, 0),
			new Cell(0, 1, 1),
			new Cell(0, -1, -1),
			new Cell(0, 1, -1),
			new Cell(0, -1, 1),
			// also include the non-diagonal neighbors
			new Cell(1, 0, 0),
			new Cell(-1, 0, 0),
			new Cell(0, 1, 0),
			new Cell(0, -1, 0),
			new Cell(0, 0, 1),
			new Cell(0, 0, -1)
	};


	public ArrayList<Cell> findPath() {
		return this.findPath(Integer.MAX_VALUE);
	}

	/**
	 * @param maxLoops used to prevent infinite loops caused by invalid path
	 */
	public ArrayList<Cell> findPath(int maxLoops) {
		final ArrayList<Cell> open = new ArrayList<>();
		final ArrayList<Cell> closed = new ArrayList<>();

		open.add(this.start);

		Cell current = null;
		int currentIdx;
		int loops = 0;

		// Loop until you find the end
		while (!open.isEmpty() && loops < maxLoops) {
			// Get the current node
			current = open.get(0);
			currentIdx = 0;
			for (int i = 1; i < open.size(); i++) {
				if (open.get(i).f < current.f) {
					current = open.get(i);
					currentIdx = i;
				}
			}

			// Pop current off open list, add to closed list
			open.remove(currentIdx);
			closed.add(current);

			// Found the goal
			if (current.equals(this.end)) {
				break;
			}

			// Generate children
			final ArrayList<Cell> children = new ArrayList<>();
			for (final Cell neighbor : this.neighbours) {
				final Cell child = new Cell(current.x + neighbor.x, current.y + neighbor.y, current.z + neighbor.z);
				child.parent = current;

				if (this.world.isBlocked(child)) {
					continue;
				}

				children.add(child);
			}

			// Loop through children
			for (final Cell child : children) {
				// Child is on the closed list
				if (closed.contains(child)) {
					continue;
				}

				// Create the f, g, and h values
				child.g = current.g + 1;
				child.h = (int) (Math.pow(child.x - this.end.x, 2) + Math.pow(child.y - this.end.y, 2) + Math.pow(child.z - this.end.z, 2));
				child.f = child.g + child.h;

				// Child is already in the open list
				if (open.contains(child) && open.get(open.indexOf(child)).g > child.g) {
					continue;
				}

				open.add(child);
			}

			loops++;
		}

		final ArrayList<Cell> path = new ArrayList<>();
		Cell cur = current;
		while (cur != null) {
			path.add(cur);
			cur = cur.parent;
		}
		// Reverse the list
		Collections.reverse(path);

		return path;
	}
}