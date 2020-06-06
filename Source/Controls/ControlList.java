package Controls;

import java.util.*;
import java.util.function.*;
import Display.*;
import Geometry.*;
import Model.*;
import Utility.*;

public class ControlList implements Control
{
	public String name;
	public Coords pos;
	public Coords size;
	public DataBinding _items;
	public DataBinding bindingForItemText;
	public double fontHeightInPixels;
	public DataBinding bindingForItemSelected;
	public DataBinding bindingForItemValue;
	public DataBinding<Object,Boolean> bindingForIsEnabled;
	public Runnable confirm;

	private double itemSpacing;
	private boolean isHighlighted;
	private ControlScrollbar scrollbar;

	private Coords _drawPos;
	private Location _drawLoc;
	private Coords _mouseClickPos;

	private String styleName;

	public ControlList
	(
		String name, Coords pos, Coords size, DataBinding items,
		DataBinding bindingForItemText, double fontHeightInPixels
	)
	{
		this
		(
			name, pos, size, items, bindingForItemText, fontHeightInPixels,
			null, null // todo
		);
	}

	public ControlList
	(
		String name, Coords pos, Coords size, DataBinding items,
		DataBinding bindingForItemText, double fontHeightInPixels,
		DataBinding bindingForItemSelected, DataBinding bindingForItemValue
	)
	{
		this
		(
			name, pos, size, items, bindingForItemText, fontHeightInPixels,
			bindingForItemSelected, bindingForItemValue,
			null, null
		);
	}

	public ControlList
	(
		String name, Coords pos, Coords size, DataBinding items,
		DataBinding bindingForItemText, double fontHeightInPixels,
		DataBinding bindingForItemSelected, DataBinding bindingForItemValue,
		DataBinding bindingForIsEnabled, Runnable confirm
	)
	{
		this.name = name;
		this.pos = pos;
		this.size = size;
		this._items = items;
		this.bindingForItemText = bindingForItemText;
		this.fontHeightInPixels = fontHeightInPixels;
		this.bindingForItemSelected = bindingForItemSelected;
		this.bindingForItemValue = bindingForItemValue;
		this.bindingForIsEnabled = bindingForIsEnabled;
		this.confirm = confirm;

		this.itemSpacing = 1.2 * this.fontHeightInPixels; // hack

		this.isHighlighted = false;

		var scrollbarWidth = this.itemSpacing;
		this.scrollbar = new ControlScrollbar
		(
			new Coords(this.size.x - scrollbarWidth, 0), // pos
			new Coords(scrollbarWidth, this.size.y), // size
			this.fontHeightInPixels,
			this.itemSpacing, // itemHeight
			this._items
		);

		// Helper variables.
		this._drawPos = new Coords();
		this._drawLoc = new Location(this._drawPos);
		this._mouseClickPos = new Coords();
	}

	public static ControlList fromPosSizeAndItems(Coords pos, Coords size, DataBinding items)
	{
		var returnValue = new ControlList
		(
			"", // name,
			pos,
			size,
			items,
			new DataBinding(null), // bindingForItemText,
			10, // fontHeightInPixels,
			null, // bindingForItemSelected,
			null, // bindingForItemValue,
			new DataBinding(true), // bindingForIsEnabled
			0 // todo
		);

		return returnValue;
	}

