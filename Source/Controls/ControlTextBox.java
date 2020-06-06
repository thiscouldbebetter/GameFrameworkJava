package Controls;

import java.util.*;
import Display.*;
import Geometry.*;
import Model.*;
import Utility.*;

public class ControlTextBox implements Control
{
	public String name;
	public Coords _pos;
	private Coords _size;
	public DataBinding _text;
	public int fontHeightInPixels;
	public int numberOfCharsMax;

	private Control parent;
	private boolean isHighlighted;
	private int cursorPos;
	private String styleName;

	private Coords _drawPos = new Coords();
	private Coords _drawPosText = new Coords();
	private Location _drawLoc = new Location(_drawPos);
	private Coords _textMargin = new Coords();
	private Coords _textSize = new Coords();

	public ControlTextBox
	(
		String name, Coords pos, Coords size, DataBinding text,
		int fontHeightInPixels, int numberOfCharsMax
	)
	{
		this.name = name;
		this._pos = pos;
		this._size = size;
		this._text = text;
		this.fontHeightInPixels = fontHeightInPixels;
		this.numberOfCharsMax = numberOfCharsMax;

		this.isHighlighted = false;
		this.cursorPos = this.text().length();
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	}

	public String text()
	{
		return (String)(this._text.get());
	}

	public void text(String value)
	{
		this._text.set(value);
	}

	// events

	public boolean actionHandle(String actionNameToHandle, Universe universe)
	{
		var text = this.text();

		if (actionNameToHandle == ControlActionNames.ControlCancel)
		{
			this.text(text.substring(0, text.length() - 1));

			this.cursorPos = NumberHelper.wrapToRangeMinMax
			(
				this.cursorPos - 1, 0, text.length() + 1
			);
		}
		else if (actionNameToHandle == ControlActionNames.ControlConfirm)
		{
			this.cursorPos = NumberHelper.wrapToRangeMinMax(this.cursorPos + 1, 0, text.length() + 1);
		}
		else if
		(
			actionNameToHandle == ControlActionNames.ControlIncrement
			|| actionNameToHandle == ControlActionNames.ControlDecrement
		)
		{
			// This is a bit counterintuitive.
			var direction = (actionNameToHandle == ControlActionNames.ControlIncrement ? -1 : 1);

			var charCodeAtCursor =
			(
				this.cursorPos < text.length() ? text.charAt(this.cursorPos) : 'A' - 1
			);

			charCodeAtCursor = (char)
			(
				NumberHelper.wrapToRangeMinMax
				(
					charCodeAtCursor + direction,
					(int)'A',
					(int)'Z' + 1
				)
			);

			var charAtCursor = new String ( new char[] {charCodeAtCursor } );

			this.text
			(
				text.substring(0, this.cursorPos)
				+ charAtCursor
				+ text.substring(this.cursorPos + 1)
			);
		}
		else if (actionNameToHandle.length() == 1 || actionNameToHandle.startsWith("_") ) // printable character
		{
			if (actionNameToHandle.startsWith("_"))
			{
				if (actionNameToHandle == "_")
				{
					actionNameToHandle = " ";
				}
				else
				{
					actionNameToHandle = actionNameToHandle.substring(1);
				}
			}

			if (this.numberOfCharsMax == 0 || text.length() < this.numberOfCharsMax)
			{
				this.text
				(
					text.substring(0, this.cursorPos)
					+ actionNameToHandle
					+ text.substring(this.cursorPos + 1)
				);

				this.cursorPos = NumberHelper.wrapToRangeMinMax
				(
					this.cursorPos + 1, 0, text.length() + 1
				);
			}
		}

		return true; // wasActionHandled
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

	public boolean isEnabled()
	{
		return true; // todo
	}

	public boolean actionHandle(String actionName)
	{
		return true; // todo
	}

	public boolean mouseClick(Coords mouseClickPos)
	{
		var parentAsControl = this.parent();
		var parent = (ControlContainer)parentAsControl;
		parent.childFocus(Arrays.asList(parent.children).indexOf(this));
		this.isHighlighted = true;
	}

	public void mouseEnter() {}
	public void mouseExit() {}

	public boolean mouseMove(Coords mouseMovePos)
	{}

	public Coords pos()
	{
		return this._pos;
	}

	public Coords size()
	{
		return this._size;
	}

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add(this.pos());
		var style = this.style(universe);

		var text = this.text();

		display.drawRectangle
		(
			drawPos, this.size(),
			style.colorFill, style.colorBorder,
			this.isHighlighted // areColorsReversed
		);

		var textWidth =
			display.textWidthForFontHeight(text, this.fontHeightInPixels);
		var textSize =
			this._textSize.overwriteWithDimensions(textWidth, this.fontHeightInPixels, 0);
		var textMargin =
			this._textMargin.overwriteWith(this.size()).subtract(textSize).half();
		var drawPosText =
			this._drawPosText.overwriteWith(drawPos).add(textMargin);

		display.drawText
		(
			text,
			this.fontHeightInPixels,
			drawPosText,
			style.colorBorder,
			style.colorFill,
			this.isHighlighted,
			false, // isCentered
			this.size().x // widthMaxInPixels
		);

		if (this.isHighlighted)
		{
			var textBeforeCursor = text.substring(0, this.cursorPos);
			var textAtCursor = text.substring(this.cursorPos, this.cursorPos + 1);
			var cursorX = display.textWidthForFontHeight
			(
				textBeforeCursor, this.fontHeightInPixels
			);
			var cursorWidth = display.textWidthForFontHeight
			(
				textAtCursor, this.fontHeightInPixels
			);
			drawPosText.x += cursorX;

			display.drawRectangle
			(
				drawPosText,
				new Coords(cursorWidth, this.fontHeightInPixels), // size
				style.colorFill,
				style.colorFill
			);

			display.drawText
			(
				textAtCursor,
				this.fontHeightInPixels,
				drawPosText,
				style.colorBorder,
				null, // colorBack
				false, // isHighlighted
				false, // isCentered
				this.size().x // widthMaxInPixels
			);
		}
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
}
