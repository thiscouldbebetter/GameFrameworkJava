package Controls;

import java.util.*;
import Display.*;
import Geometry.*;
import Model.*;

public class ControlTextArea implements Control
{
	public String _name;
	public Coords _pos;
	public Coords _size;
	public String text;
	public int fontHeightInPixels;
	private DataBinding _isEnabled;

	private double lineSpacing;
	private boolean isHighlighted;
	private ControlScrollbar scrollbar;
	private String styleName;

	private Coords _drawPos;
	private Location _drawLoc;
	private int _indexOfLineSelected;
	private Coords _mouseClickPos;
	private List<String> _textAsLines;

	public ControlTextArea
	(
		String name, Coords pos, Coords size, String text,
		int fontHeightInPixels, boolean isEnabled
	)
	{
		this(name, pos, size, text, fontHeightInPixels, new DataBinding(true));
	}

	public ControlTextArea
	(
		String name, Coords pos, Coords size, String text,
		int fontHeightInPixels, DataBinding isEnabled
	)
	{
		this._name = name;
		this._pos = pos;
		this._size = size;
		this.text = text;
		this.fontHeightInPixels = fontHeightInPixels;
		this._isEnabled = isEnabled;

		this.lineSpacing = 1.2 * this.fontHeightInPixels; // hack

		this.isHighlighted = false;

		var scrollbarWidth = this.lineSpacing;
		this.scrollbar = new ControlScrollbar
		(
			new Coords(this.size().x - scrollbarWidth, 0), // pos
			new Coords(scrollbarWidth, this.size().y), // size
			this.fontHeightInPixels,
			this.lineSpacing, // itemHeight
			new DataBinding<ControlTextArea,List<String>>(this, (ControlTextArea c) -> c.textAsLines()),
			0 // sliderPosInItems
		);

		// Helper variables.
		this._drawPos = new Coords();
		this._drawLoc = new Location(this._drawPos);
		this._mouseClickPos = new Coords();
	}

	public boolean actionHandle(String actionNameToHandle, Universe universe)
	{
		var wasActionHandled = false;
		if (actionNameToHandle == ControlActionNames.ControlIncrement)
		{
			this.itemSelectedNextInDirection(1);
			wasActionHandled = true;
		}
		else if (actionNameToHandle == ControlActionNames.ControlDecrement)
		{
			this.itemSelectedNextInDirection(-1);
			wasActionHandled = true;
		}
		else if (actionNameToHandle == ControlActionNames.ControlConfirm)
		{
			// todo
		}
		return wasActionHandled;
	}

	public void childFocus(Control child)
	{
		// todo
	}

	public void focusGain()
	{
		this.isHighlighted = true;
	}

	public void focusLose()
	{
		this.isHighlighted = false;
	}

	public int indexOfFirstLineVisible()
	{
		return (int)( this.scrollbar.sliderPosInItems() );
	}

	public int indexOfLastLineVisible()
	{
		return this.indexOfFirstLineVisible() + (int)( Math.floor(this.scrollbar.windowSizeInItems) ) - 1;
	}

	public int indexOfLineSelected()
	{
		var lines = this.textAsLines();
		var returnValue = _indexOfLineSelected;
		return returnValue;
	}

	public int indexOfLineSelected(int valueToSet)
	{
		var returnValue = valueToSet;
		var lines = this.textAsLines();
		_indexOfLineSelected = valueToSet;
		return returnValue;
	}

	public boolean isEnabled()
	{
		return (Boolean)(this._isEnabled.get());
	}

	public void itemSelectedNextInDirection(int direction)
	{
		// todo
	}

	public Coords pos()
	{
		return this._pos;
	}

	public Coords size()
	{
		return this._size;
	}

	public List<String> textAsLines()
	{
		if (this._textAsLines == null)
		{
			this._textAsLines = new ArrayList<String>();

			var charWidthInPixels = this.fontHeightInPixels / 2; // hack
			var charsPerLine = (int)( Math.floor(this.size().x / charWidthInPixels) );
			var textComplete = this.text;
			var textLength = textComplete.length();
			var i = 0;
			while (i < textLength)
			{
				var line = textComplete.substring(i, i + charsPerLine);
				this._textAsLines.add(line);
				i += charsPerLine;
			}
		}

		return this._textAsLines;
	}

	public boolean mouseClick(Coords clickPos)
	{
		clickPos = this._mouseClickPos.overwriteWith(clickPos);

		var pos = this.pos();
		var size = this.size();
		if (clickPos.x - pos.x > size.x - this.scrollbar.handleSize.x)
		{
			if (clickPos.y - pos.y <= this.scrollbar.handleSize.y)
			{
				this.scrollbar.scrollUp();
			}
			else if (clickPos.y - pos.y >= this.scrollbar.size().y - this.scrollbar.handleSize.y)
			{
				this.scrollbar.scrollDown();
			}
			else
			{
				var clickPosRelativeToSlideInPixels = clickPos.subtract
				(
					this.scrollbar.pos()
				).subtract
				(
					new Coords(0, this.scrollbar.handleSize.y)
				);

				// todo
			}
		}
		else
		{
			var offsetOfLineClicked = clickPos.y - pos.y;
			var indexOfLineClicked =
				this.indexOfFirstLineVisible()
				+ (int)
				(
					Math.floor
					(
						offsetOfLineClicked / this.lineSpacing
					)
				);

			var lines = this.textAsLines();
			if (indexOfLineClicked < lines.size())
			{
				this.indexOfLineSelected(indexOfLineClicked);
			}
		}

		return true; // wasActionHandled
	}

	public void mouseEnter() {}
	public void mouseExit() {}

	public boolean mouseMove(Coords movePos)
	{
		// Do nothing.
		return true;
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

	public Control scalePosAndSize(Coords scaleFactor)
	{
		this._pos.multiply(scaleFactor);
		this._size.multiply(scaleFactor);
		this.fontHeightInPixels *= scaleFactor.y;
		this.lineSpacing *= scaleFactor.y;
		this.scrollbar.scalePosAndSize(scaleFactor);
		return this;
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	}

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		drawLoc = this._drawLoc.overwriteWith(drawLoc);
		var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add(this.pos());

		var style = this.style(universe);
		var colorFore = (this.isHighlighted ? style.colorFill : style.colorBorder);
		var colorBack = (this.isHighlighted ? style.colorBorder : style.colorFill);

		display.drawRectangle
		(
			drawPos,
			this.size(),
			colorBack, // fill
			style.colorBorder, // border
			false // areColorsReversed
		);

		var itemSizeY = this.lineSpacing;
		var textMarginLeft = 2;
		var itemPosY = drawPos.y;

		var lines = this.textAsLines();

		if (lines == null)
		{
			return;
		}

		var numberOfLinesVisible = Math.floor(this.size().y / itemSizeY);
		var indexStart = this.indexOfFirstLineVisible();
		var indexEnd = indexStart + numberOfLinesVisible - 1;
		if (indexEnd >= lines.size())
		{
			indexEnd = lines.size() - 1;
		}

		var drawPos2 = new Coords(drawPos.x + textMarginLeft, itemPosY);

		for (var i = indexStart; i <= indexEnd; i++)
		{
			var line = lines.get(i);

			display.drawText
			(
				line,
				this.fontHeightInPixels,
				drawPos2,
				colorFore,
				colorBack,
				false, // areColorsReversed
				false, // isCentered
				this.size().x // widthMaxInPixels
			);

			drawPos2.y += itemSizeY;
		}

		this.scrollbar.draw(universe, display, drawLoc);
	}

	// Namable.

	public String name()
	{
		return this._name;
	}
}
