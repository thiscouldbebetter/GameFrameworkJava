package Display.Visuals;

import Display.*;
import Geometry.*;
import Model.*;

public class VisualArc implements Visual
{
	private double radiusOuter;
	private double radiusInner;
	private Coords directionMin;
	private double angleSpannedInTurns;
	private String colorFill;
	private String colorBorder;

	// helper variables
	private Coords _drawPos = new Coords();
	private Polar _polar = new Polar();

	public VisualArc
	(
		double radiusOuter, double radiusInner, Coords directionMin,
		double angleSpannedInTurns, String colorFill
	)
	{
		this(radiusOuter, radiusInner, directionMin, angleSpannedInTurns, colorFill, null);
	}

	public VisualArc
	(
		double radiusOuter, double radiusInner, Coords directionMin,
		double angleSpannedInTurns, String colorFill, String colorBorder
	)
	{
		this.radiusOuter = radiusOuter;
		this.radiusInner = radiusInner;
		this.directionMin = directionMin;
		this.angleSpannedInTurns = angleSpannedInTurns;
		this.colorFill = colorFill;
		this.colorBorder = colorBorder;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var drawableLoc = entity.locatable().loc;
		var drawPos = this._drawPos.overwriteWith
		(
			drawableLoc.pos
		);

		var drawableAngleInTurns = drawableLoc.orientation.headingInTurns();
		var wedgeAngleMin =
			drawableAngleInTurns
			+ this._polar.fromCoords(this.directionMin).azimuthInTurns;
		var wedgeAngleMax = wedgeAngleMin + this.angleSpannedInTurns;

		display.drawArc
		(
			drawPos, // center
			this.radiusInner, this.radiusOuter,
			wedgeAngleMin, wedgeAngleMax,
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
