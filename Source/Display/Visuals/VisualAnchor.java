package Display.Visuals;

public class VisualAnchor implements Visual
{
	private Visual child;
	private Coords posToAnchorAt;

	private Coords _posSaved = new Coords();

	public VisualAnchor(Visual child, Coords posToAnchorAt)
	{
		this.child = child;
		this.posToAnchorAt = posToAnchorAt;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var drawablePos = entity.locatable().loc.pos;
		this._posSaved.overwriteWith(drawablePos);
		drawablePos.overwriteWith(this.posToAnchorAt);
		this.child.draw(universe, world, display, entity);
		drawablePos.overwriteWith(this._posSaved);
	};

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
