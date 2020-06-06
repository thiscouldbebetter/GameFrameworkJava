package Display.Visuals;

import Display.*;
import Geometry.*;
import Model.*;

public class VisualOffset implements Visual
{
	private Visual child;
	private Coords offset;

	// Helper variables.
	private Coords _posSaved = new Coords();

	public VisualOffset(Visual child, Coords offset)
	{
		this.child = child;
		this.offset = offset;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var drawablePos = entity.locatable().loc.pos;
		this._posSaved.overwriteWith(drawablePos);
		drawablePos.add(this.offset);
		this.child.draw(universe, world, display, entity);
		drawablePos.overwriteWith(this._posSaved);
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
