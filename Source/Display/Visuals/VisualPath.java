package Display.Visuals;

import Display.*;
import Geometry.*;
import Geometry.Shapes.*;
import Geometry.Transforms.*;
import Model.*;

public class VisualPath implements Visual
{
	private Path verticesAsPath;
	private String color;
	private double lineThickness;
	private boolean isClosed;

	private Path verticesAsPathTransformed;
	private Transform_Translate transformTranslate = new Transform_Translate(new Coords());

	public VisualPath(Path verticesAsPath, String color, double lineThickness)
	{
		this(verticesAsPath, color, lineThickness, false);
	}

	public VisualPath(Path verticesAsPath, String color, double lineThickness, boolean isClosed)
	{
		this.verticesAsPath = verticesAsPath;
		this.color = color;
		this.lineThickness = lineThickness;
		this.isClosed = isClosed;

		this.verticesAsPathTransformed = this.verticesAsPath.clonify();
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var drawablePos = entity.locatable().loc.pos;
		this.transformTranslate.displacementSet(drawablePos);

		this.verticesAsPathTransformed.overwriteWith
		(
			this.verticesAsPath
		);

		Transform.applyTransformToCoordsMany
		(
			this.transformTranslate,
			this.verticesAsPathTransformed.points
		);

		display.drawPath
		(
			this.verticesAsPathTransformed.points,
			this.color,
			this.lineThickness,
			this.isClosed
		);
	}

	// Clonable<Visual>.

	public Visual clonify()
	{
		return this; // todo
	}

	public Visual overwriteWith(Visual other)
	{
		return this; // todo
	}

}
