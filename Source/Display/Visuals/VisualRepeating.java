package Display.Visuals;

import Display.*;
import Geometry.*;
import Model.*;

public class VisualRepeating implements Visual
{
	private Coords cellSize;
	private Coords viewSize;
	private Visual child;

	private Coords viewSizeInCells;

	// Helper variables.
	private Coords _cellPos = new Coords();
	private Coords _drawOffset = new Coords();
	private Coords _drawPosWrapped = new Coords();
	private Coords _drawablePosToRestore = new Coords();

	public VisualRepeating(Coords cellSize, Coords viewSize, Visual child)
	{
		this.cellSize = cellSize;
		this.viewSize = viewSize;
		this.child = child;

		if (this.cellSize.z == 0)
		{
			throw "Invalid argument: cellSize.z must not be 0.";
		}

		this.viewSizeInCells = this.viewSize.clonify().divide
		(
			this.cellSize
		);
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var drawPos = entity.locatable().loc.pos;

		this._drawablePosToRestore.overwriteWith(drawPos);

		var drawPosWrapped = this._drawPosWrapped.overwriteWith
		(
			drawPos
		).wrapToRangeMax(this.cellSize);

		var cellPos = this._cellPos;
		var viewSizeInCells = this.viewSizeInCells;

		for (var y = -1; y < viewSizeInCells.y + 1; y++)
		{
			cellPos.y = y;

			for (var x = -1; x < viewSizeInCells.x + 1; x++)
			{
				cellPos.x = x;

				drawPos.overwriteWith
				(
					this._drawOffset.overwriteWith(cellPos).multiply
					(
						this.cellSize
					)
				).add
				(
					drawPosWrapped
				);

				this.child.draw(universe, world, display, entity);
			}
		}

		drawPos.overwriteWith(this._drawablePosToRestore);
	}

	// Clonable.

	public Visual clonify()
	{
		return this; // todo
	}

	public Visual overwriteWith(Visual other)
	{
		return this; // todo
	}

}
