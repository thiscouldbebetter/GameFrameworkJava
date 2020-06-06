package Display.Visuals;

import Display.*;
import Geometry.*;
import Media.*;
import Model.*;

public class VisualImageImmediate implements VisualImage
{
	private Image _image;

	// Helper variables.
	private Coords _drawPos = new Coords();

	public VisualImageImmediate(Image image)
	{
		this._image = image;
	}

	// static methods

	public static VisualImageImmediate[] manyFromImages(Image[] images)
	{
		var returnValues = new VisualImageImmediate[images.length];

		for (var i = 0; i < images.length; i++)
		{
			var image = images[i];
			var visual = new VisualImageImmediate(image);
			returnValues[i] = visual;
		}

		return returnValues;
	};

	// instance methods

	public Image image()
	{
		return this._image;
	};

	// clone

	public VisualImageImmediate clonify()
	{
		return this; // todo
	};

	// visual

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var image = this.image();
		var imageSize = image.sizeInPixels;
		var drawPos = this._drawPos.clear().subtract(imageSize).half().add
		(
			entity.locatable().loc.pos
		);
		//display.drawImageScaled(image, drawPos, imageSize);
		display.drawImage(image, drawPos);
	}

	// Clonable<Visual>.

	public VisualImageImmediate overwriteWith(Visual other)
	{
		return this; // todo
	}
}
