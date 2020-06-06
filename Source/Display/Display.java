package Display;

import Geometry.*;
import Geometry.Shapes.*;
import Media.*;
import Model.*;
import Utility.*;

public class Display implements Platformable
{
	public Coords[] sizesAvailable;
	public String fontName;
	public double fontHeightInPixels;
	public String colorFore;
	public String colorBack;
	public boolean isInvisible;

	public Coords sizeInPixels;

	private Coords _drawPos;
	private Coords _scaleFactor;
	private Coords _sizeDefault;
	private Coords _sizeHalf;
	private Coords _zeroes;

	public Display(Coords size)
	{
		this(new Coords[] { size }, "Font", 10, "Black", "White");
	}

	public Display
	(
		Coords[] sizesAvailable, String fontName, double fontHeightInPixels,
		String colorFore, String colorBack
	)
	{
		this(sizesAvailable, fontName, fontHeightInPixels, colorFore, colorBack, false);
	}

	public Display
	(
		Coords[] sizesAvailable, String fontName, double fontHeightInPixels,
		String colorFore, String colorBack, boolean isInvisible
	)
	{
		this.sizesAvailable = sizesAvailable;
		this._sizeDefault = this.sizesAvailable[0];
		this.sizeInPixels = this._sizeDefault;
		this.fontName = fontName;
		this.fontHeightInPixels = fontHeightInPixels;
		this.colorFore = colorFore;
		this.colorBack = colorBack;
		this.isInvisible = isInvisible;

		// Helper variables.

		this._drawPos = new Coords();
		this._sizeHalf = new Coords();
		this._zeroes = Coords.Instances().Zeroes;
		this._scaleFactor = new Coords();
	}

	// constants

	public static double RadiansPerTurn = Math.PI * 2.0;

	// methods

	public void clear()
	{
		// todo
	}

	public void drawArc
	(
		Coords center, double radiusInner, double radiusOuter, double angleStartInTurns,
		double angleStopInTurns, String colorFill, String colorBorder
	)
	{
		var drawPos = this._drawPos.overwriteWith(center);
		var angleStartInRadians = angleStartInTurns * Display.RadiansPerTurn;
		var angleStopInRadians = angleStopInTurns * Display.RadiansPerTurn;

		if (colorFill != null)
		{
			// todo
		}

		if (colorBorder != null)
		{
			// todo
		}
	};

	public void drawBackground(String colorBack, String colorBorder)
	{
		this.drawRectangle
		(
			this._zeroes,
			this.sizeDefault(), // Automatic scaling.
			(colorBack == null ? this.colorBack : colorBack),
			(colorBorder == null ? this.colorFore : colorBorder)
		);
	};

	public void drawCircle(Coords center, double radius, String colorFill, String colorBorder)
	{
		var drawPos = this._drawPos.overwriteWith(center);

		// todo
		
		if (colorFill != null)
		{
			// todo
		}

		if (colorBorder != null)
		{
			// todo
		}
	};

	public void drawCircleWithGradient(Coords center, double radius, Gradient gradientFill, String colorBorder)
	{
		// todo

		if (colorBorder != null)
		{
			// todo
		}
	};

	public void drawCrosshairs(Coords center, double radius, String color)
	{
		var drawPos = this._drawPos.overwriteWith(center);
		// todo
	};

	public void drawEllipse
	(
		Coords center, double semimajorAxis, double semiminorAxis, double rotationInTurns, String colorFill, String colorBorder
	)
	{
		var drawPos = this._drawPos.overwriteWith(center);

		// todo
	};

	public void drawImage(Image imageToDraw, Coords pos)
	{
		// todo
	};

	public void drawImagePartial(Image imageToDraw, Coords pos, Box boxToShow)
	{
		// todo
	};

	public void drawImageScaled(Image imageToDraw, Coords pos, Coords size)
	{
		// todo
	};

	public void drawLine(Coords fromPos, Coords toPos, String color, double lineThickness)
	{
		var drawPos = this._drawPos;
		// todo
	};

	public void drawPath(Coords[] vertices, String color, double lineThickness, boolean isClosed)
	{
		// todo
		if (isClosed)
		{
			// todo
		}
		// todo
	};

	public void drawPixel(Coords pos, String color)
	{
		// todo
	};

	public void drawPolygon(Coords[] vertices, String colorFill, String colorBorder)
	{
		var drawPos = this._drawPos;

		for (var i = 0; i < vertices.length; i++)
		{
			var vertex = vertices[i];
			drawPos.overwriteWith(vertex);
			if (i == 0)
			{
				// todo
			}
			else
			{
				// todo
			}
		}

		if (colorFill != null)
		{
			// todo
		}

		if (colorBorder != null)
		{
			// todo
		}
	}

