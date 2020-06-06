package Display.Visuals;

import Display.*;
import Model.*;

public class VisualEllipse implements Visual
{
	private double semimajorAxis;
	private double semiminorAxis;
	private double rotationInTurns;
	private String colorFill;
	private String colorBorder;

	public VisualEllipse
	(
		double semimajorAxis, double semiminorAxis, double rotationInTurns,
		String colorFill
	)
	{
		this(semimajorAxis, semiminorAxis, rotationInTurns, colorFill, null);
	}

	public VisualEllipse
	(
		double semimajorAxis, double semiminorAxis, double rotationInTurns,
		String colorFill, String colorBorder
	)
	{
		this.semimajorAxis = semimajorAxis;
		this.semiminorAxis = semiminorAxis;
		this.rotationInTurns = rotationInTurns;
		this.colorFill = colorFill;
		this.colorBorder = colorBorder;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var drawableLoc = entity.locatable().loc;
		var drawableOrientation = drawableLoc.orientation;
		var drawableRotationInTurns = drawableOrientation.headingInTurns();
		display.drawEllipse
		(
			drawableLoc.pos,
			this.semimajorAxis, this.semiminorAxis,
			(this.rotationInTurns + drawableRotationInTurns).wrapToRangeZeroOne(),
			this.colorFill, this.colorBorder
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
