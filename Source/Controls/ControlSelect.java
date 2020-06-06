package Controls;

import Display.*;
import Geometry.*;
import Model.*;
import Utility.*;

public class ControlSelect implements Control
{
	public String name;
	public Coords pos;
	public Coords size;
	public DataBinding bindingForValueSelected;
	public DataBinding _options;
	public DataBinding bindingForOptionValues;
	public DataBinding bindingForOptionText;
	public double fontHeightInPixels;

	private Integer indexOfOptionSelected;
	private boolean isHighlighted;
	private String styleName;

	private Coords _drawPos;
	private Coords _sizeHalf;
	private Object _valueSelected;

	public ControlSelect
	(
		String name,
		Coords pos,
		Coords size,
		DataBinding bindingForValueSelected,
		DataBinding options,
		DataBinding bindingForOptionValues,
		DataBinding bindingForOptionText,
		double fontHeightInPixels
	)
	{
		this.name = name;
		this.pos = pos;
		this.size = size;
		this.bindingForValueSelected = valueSelected;
		this._options = options;
		this.bindingForOptionValues = bindingForOptionValues;
		this.bindingForOptionText = bindingForOptionText;
		this.fontHeightInPixels = fontHeightInPixels;

		this.indexOfOptionSelected = null;
		this._valueSelected = this.valueSelected();
		var optionsFound = this.options();
		for (var i = 0; i < optionsFound.length; i++)
		{
			var option = optionsFound[i];
			var optionValue = this.bindingForOptionValues.contextSet
			(
				option
			).get();

			if (optionValue == valueSelected)
			{
				this.indexOfOptionSelected = i;
				break;
			}
		}

		this.isHighlighted = false;

		// Helper variables.
		this._drawPos = new Coords();
		this._sizeHalf = new Coords();
	}

	public boolean actionHandle(String actionNameToHandle)
	{
		if (actionNameToHandle == ControlActionNames.ControlDecrement)
		{
			this.optionSelectedNextInDirection(-1);
		}
		else if
		(
			actionNameToHandle == ControlActionNames.ControlIncrement
			|| actionNameToHandle == ControlActionNames.ControlConfirm
		)
		{
			this.optionSelectedNextInDirection(1);
		}
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
		// todo
		return true;
	}

	public Object optionSelected()
	{
		var optionSelected =
		(
			this.indexOfOptionSelected == null
			? null
			: this.options()[this.indexOfOptionSelected]
		);
		return optionSelected;
	}

	public void optionSelectedNextInDirection(int direction)
	{
		var options = this.options();

		this.indexOfOptionSelected = NumberHelper.wrapToRangeMinMax
		(
			this.indexOfOptionSelected + direction,
			0, options.length
		);

		var optionSelected = this.optionSelected();
		var valueToSelect =
		(
			optionSelected == null
			? null
			: this.bindingForOptionValues.contextSet(optionSelected).get()
		);

		this._valueSelected.set(valueToSelect);
	}

	public Object[] options()
	{
		return (Object[])(this._options.get());
	}

	public boolean mouseClick(Coords clickPos)
	{
		this.optionSelectedNextInDirection(1);
		return true; // wasClickHandled
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

	public Control scalePosAndSize(Coords scaleFactors)
	{
		return this; // todo
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	}

	public Object valueSelected()
	{
		return this._valueSelected.get();
	}

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add(this.pos);

		var style = this.style(universe);

		display.drawRectangle
		(
			drawPos, this.size,
			style.colorFill, style.colorBorder,
			this.isHighlighted // areColorsReversed
		);

		drawPos.add(this._sizeHalf.overwriteWith(this.size).half());

		var optionSelected = this.optionSelected();
		var text =
		(
			optionSelected == null
			? "-"
			: (String)( this.bindingForOptionText.contextSet(optionSelected).get() )
		);

		display.drawText
		(
			text,
			this.fontHeightInPixels,
			drawPos,
			style.colorBorder,
			style.colorFill,
			this.isHighlighted,
			true, // isCentered
			this.size.x // widthMaxInPixels
		);
	}
}
