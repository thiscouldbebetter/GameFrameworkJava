package Controls;

import java.util.*;
import Display.*;
import Geometry.*;
import Input.*;
import Model.*;
import Utility.*;

public class ControlContainer implements Control
{
	public String _name;
	public Coords _pos;
	public Coords _size;
	public Control[] children;
	public Map<String,Control> childrenByName;
	public Action[] actions;
	public Map<String,Action> actionsByName;
	public ActionToInputsMapping[] _actionToInputsMappings;

	public List<Control> childrenContainingPos;
	public List<Control> childrenContainingPosPrev;
	private int indexOfChildWithFocus;
	private String styleName;

	private Coords _childMax;
	private Coords _drawPos;
	public Location _drawLoc;
	private Coords _mouseClickPos;
	private Coords _mouseMovePos;
	private Coords _posToCheck;

	public ControlContainer
	(
		String name, Coords pos, Coords size, Control[] children
	)
	{
		this(name, pos, size, children, new Action[0]);
	}

	public ControlContainer
	(
		String name, Coords pos, Coords size, Control[] children, Action[] actions
	)
	{
		this(name, pos, size, children, actions, new ActionToInputsMapping[0]);
	}

	public ControlContainer
	(
		String name, Coords pos, Coords size, Control[] children,
		Action[] actions, ActionToInputsMapping[] actionToInputsMappings
	)
	{
		this._name = name;
		this._pos = pos;
		this._size = size;
		this.children = children;
		this.childrenByName = ArrayHelper.addLookupsByName(children);
		this.actions = actions;
		this.actionsByName = ArrayHelper.addLookupsByName(this.actions);
		this._actionToInputsMappings = actionToInputsMappings;

		for (var i = 0; i < this.children.length; i++)
		{
			var child = this.children[i];
			child.parent(this);
		}

		this.indexOfChildWithFocus = -1;
		this.childrenContainingPos = new ArrayList<Control>();
		this.childrenContainingPosPrev = new ArrayList<Control>();

		// Helper variables.
		this._childMax = new Coords();
		this._drawPos = new Coords();
		this._drawLoc = new Location(this._drawPos);
		this._mouseClickPos = new Coords();
		this._mouseMovePos = new Coords();
		this._posToCheck = new Coords();
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

		var childWithFocus = this.childWithFocus();

		if
		(
			actionNameToHandle == ControlActionNames.ControlPrev
			|| actionNameToHandle == ControlActionNames.ControlNext
		)
		{
			wasActionHandled = true;

			var direction = (actionNameToHandle == ControlActionNames.ControlPrev ? -1 : 1);

			if (childWithFocus == null)
			{
				childWithFocus = this.childWithFocusNextInDirection(direction);
				if (childWithFocus != null)
				{
					childWithFocus.focusGain();
				}
			}
			else if (childWithFocus.childWithFocus != null)
			{
				childWithFocus.actionHandle(actionNameToHandle, universe);
				if (childWithFocus.childWithFocus() == null)
				{
					childWithFocus = this.childWithFocusNextInDirection(direction);
					if (childWithFocus != null)
					{
						childWithFocus.focusGain();
					}
				}
			}
			else
			{
				childWithFocus.focusLose();
				childWithFocus = this.childWithFocusNextInDirection(direction);
				if (childWithFocus != null)
				{
					childWithFocus.focusGain();
				}
			}
		}
		else if (this.actionsByName.get(actionNameToHandle) != null)
		{
			var action = this.actionsByName.get(actionNameToHandle);
			action.perform.accept
			(
				new UniverseWorldPlaceEntities(universe, universe.world, null, null)
			);
			wasActionHandled = true;
		}
		else if (childWithFocus != null)
		{
			wasActionHandled = childWithFocus.actionHandle(actionNameToHandle, universe);
		}

		return wasActionHandled;
	}

	public ActionToInputsMapping[] actionToInputsMappings()
	{
		return this._actionToInputsMappings;
	}

	public void childFocus(Control child)
	{
		// todo
	}

	public Control childWithFocus()
	{
		return (this.indexOfChildWithFocus == -1 ? null : this.children[this.indexOfChildWithFocus] );
	}

