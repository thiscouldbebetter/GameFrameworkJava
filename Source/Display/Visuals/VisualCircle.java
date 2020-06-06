package Display.Visuals;

import Display.*;
import Model.*;

public class VisualCircle implements Visual
{
	private double radius;
	private String colorFill;
	private String colorBorder;

	public VisualCircle(double radius, String colorFill)
	{
		this(radius, colorFill, null);
	}

	public VisualCircle(double radius, String colorFill, String colorBorder)
	{
		this.radius = radius;
		this.colorFill = colorFill;
		this.colorBorder = colorBorder;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		display.drawCircle(entity.locatable().loc.pos, this.radius, this.colorFill, this.colorBorder);
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