	public static ControlList fromPosSizeItemsAndBindingForItemText
	(
		Coords pos, Coords size, DataBinding items, DataBinding bindingForItemText
	)
	{
		var returnValue = new ControlList
		(
			"", // name,
			pos,
			size,
			items,
			bindingForItemText,
			10, // fontHeightInPixels,
			null, // bindingForItemSelected,
			null, // bindingForItemValue,
			new DataBinding(true) // bindingForIsEnabled
		);

		return returnValue;
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
			if (this.confirm != null)
			{
				this.confirm.run();
				wasActionHandled = true;
			}
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

	public int indexOfFirstItemVisible()
	{
		return this.scrollbar.sliderPosInItems();
	}

	public int indexOfItemSelected()
	{
		var items = this.items();
		var itemSelected = this.itemSelected();
		var returnValue = Arrays.asList(items).indexOf(itemSelected);
		return returnValue;
	}

	public int indexOfItemSelected(int valueToSet)
	{
		var returnValue = valueToSet;
		var items = this.items();
		var itemToSelect = items[valueToSet];
		this.itemSelected(itemToSelect);
		return returnValue;
	}

	public int indexOfLastItemVisible()
	{
		return (int)(this.indexOfFirstItemVisible() + Math.floor(this.scrollbar.windowSizeInItems) - 1);
	}

	public boolean isEnabled()
	{
		return (this.bindingForIsEnabled == null ? true : this.bindingForIsEnabled.get());
	}

	public Object itemSelected()
	{
		Object returnValue;

		if (this.bindingForItemSelected == null)
		{
			returnValue = this._itemSelected;
		}
		else
		{
			returnValue = this.bindingForItemSelected.get();
		}

		return returnValue;
	}

	private Object _itemSelected;

	public Object itemSelected(Object itemToSet)
	{
		this._itemSelected = itemToSet;

		if (this.bindingForItemSelected != null)
		{
			var valueToSet = this.bindingForItemValue.contextSet
			(
				this._itemSelected
			).get();
			this.bindingForItemSelected.set(valueToSet);
		}

		return itemToSet;
	}

	public Object itemSelectedNextInDirection(int direction)
	{
		var items = this.items();
		var numberOfItems = items.length;

		var itemSelected = this.itemSelected();
		var indexOfItemSelected = this.indexOfItemSelected();

		if (indexOfItemSelected < 0)
		{
			if (numberOfItems > 0)
			{
				if (direction == 1)
				{
					indexOfItemSelected = 0;
				}
				else // if (direction == -1)
				{
					indexOfItemSelected = numberOfItems - 1;
				}
			}
		}
		else
		{
			indexOfItemSelected = NumberHelper.trimToRangeMinMax
			(
				indexOfItemSelected + direction, 0, numberOfItems - 1
			);
		}

		var itemToSelect = (indexOfItemSelected == null ? null : items[indexOfItemSelected]);
		this.itemSelected(itemToSelect);

		indexOfFirstItemVisible = this.indexOfFirstItemVisible();
		var indexOfLastItemVisible = this.indexOfLastItemVisible();

		var indexOfItemSelected = this.indexOfItemSelected();
		if (indexOfItemSelected < indexOfFirstItemVisible)
		{
			this.scrollbar.scrollUp();
		}
		else if (indexOfItemSelected > indexOfLastItemVisible)
		{
			this.scrollbar.scrollDown();
		}

		var returnValue = this.itemSelected();
		return returnValue;
	}

	public Object[] items()
	{
		return (Object[])( this._items.get() );
	}

	public boolean mouseClick(Coords clickPos)
	{
		clickPos = this._mouseClickPos.overwriteWith(clickPos);

		if (clickPos.x - this.pos.x > this.size.x - this.scrollbar.handleSize.x)
		{
			if (clickPos.y - this.pos.y <= this.scrollbar.handleSize.y)
			{
				this.scrollbar.scrollUp();
			}
			else if (clickPos.y - this.pos.y >= this.scrollbar.size.y - this.scrollbar.handleSize.y)
			{
				this.scrollbar.scrollDown();
			}
			else
			{
				var clickPosRelativeToSlideInPixels = clickPos.subtract
				(
					this.scrollbar.pos
				).subtract
				(
					new Coords(0, this.scrollbar.handleSize.y)
				);

				// todo
			}
		}
		else
		{
			var offsetOfItemClicked = clickPos.y - this.pos.y;
			var indexOfItemClicked = (int)
			(
				this.indexOfFirstItemVisible()
				+ Math.floor(offsetOfItemClicked / this.itemSpacing)
			);

			var items = this.items();
			if (indexOfItemClicked < items.length)
			{
				this.indexOfItemSelected(indexOfItemClicked);
			}
		}

		return true; // wasActionHandled
	}

	public void mouseEnter() {}
	public void mouseExit() {}

	public boolean mouseMove(Coords mousePos)
	{
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
		this.pos.multiply(scaleFactor);
		this.size.multiply(scaleFactor);
		this.fontHeightInPixels *= scaleFactor.y;
		this.itemSpacing *= scaleFactor.y;
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
		var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add(this.pos);

		var style = this.style(universe);
		var colorFore = (this.isHighlighted ? style.colorFill : style.colorBorder);
		var colorBack = (this.isHighlighted ? style.colorBorder : style.colorFill);

		display.drawRectangle
		(
			drawPos,
			this.size,
			colorBack, // fill
			style.colorBorder, // border
			false // areColorsReversed
		);

		var itemSizeY = this.itemSpacing;
		var textMarginLeft = 2;
		var itemPosY = drawPos.y;

		var items = this.items();

		if (items == null)
		{
			return;
		}

		var numberOfItemsVisible = Math.floor(this.size.y / itemSizeY);
		var indexStart = this.indexOfFirstItemVisible();
		var indexEnd = indexStart + numberOfItemsVisible - 1;
		if (indexEnd >= items.length)
		{
			indexEnd = items.length - 1;
		}

		var itemSelected = this.itemSelected();

		for (var i = indexStart; i <= indexEnd; i++)
		{
			var item = items[i];

			if (item == itemSelected)
			{
				display.drawRectangle
				(
					new Coords(drawPos.x, itemPosY), // pos
					new Coords(this.size.x,itemSizeY), // size
					colorFore // colorFill
				);
			}

			var text = (String)
			(
				this.bindingForItemText.contextSet(item).get()
			);

			var drawPos2 = new Coords(drawPos.x + textMarginLeft, itemPosY);

			display.drawText
			(
				text,
				this.fontHeightInPixels,
				drawPos2,
				colorFore,
				colorBack,
				(i == this.indexOfItemSelected()), // areColorsReversed
				false, // isCentered
				this.size.x // widthMaxInPixels
			);

			itemPosY += itemSizeY;
		}

		this.scrollbar.draw(universe, display, drawLoc);
	}
}
