package Display.Visuals;

import Display.*;
import Model.*;

public class VisualNone implements Visual
{
	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		// do nothing
	};

	// Clonable<Visual>.

	public Visual clonify()
	{
		return this; // todo
	}

	public Visual overwriteWith(Visual other)
	{
		return this; // todo
	}
}
