package Controls;

import java.util.*;
import java.util.function.*;
import Display.*;
import Geometry.*;
import Model.*;
import Utility.*;

public class ControlTabbed implements Control
{
	public String _name;
	public Coords _pos;
	public Coords _size;
	public Control[] children;
	public Map<String,Control> childrenByName;
	public int fontHeightInPixels;
	public String styleName;

	private ControlButton[] buttonsForChildren;
	private int childSelectedIndex;
	private boolean isChildSelectedActive;

	private Coords _childMax = new Coords();
	private Coords _drawPos = new Coords();
	private Location _drawLoc = new Location(this._drawPos);
	private Coords _mouseClickPos = new Coords();
	private Coords _mouseMovePos = new Coords();
	private Coords _posToCheck = new Coords();	

	public ControlTabbed
	(
		String name, Coords pos, Coords size, Control[] children, int fontHeightInPixels
	)
	{
		this._name = name;
		this._pos = pos;
		this._size = size;
		this.children = children;
		this.childrenByName = ArrayHelper.addLookupsByName(children);

		this.childSelectedIndex = 0;
		this.isChildSelectedActive = false;

		var marginSize = fontHeightInPixels;
		var buttonSize = new Coords(50, fontHeightInPixels * 2);
		var buttonsForChildren = new ControlButton[this.children.length];

		for (var i = 0; i < this.children.length; i++)
		{
			var child = this.children[i];

			child.pos().y += marginSize + buttonSize.y;

			var childName = child.name();
			var button = new ControlButton
			(
				"button" + childName,
				new Coords(marginSize + buttonSize.x * i, marginSize), // pos
				buttonSize.clonify(),
				childName, // text
				fontHeightInPixels,
				true, // hasBorder
				true, // isEnabled
				() ->
				{
					// todo
				}
			);
			buttonsForChildren[i] = button;
		}
		this.buttonsForChildren = buttonsForChildren;

		// Temporary variables.
		this._drawPos = new Coords();
		this._drawLoc = new Location(this._drawPos);
		this._mouseClickPos = new Coords();
		this._mouseMovePos = new Coords();
	}

	// instance methods

	public boolean isEnabled()
	{
		return true;
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	}

	// actions

	public boolean actionHandle(String actionNameToHandle, Universe universe)
	{
		var wasActionHandled = false;

		var childSelected = this.childSelected();

		if (this.isChildSelectedActive)
		{
			if (actionNameToHandle == ControlActionNames.ControlCancel)
			{
				this.isChildSelectedActive = false;
				childSelected.focusLose();
				wasActionHandled = true;
			}
			else
			{
				wasActionHandled = childSelected.actionHandle(actionNameToHandle, universe);
			}
		}
		else
		{
			wasActionHandled = true;

			if (actionNameToHandle == ControlActionNames.ControlConfirm)
			{
				this.isChildSelectedActive = true;
				childSelected.focusGain();
			}
			else if
			(
				actionNameToHandle == ControlActionNames.ControlPrev
				|| actionNameToHandle == ControlActionNames.ControlNext
			)
			{
				var direction = (actionNameToHandle == ControlActionNames.ControlPrev ? -1 : 1);
				childSelected.focusLose();
				childSelected = this.childSelectNextInDirection(direction);
			}
		}

		return wasActionHandled;
	}

	public void childFocus(Control child)
	{
		// todo
	}

	public Control childSelected()
	{
		return this.children[this.childSelectedIndex];
	}

	public Control childSelectNextInDirection(int direction)
	{
		var childIndexOriginal = this.childSelectedIndex;

		while (true)
		{
			this.childSelectedIndex += direction;

			var isChildNextInRange = NumberHelper.isInRangeMinMax
			(
				this.childSelectedIndex, 0, this.children.length - 1
			);

			if (isChildNextInRange == false)
			{
				this.childSelectedIndex = NumberHelper.wrapToRangeMax
				(
					this.childSelectedIndex, this.children.length
				);
			}

			var child = this.children[this.childSelectedIndex];
			if (child.isEnabled())
			{
				break;
			}

		} // end while (true)

		var returnValue = this.childSelected();

		return returnValue;
	}

	public List<Control> childrenAtPosAddToList
	(
		Coords posToCheck, List<Control> listToAddTo, boolean addFirstChildOnly
	)
	{
		posToCheck = this._posToCheck.overwriteWith(posToCheck).clearZ();

		for (var i = this.children.length - 1; i >= 0; i--)
		{
			var child = this.children[i];

			var doesChildContainPos = posToCheck.isInRangeMinMax
			(
				child.pos(),
				this._childMax.overwriteWith(child.pos()).add(child.size())
			);

			if (doesChildContainPos)
			{
				listToAddTo.add(child);
				if (addFirstChildOnly)
				{
					break;
				}
			}
		}

		return listToAddTo;
	}

	public void focusGain()
	{
		this.childSelectedIndex = -1;
		var childSelected = this.childSelectNextInDirection(1);
		if (childSelected != null)
		{
			childSelected.focusGain();
		}
	}

	public void focusLose()
	{
		var childSelected = this.childSelected();
		if (childSelected != null)
		{
			childSelected.focusLose();
			this.childSelectedIndex = -1;
		}
	}

	public boolean mouseClick(Coords mouseClickPos)
	{
		mouseClickPos = this._mouseClickPos.overwriteWith
		(
			mouseClickPos
		).subtract
		(
			this.pos()
		);

		var wasClickHandled = false;
		var child = this.childSelected();
		var wasClickHandledByChild = child.mouseClick(mouseClickPos);
		if (wasClickHandledByChild)
		{
			wasClickHandled = true;
		}

		return wasClickHandled;
	}

	public void mouseEnter() {}
	public void mouseExit() {}

	public boolean mouseMove(Coords mouseMovePos)
	{
		this._mouseMovePos = this._mouseMovePos.overwriteWith
		(
			mouseMovePos
		).subtract
		(
			this.pos()
		);

		var wasMoveHandled = false;
		var child = this.childSelected();
		var wasMoveHandledByChild = child.mouseMove(mouseMovePos);
		if (wasMoveHandledByChild)
		{
			wasMoveHandled = true;
		}

		return wasMoveHandled;
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
		this.pos().multiply(scaleFactor);
		this.size().multiply(scaleFactor);

		for (var i = 0; i < this.children.length; i++)
		{
			var child = this.children[i];
			child.scalePosAndSize(scaleFactor);
		}

		return this;
	}

	public Venue toVenue()
	{
		return new VenueFader(new VenueControls(this));
	}

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		drawLoc = this._drawLoc.overwriteWith(drawLoc);
		var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add(this.pos());
		var style = this.style(universe);

		display.drawRectangle
		(
			drawPos, this.size(),
			style.colorBackground, style.colorBorder
		);

		var buttons = this.buttonsForChildren;
		for (var i = 0; i < buttons.length; i++)
		{
			var button = buttons[i];
			button.isHighlighted = (i == this.childSelectedIndex);
			button.draw(universe, display, drawLoc);
		}

		var child = this.childSelected();
		child.draw(universe, display, drawLoc);
	}

	public String name() { return this._name; }
	public Coords pos() { return this._pos; }
	public Coords size() { return this._size; }

}
