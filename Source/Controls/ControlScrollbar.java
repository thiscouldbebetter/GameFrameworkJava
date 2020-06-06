package Controls;

import Display.*;
import Geometry.*;
import Model.*;
import Utility.*;

public class ControlScrollbar implements Control
{
	public Coords _pos;
	public Coords _size;
	public double fontHeightInPixels;
	public double itemHeight;
	public DataBinding _items;
	public double _sliderPosInItems;

	public double windowSizeInItems;
	public Coords handleSize;
	private boolean isHighlighted;
	private ControlButton buttonScrollUp;
	private ControlButton buttonScrollDown;
	private String styleName;

	private Coords _drawPos;

	public ControlScrollbar
	(
		Coords pos, Coords size, double fontHeightInPixels, double itemHeight,
		DataBinding items, int sliderPosInItems
	)
	{
		this._pos = pos;
		this._size = size;
		this.fontHeightInPixels = fontHeightInPixels;
		this.itemHeight = itemHeight;
		this._items = items;
		this._sliderPosInItems = sliderPosInItems;

		this.windowSizeInItems = Math.floor(size.y / itemHeight);

		this.handleSize = new Coords(size.x, size.x);

		var scrollbar = this;

		this.buttonScrollUp = new ControlButton
		(
			null, // name
			new Coords(0, 0), // pos
			this.handleSize.clonify(), // size
			"-", // text
			this.fontHeightInPixels,
			true, // hasBorder
			true, // isEnabled
			() -> scrollbar.scrollUp() // click
		);

		this.buttonScrollDown = new ControlButton
		(
			null, // name
			new Coords(0, size.y - this.handleSize.y), // pos
			this.handleSize.clonify(), // size
			"+", // text
			this.fontHeightInPixels,
			true, // hasBorder
			true, // isEnabled
			() -> scrollbar.scrollDown() // click
		);

		// Helper variables.
		this._drawPos = new Coords();
	}

	public boolean actionHandle(String actionNameToHandle, Universe universe)
	{
		return true;
	}

	public Object[] items()
	{
		return (Object[])(this._items.get());
	}

	public void childFocus(Control child)
	{
		// todo
	}

	public void focusGain() {}
	public void focusLose() {}

	public boolean isEnabled()
	{
		return true; // todo
	}

	public boolean mouseClick(Coords clickPos)
	{
		return true; // todo
	}

	public void mouseEnter() {}
	public void mouseExit() {}

	public boolean mouseMove(Coords mousePos)
	{
		return true; // todo
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

	public Coords pos()
	{
		return this._pos;
	}

	public Control scalePosAndSize(Coords scaleFactor)
	{
		this._pos.multiply(scaleFactor);
		this._size.multiply(scaleFactor);
		this.handleSize.multiply(scaleFactor);
		this.fontHeightInPixels *= scaleFactor.y;
		this.buttonScrollUp.scalePosAndSize(scaleFactor);
		this.buttonScrollDown.scalePosAndSize(scaleFactor);
		return this;
	}

	public void scrollDown()
	{
		var sliderPosInItems = NumberHelper.trimToRangeMinMax
		(
			this.sliderPosInItems() + 1, 0, this.sliderMaxInItems()
		);

		this._sliderPosInItems = sliderPosInItems;
	}

	public void scrollUp()
	{
		var sliderPosInItems = NumberHelper.trimToRangeMinMax
		(
			this.sliderPosInItems() - 1, 0, this.sliderMaxInItems()
		);

		this._sliderPosInItems = sliderPosInItems;
	}

	public Coords size()
	{
		return this._size;
	}

	public Coords slideSizeInPixels()
	{
		var slideSizeInPixels = new Coords
		(
			this.handleSize.x,
			this.size().y - 2 * this.handleSize.y
		);

		return slideSizeInPixels;
	}

	public double sliderPosInItems()
	{
		return this._sliderPosInItems;
	}

	public double sliderMaxInItems()
	{
		return this.items().length - Math.floor(this.windowSizeInItems);
	}

	public Coords sliderPosInPixels()
	{
		var sliderPosInPixels = new Coords
		(
			this.size().x - this.handleSize.x,
			this.handleSize.y
				+ this.sliderPosInItems()
				* this.slideSizeInPixels().y
				/ this.items().length
		);

		return sliderPosInPixels;
	}

	public Coords sliderSizeInPixels()
	{
		var sliderSizeInPixels = this.slideSizeInPixels().multiply
		(
			new Coords(1, this.windowSizeInItems / this.items().length)
		);

		return sliderSizeInPixels;
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	}

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		var numberOfItems = this.items().length;

		if (this.windowSizeInItems < numberOfItems)
		{
			var style = this.style(universe);
			var colorFore = (this.isHighlighted ? style.colorFill : style.colorBorder);
			var colorBack = (this.isHighlighted ? style.colorBorder : style.colorFill);

			var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add(this.pos());
			display.drawRectangle(drawPos, this.size(), colorFore, null);

			drawLoc.pos.add(this.pos());
			this.buttonScrollDown.draw(universe, display, drawLoc);
			this.buttonScrollUp.draw(universe, display, drawLoc);

			var sliderPosInPixels = this.sliderPosInPixels().add(drawPos);
			var sliderSizeInPixels = this.sliderSizeInPixels();

			display.drawRectangle
			(
				sliderPosInPixels, sliderSizeInPixels,
				colorBack, colorFore
			);
		}
	}
}
