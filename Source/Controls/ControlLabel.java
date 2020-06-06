package Controls;

import Display.*;
import Geometry.*;
import Model.*;

public class ControlLabel implements Control
{
	public String name;
	public Coords pos;
	public Coords size;
	public boolean isTextCentered;
	public DataBinding _text;
	public double fontHeightInPixels;

	private String styleName;

	private Coords _drawPos = new Coords();

	public ControlLabel
	(
		String name, Coords pos, Coords size, boolean isTextCentered,
		String text, double fontHeightInPixels
	)
	{
		this(name, pos, size, isTextCentered, new DataBinding(text), fontHeightInPixels);
	}

	public ControlLabel
	(
		String name, Coords pos, Coords size, boolean isTextCentered,
		DataBinding text, double fontHeightInPixels
	)
	{
		this.name = name;
		this.pos = pos;
		this.size = size;
		this.isTextCentered = isTextCentered;
		this._text = text;
		this.fontHeightInPixels = fontHeightInPixels;
	}

	public static ControlLabel fromPosAndText(Coords pos, String text)
	{
		return new ControlLabel
		(
			null, //name
			pos,
			null, // size
			false, // isTextCentered
			text,
			10 // fontHeightInPixels
		);
	}

	public void childFocus(Control child)
	{
		// todo
	}

	private Control _parent;

	public Control parent()
	{
		return this._parent;
	}

	public void parent(Control value)
	{
		this._parent = value;
	}

	public Control scalePosAndSize(Coords scaleFactors)
	{
		return this; // todo
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	}

	public String text()
	{
		return this._text.get();
	}

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add(this.pos);
		var style = this.style(universe);
		var text = this.text();

		if (text != null)
		{
			var textAsLines = ("" + text).split("\n");
			var widthMaxInPixels = (this.size == null ? null : this.size.x);
			for (var i = 0; i < textAsLines.length; i++)
			{
				var textLine = textAsLines[i];
				display.drawText
				(
					textLine,
					this.fontHeightInPixels,
					drawPos,
					style.colorBorder,
					style.colorFill, // colorOutline
					false, // areColorsReversed
					this.isTextCentered,
					widthMaxInPixels
				);

				drawPos.y += this.fontHeightInPixels;
			}
		}
	}
}
