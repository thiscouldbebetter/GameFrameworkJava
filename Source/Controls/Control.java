package Controls;

import Display.*;
import Geometry.*;
import Model.*;
import Utility.*;

public interface Control extends Namable
{
	boolean actionHandle(String actionName, Universe universe);
	void childFocus(Control child);
	void draw(Universe universe, Display display, Location drawLoc);
	void focusGain();
	void focusLose();
	boolean isEnabled();
	boolean mouseClick(Coords clickPos);
	void mouseEnter();
	void mouseExit();
	boolean mouseMove(Coords movePos);
	Control parent();
	Coords pos();
	Coords size();
	Control scalePosAndSize(Coords scaleFactor);
}
