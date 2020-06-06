package Display.Visuals;

import Display.*;
import Geometry.*;
import Media.*;
import Model.*;

public class VisualImageFromLibrary implements VisualImage
{
	private String imageName;
	private Coords imageSizeScaled;

	// Helper variables.
	private Coords _drawPos = new Coords();

	public VisualImageFromLibrary(String imageName)
	{
		this(imageName, null);
	}

	public VisualImageFromLibrary(String imageName, Coords imageSizeScaled)
	{
		this.imageName = imageName;
		this.imageSizeScaled = imageSizeScaled;
	}

	// static methods

	public static VisualImageFromLibrary[] manyFromImages(Image[] images, Coords imageSizeScaled)
	{
		var returnValues = new VisualImageFromLibrary[images.length];

		for (var i = 0; i < images.length; i++)
		{
			var image = images[i];
			var visual = new VisualImageFromLibrary(image.name(), imageSizeScaled);
			returnValues[i] = visual;
		}

		return returnValues;
	};

	// instance methods

	public Image image(Universe universe)
	{
		return universe.mediaLibrary.imageGetByName(this.imageName);
	};

	// visual

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var image = this.image(universe);
		var imageSize = this.image(universe).sizeInPixels;
		var drawPos = this._drawPos.clear().subtract(imageSize).half().add
		(
			entity.locatable().loc.pos
		);
		display.drawImageScaled(image, drawPos, imageSize);
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
