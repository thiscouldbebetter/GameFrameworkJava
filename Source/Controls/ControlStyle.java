package Controls;

import Display.*;
import Geometry.*;
import Model.*;
import Utility.*;

public class ControlStyle implements Namable
{
	public String _name;
	public String colorBackground;
	public String colorFill;
	public String colorBorder;
	public String colorDisabled;

	public ControlStyle
	(
		String name, String colorBackground, String colorFill,
		String colorBorder, String colorDisabled
	)
	{
		this._name = name;
		this.colorBackground = colorBackground;
		this.colorFill = colorFill;
		this.colorBorder = colorBorder;
		this.colorDisabled = colorDisabled;
	}

	public static class Instances
	{
		public static ControlStyle Default = new ControlStyle
		(
			"Default", // name
			"rgb(240, 240, 240)", // colorBackground
			"White", // colorFill
			"Gray", // colorBorder
			"LightGray" // colorDisabled
		);
	}

	// Namable.

	public String name()
	{
		return this._name;
	}
}