	public Control childWithFocusNextInDirection(int direction)
	{
		if (this.indexOfChildWithFocus == -1)
		{
			var iStart = (direction == 1 ? 0 : this.children.length - 1);
			var iEnd = (direction == 1 ? this.children.length : -1);

			for (var i = iStart; i != iEnd; i += direction)
			{
				var child = this.children[i];
				if (child.isEnabled())
				{
					this.indexOfChildWithFocus = i;
					break;
				}
			}
		}
		else
		{
			var childIndexOriginal = this.indexOfChildWithFocus;

			while (true)
			{
				this.indexOfChildWithFocus += direction;

				var isChildNextInRange = NumberHelper.isInRangeMinMax
				(
					this.indexOfChildWithFocus, 0, this.children.length - 1
				);

				if (isChildNextInRange == false)
				{
					this.indexOfChildWithFocus = -1;
					break;
				}
				else
				{
					var child = this.children[this.indexOfChildWithFocus];
					if(child.isEnabled())
					{
						break;
					}
				}

			} // end while (true)

		} // end if

		var returnValue = this.childWithFocus();

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
			var childPos = child.pos();

			var doesChildContainPos = posToCheck.isInRangeMinMax
			(
				childPos,
				this._childMax.overwriteWith(childPos).add(child.size())
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
		this.indexOfChildWithFocus = -1;
		var childWithFocus = this.childWithFocusNextInDirection(1);
		if (childWithFocus != null)
		{
			childWithFocus.focusGain();
		}
	}

	public void focusLose()
	{
		var childWithFocus = this.childWithFocus();
		if (childWithFocus != null)
		{
			childWithFocus.focusLose();
			this.indexOfChildWithFocus = -1;
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

		this.childrenContainingPos.clear();

		var childrenContainingPos = this.childrenAtPosAddToList
		(
			mouseClickPos, this.childrenContainingPos,
			true // addFirstChildOnly
		);

		var wasClickHandled = false;
		if (childrenContainingPos.size() > 0)
		{
			var child = childrenContainingPos.get(0);
			var wasClickHandledByChild = child.mouseClick(mouseClickPos);
			if (wasClickHandledByChild)
			{
				wasClickHandled = true;
			}
		}

		return wasClickHandled;
	}

	public void mouseEnter() {}
	public void mouseExit() {}

	public boolean mouseMove(Coords mouseMovePos)
	{
		var temp = this.childrenContainingPosPrev;
		this.childrenContainingPosPrev = this.childrenContainingPos;
		this.childrenContainingPos = temp;

		mouseMovePos = this._mouseMovePos.overwriteWith
		(
			mouseMovePos
		).subtract
		(
			this.pos()
		);

		this.childrenContainingPos.clear();

		var childrenContainingPos = this.childrenAtPosAddToList
		(
			mouseMovePos,
			this.childrenContainingPos,
			true // addFirstChildOnly
		);

		for (var i = 0; i < childrenContainingPos.size(); i++)
		{
			var child = childrenContainingPos.get(i);

			child.mouseMove(mouseMovePos);

			if (this.childrenContainingPosPrev.indexOf(child) == -1)
			{
				child.mouseEnter();
			}
		}

		for (var i = 0; i < this.childrenContainingPosPrev.size(); i++)
		{
			var child = this.childrenContainingPosPrev.get(i);
			if (childrenContainingPos.indexOf(child) == -1)
			{
				child.mouseExit();
			}
		}

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

	public Coords pos()
	{
		return this._pos;
	}

	public Control scalePosAndSize(Coords scaleFactor)
	{
		this._pos.multiply(scaleFactor);
		this._size.multiply(scaleFactor);

		for (var i = 0; i < this.children.length; i++)
		{
			var child = this.children[i];
			/*
			if (child.scalePosAndSize == null)
			{
				child.pos.multiply(scaleFactor);
				child.size.multiply(scaleFactor);
				if (child.fontHeightInPixels != null)
				{
					child.fontHeightInPixels *= scaleFactor.y;
				}
			}
			*/
			
			child.scalePosAndSize(scaleFactor);
			
		}

		return this;
	}

	public void shiftChildPositions(Coords displacement)
	{
		for (var i = 0; i < this.children.length; i++)
		{
			var child = this.children[i];
			child.pos.add(displacement);
		}
	}

	public Coords size()
	{
		return this._size;
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

		var children = this.children;
		for (var i = 0; i < children.length; i++)
		{
			var child = children[i];
			child.draw(universe, display, drawLoc);
		}
	}

	// Namable.

	public String name()
	{
		return this._name;
	}
}
