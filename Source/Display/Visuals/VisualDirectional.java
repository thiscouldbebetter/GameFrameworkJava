package Display.Visuals;

import Display.*;
import Model.*;
import Utility.*;

public class VisualDirectional implements Visual
{
	private Visual visualForNoDirection;
	private Visual[] visualsForDirections;

	private int numberOfDirections;

	public VisualDirectional(Visual visualForNoDirection, Visual[] visualsForDirections)
	{
		this.visualForNoDirection = visualForNoDirection;
		this.visualsForDirections = visualsForDirections;
		this.numberOfDirections = this.visualsForDirections.length;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var loc = entity.locatable().loc;
		var headingInTurns = loc.orientation.headingInTurns();
		Visual visualForHeading;

		if (headingInTurns == null)
		{
			visualForHeading = this.visualForNoDirection;
		}
		else
		{
			var direction = NumberHelper.wrapToRangeMinMax
			(
				Math.round(headingInTurns * this.numberOfDirections),
				0,
				this.numberOfDirections
			);
			visualForHeading = this.visualsForDirections[direction];
		}

		visualForHeading.draw(universe, world, display, entity);
	}

	// Clonable.

	public Visual clonify()
	{
		return this; // todo
	}

	public Visual overwriteWith(Visual other)
	{
		return this; // todo
	}

}
