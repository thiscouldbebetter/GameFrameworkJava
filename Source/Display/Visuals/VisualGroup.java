package Display.Visuals;

import Display.*;
import Model.*;

public class VisualGroup implements Visual
{
	public Visual[] children;

	public VisualGroup(Visual[] children)
	{
		this.children = children;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		for (var i = 0; i < this.children.length; i++)
		{
			var child = this.children[i];
			child.draw(universe, world, display, entity);
		}
	}

	// Clonable.

	public Visual clonify()
	{
		return null; // todo
	}

	public Visual overwriteWith(Visual other)
	{
		return null; // todo
	}
}
