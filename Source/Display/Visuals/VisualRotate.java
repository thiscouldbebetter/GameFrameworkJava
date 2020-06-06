package Display.Visuals;

import Display.*;
import Geometry.*;
import Model.*;

public class VisualRotate implements Visual
{
	public double rotationInTurns;
	public Visual child;

	public VisualRotate(double rotationInTurns, Visual child)
	{
		this.rotationInTurns = rotationInTurns;
		this.child = child;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		/*
		var graphics = display.graphics;
		graphics.save();

		var centerOfRotation = entity.locatable().loc.pos;
		graphics.translate(centerOfRotation.x, centerOfRotation.y);

		var rotationInRadians = this.rotationInTurns * Polar.RadiansPerTurn;
		graphics.rotate(rotationInRadians);

		graphics.translate(0 - centerOfRotation.x, 0 - centerOfRotation.y);

		this.child.draw(universe, world, display, entity);

		graphics.restore();
		*/
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