	public void drawRectangle
	(
		Coords pos, Coords size, String colorFill
	)
	{
		this.drawRectangle(pos, size, colorFill);
	}

	public void drawRectangle
	(
		Coords pos, Coords size, String colorFill, String colorBorder
	)
	{
		this.drawRectangle(pos, size, colorFill, colorBorder, false);
	}

	public void drawRectangle
	(
		Coords pos, Coords size, String colorFill, String colorBorder,
		boolean areColorsReversed
	)
	{
		if (areColorsReversed)
		{
			var temp = colorFill;
			colorFill = colorBorder;
			colorBorder = temp;
		}

		if (colorFill != null)
		{
			// todo
		}

		if (colorBorder != null)
		{
			// todo
		}
	};

	public void drawRectangleCentered
	(
		Coords pos, Coords size, String colorFill, String colorBorder
	)
	{
		var sizeHalf = this._sizeHalf.overwriteWith(size).half();
		var posAdjusted = this._drawPos.overwriteWith(pos).subtract(sizeHalf);
		this.drawRectangle(posAdjusted, size, colorFill, colorBorder);
	};

	public void drawText
	(
		String text,
		double fontHeightInPixels,
		Coords pos,
		String colorFill,
		String colorOutline,
		boolean areColorsReversed,
		boolean isCentered,
		double widthMaxInPixels
	)
	{
		//var fontToRestore = this.graphics.font;

		this.fontSet(null, fontHeightInPixels);

		if (areColorsReversed)
		{
			var temp = colorFill;
			colorFill = colorOutline;
			colorOutline = temp;
		}

		if (colorFill == null)
		{
			colorFill = this.colorFore;
		}

		var drawPos = new Coords(pos.x, pos.y + fontHeightInPixels);

		var textAsLines = text.split("\n");
		for (var i = 0; i < textAsLines.length; i++)
		{
			var textLine = textAsLines[i];

			var textTrimmed = textLine;
			while (this.textWidthForFontHeight(textTrimmed, fontHeightInPixels) > widthMaxInPixels)
			{
				textTrimmed = textTrimmed.substring(0, textTrimmed.length() - 1);
			}

			var textWidthInPixels = this.textWidthForFontHeight
			(
				textTrimmed, fontHeightInPixels
			);

			if (isCentered)
			{
				drawPos.addDimensions(0 - textWidthInPixels / 2, 0 - fontHeightInPixels / 2, 0);
			}

			if (colorOutline != null)
			{
				// todo
			}

			// todo

			drawPos.y += fontHeightInPixels;
		}
	};

	public void fontSet(String fontName, double fontHeightInPixels)
	{
		if (fontName != this.fontName || fontHeightInPixels != this.fontHeightInPixels)
		{
			this.fontName = (fontName == null ? this.fontName : fontName);
			this.fontHeightInPixels = fontHeightInPixels;
			//this.graphics.font = this.fontHeightInPixels + "px " + this.fontName;
		}
	};

	public void hide(Universe universe)
	{
		universe.platformHelper.platformableRemove(this);
	};

	public Display initialize(Universe universe)
	{
		if (this.isInvisible)
		{
			//this.toDomElement();
		}
		else if (universe == null)
		{
			/*
			// hack - Allows use of this class
			// without including PlatformHelper or Universe.
			var domElement = this.toDomElement();
			var divMain = document.getElementById("divMain");
			if (divMain == null)
			{
				divMain = document.createElement("div");
				divMain.id = "divMain";
				document.body.appendChild(divMain);
			}
			divMain.appendChild(domElement);
			*/
		}
		else
		{
			universe.platformHelper.platformableAdd(this);
		}

		return this;
	};

	public Coords sizeDefault()
	{
		return this._sizeDefault;
	};

	public Coords scaleFactor()
	{
		if (this._scaleFactor == null)
		{
			var sizeBase = this.sizesAvailable[0];
			this._scaleFactor = this.sizeInPixels.clonify().divide(sizeBase);
		}
		return this._scaleFactor;
	};

	public double textWidthForFontHeight(String textToMeasure, double fontHeightInPixels)
	{
		// var fontToRestore = this.graphics.font;
		this.fontSet(null, fontHeightInPixels);
		//var returnValue = this.graphics.measureText(textToMeasure).width;
		// this.graphics.font = fontToRestore;
		var returnValue = 0; // todo
		return returnValue;
	};

	public Image toImage()
	{
		return null; // todo
	};

	// platformable

	public Object toPlatformable()
	{
		// todo
		return null;
	};
}
