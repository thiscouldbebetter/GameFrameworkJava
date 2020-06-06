package Controls;

import java.util.*;
import Display.*;
import Geometry.*;
import Input.*;
import Model.*;

public class ControlContainerTransparent implements Control
{
	private ControlContainer containerInner;

	private List<Control> _childrenContainingPos = new ArrayList<Control>;
	private Coords _drawPos = new Coords();

	public ControlContainerTransparent(ControlContainer containerInner)
	{
		this.containerInner = containerInner;
	}

	// instance methods

	public ActionToInputsMapping[] actionToInputsMappings()
	{
		return this.containerInner.actionToInputsMappings();
	}

	public Control childWithFocus()
	{
		return this.containerInner.childWithFocus();
	}

	public Control childWithFocusNextInDirection(int direction)
	{
		return this.containerInner.childWithFocusNextInDirection(direction);
	}

	public List<Control> childrenAtPosAddToList
	(
		Coords posToCheck, List<Control> listToAddTo, boolean addFirstChildOnly
	)
	{
		return this.containerInner.childrenAtPosAddToList
		(
			posToCheck, listToAddTo, addFirstChildOnly
		);
	}

	public boolean actionHandle(String actionNameToHandle, Universe universe)
	{
		return this.containerInner.actionHandle(actionNameToHandle, universe);
	}

	public void childFocus(Control child)
	{
		// todo
	}

	public void focusGain() {}
	public void focusLose() {}

	public boolean isEnabled()
	{
		return true;
	}

	public boolean mouseClick(Coords mouseClickPos)
	{
		this._childrenContainingPos.clear();
		var childrenContainingPos = this.containerInner.childrenAtPosAddToList
		(
			mouseClickPos,
			this._childrenContainingPos,
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
		this.containerInner.mouseMove(mouseMovePos);
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
		return this.containerInner.pos();
	}

	public Control scalePosAndSize(Coords scaleFactor)
	{
		return this.containerInner.scalePosAndSize(scaleFactor);
	}

	public Coords size()
	{
		return this.containerInner.size();
	}

	// drawable

	public void draw(Universe universe, Display display, Location drawLoc)
	{
		drawLoc = this.containerInner._drawLoc.overwriteWith(drawLoc);
		var drawPos = this._drawPos.overwriteWith(drawLoc.pos).add
		(
			this.containerInner.pos()
		);

		display.drawRectangle
		(
			drawPos, this.containerInner.size(),
			null, // display.colorBack,
			display.colorFore
		);

		var children = this.containerInner.children;
		for (var i = 0; i < children.length; i++)
		{
			var child = children[i];
			child.draw(universe, display, drawLoc);
		}
	}
}
