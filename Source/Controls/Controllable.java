package Controls;

import java.util.function.*;
import Display.*;
import Geometry.*;
import Model.*;

public class Controllable
{
	public Function<UniverseWorldPlaceEntities,Control> _toControl;

	public Controllable(Function<UniverseWorldPlaceEntities,Control> toControl)
	{
		this._toControl = toControl;
	}

	public Control toControl(Universe universe, Coords size, Entity entity, Venue venue)
	{
		return this._toControl.apply
		(
			new UniverseWorldPlaceEntities(universe, universe.world, null, entity)
		); // hack
	}
}
