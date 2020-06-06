package Display.Visuals;

public class VisualLine implements Visual
{
	private Coords fromPos;
	private Coords toPos;
	private String color;

	// Helper variables.
	private Coords drawPosFrom = new Coords();
	private Coords drawPosTo = new Coords();

	public VisualLine(Coords fromPos, Coords toPos, String color)
	{
		this.fromPos = fromPos;
		this.toPos = toPos;
		this.color = color;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var pos = entity.locatable().loc.pos;
		var drawPosFrom = this.drawPosFrom.overwriteWith
		(
			pos
		).add
		(
			this.fromPos
		);

		var drawPosTo = this.drawPosTo.overwriteWith
		(
			pos
		).add
		(
			this.toPos
		);

		display.drawLine
		(
			drawPosFrom,
			drawPosTo,
			this.color
		);
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
