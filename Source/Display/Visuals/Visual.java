package Display.Visuals;

import Display.*;
import Model.*;
import Utility.*;

public interface Visual extends Clonable<Visual>
{
	public void draw(Universe universe, World world, Display display, Entity entity);
}
