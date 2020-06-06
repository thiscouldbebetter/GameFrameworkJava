package Controls;

import java.util.function.*;
import Display.*;
import Geometry.*;
import Model.*;

public class ControlButton implements Control
{
	public String _name;
	public Coords _pos;
	public Coords _size;
	public String text;
	public double fontHeightInPixels;
	public boolean hasBorder;
	public DataBinding _isEnabled;
	public Runnable click;
	public boolean canBeHeldDown;

	public boolean isHighlighted;
	public String styleName;

	private Location _drawLoc;
	private Coords _sizeHalf;

	public ControlButton
	(
		String name, Coords pos, Coords size, String text,
		double fontHeightInPixels, boolean hasBorder, Runnable click
	)
	{
		this(name, pos, size, text, fontHeightInPixels, hasBorder, new DataBinding(true), click);
	}

	public ControlButton
	(
		String name, Coords pos, Coords size, String text,
		double fontHeightInPixels, boolean hasBorder, boolean isEnabled,
		Runnable click
	)
	{
		this(name, pos, size, text, fontHeightInPixels, hasBorder, new DataBinding(isEnabled), click, false);
	}

	public ControlButton
	(
		String name, Coords pos, Coords size, String text,
		double fontHeightInPixels, boolean hasBorder, DataBinding isEnabled,
		Runnable click
	)
	{
		this(name, pos, size, text, fontHeightInPixels, hasBorder, isEnabled, click, false);
	}

	public ControlButton
	(
		String name, Coords pos, Coords size, String text,
		double fontHeightInPixels, boolean hasBorder, DataBinding isEnabled,
		Runnable click, boolean canBeHeldDown
	)
	{
		this._name = name;
		this._pos = pos;
		this._size = size;
		this.text = text;
		this.fontHeightInPixels = fontHeightInPixels;
		this.hasBorder = hasBorder;
		this._isEnabled = isEnabled;
		this.click = click;
		this.canBeHeldDown = canBeHeldDown;

		this.isHighlighted = false;

		// Helper variables.
		this._drawLoc = new Location(new Coords());
		this._sizeHalf = new Coords();
	}

	public boolean actionHandle(String actionNameToHandle, Universe universe)
	{
		if (actionNameToHandle == ControlActionNames.ControlConfirm)
		{
			this.click.run();
		}

		return (this.canBeHeldDown == false); // wasActionHandled
	}

	public boolean isEnabled()
	{
		return (Boolean)(this._isEnabled.get());
	};

	// events

	public void childFocus(Control child)
	{}

	public void focusGain()
	{
		this.isHighlighted = true;
	};

	public void focusLose()
	{
		this.isHighlighted = false;
	};

	public boolean mouseClick(Coords clickPos)
	{
		if (this.isEnabled())
		{
			this.click.run();
		}
		return (this.canBeHeldDown == false); // wasClickHandled
	}

	public void mouseEnter()
	{
		this.isHighlighted = true;
	}

	public void mouseExit()
	{
		this.isHighlighted = false;
	}

	public boolean mouseMove(Coords clickPos)
	{}

	private Control _parent;

	public Control parent()
	{
		return this._parent;
	}

	public void parent(Control value)
	{
		this._parent = value;
	}

	public Coords pos()
	{
		return this._pos;
	}

	public Control scalePosAndSize(Coords scaleFactor)
	{
		this._pos.multiply(scaleFactor);
		this._size.multiply(scaleFactor);
		this.fontHeightInPixels *= scaleFactor.y;
		return this;
	}

	public Coords size()
	{
		return this._size;
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	};

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		var drawPos = this._drawLoc.overwriteWith(drawLoc).pos;
		drawPos.add(this.pos());

		var isEnabled = this.isEnabled();
		var isHighlighted = this.isHighlighted && isEnabled;

		var style = this.style(universe);
		var colorFill = style.colorFill;
		var colorBorder = style.colorBorder;

		if (this.hasBorder)
		{
			display.drawRectangle
			(
				drawPos, this.size(),
				colorFill, colorBorder,
				isHighlighted // areColorsReversed
			);
		}

		var size = this.size();
		drawPos.add(this._sizeHalf.overwriteWith(size).half());

		var colorText = (isEnabled ? colorBorder : style.colorDisabled);

		display.drawText
		(
			this.text,
			this.fontHeightInPixels,
			drawPos,
			colorText,
			colorFill,
			isHighlighted,
			true, // isCentered
			size.x // widthMaxInPixels
		);
	}

	// Namable.

	public String name() { return this._name; }
}
