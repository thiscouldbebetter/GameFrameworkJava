package Controls;

import Display.*;
import Display.Visuals.*;
import Geometry.*;
import Model.*;
import Model.Physics.*;

public class ControlVisual implements Control
{
	public String _name;
	public Coords _pos;
	private Coords _size;
	public Visual visual;
	public String styleName;

	// Helper variables.
	private Coords _drawPos = new Coords();
	private Locatable _locatable = new Locatable(new Location(this._drawPos));
	private Entity _locatableEntity = new Entity("_drawableEntity", new EntityProperty[] { this._locatable } );
	private Coords _sizeHalf = new Coords();

	public ControlVisual(String name, Coords pos, Coords size, Visual visual)
	{
		this._name = name;
		this._pos = pos;
		this._size = size;
		this.visual = visual;
	}

	public ControlStyle style(Universe universe)
	{
		return universe.controlBuilder.stylesByName.get(this.styleName == null ? "Default" : this.styleName);
	}

	public void childFocus(Control child)
	{
		// todo
	}

	public boolean mouseClick(Coords mousePos)
	{
		return true; // todo
	}

	public void mouseEnter() {}
	public void mouseExit() {}

	public boolean mouseMove(Coords mousePos)
	{
		return true; // todo
	}

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
		var size = this.size();

		display.drawRectangle
		(
			drawPos, size, style.colorFill, style.colorBorder
		);

		var locatableEntity = this._locatableEntity;
		locatableEntity.locatable().loc.pos.overwriteWith(drawPos);
		drawPos.add(this._sizeHalf.overwriteWith(size).half());
		this.visual.draw(universe, universe.world, display, locatableEntity);
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
		return this; // todo
	}
}
