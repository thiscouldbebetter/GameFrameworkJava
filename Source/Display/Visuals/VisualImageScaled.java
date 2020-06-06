package Display.Visuals;

import Display.*;
import Geometry.*;
import Model.*;

public class VisualImageScaled implements VisualImage
{
	private VisualImage visualImage;
	private Coords sizeScaled;

	// Helper variables.
	private Coords _drawPos = new Coords();

	public VisualImageScaled(VisualImage visualImage, Coords sizeScaled)
	{
		this.visualImage = visualImage;
		this.sizeScaled = sizeScaled;
	}

	public static VisualImageScaled[] manyFromSizeAndVisuals(Coords sizeScaled, Visual[] visualsToScale)
	{
		var returnValues = new VisualImageScaled[visualsToScale.length];
		for (var i = 0; i < visualsToScale.length; i++)
		{
			var visualToScale = visualsToScale[i];
			var visualScaled = new VisualImageScaled((VisualImage)visualToScale, sizeScaled);
			returnValues[i] = visualScaled;
		}
		return returnValues;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var image = this.visualImage.image(universe);

		var image = this.visualImage.image(universe);
		var imageSize = this.sizeScaled;
		var drawPos = this._drawPos.clear().subtract(imageSize).half().add
		(
			entity.locatable().loc.pos
		);
		display.drawImageScaled(image, drawPos, imageSize);
	}

	public Image image(Universe universe)
	{
		return this.visualImage.image(universe);
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
