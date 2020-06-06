package Display.Visuals;

public class VisualCircleGradient implements Visual
{
	private double radius;
	private Gradient gradientFill
	private String colorBorder;

	public VisualCircleGradient(double radius, Gradient gradientFill, String colorBorder)
	{
		this.radius = radius;
		this.gradientFill = gradientFill;
		this.colorBorder = colorBorder;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		display.drawCircleWithGradient
		(
			entity.locatable().loc.pos, this.radius, this.gradientFill, this.colorBorder
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
