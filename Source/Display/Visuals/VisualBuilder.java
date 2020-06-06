package Display.Visuals;

import Geometry.*;

public class VisualBuilder
{
	public static VisualBuilder Instance = new VisualBuilder();

	public Visual circleWithEyes
	(
		double circleRadius, String circleColor, double eyeRadius, Visual visualEyes
	)
	{
		if (visualEyes == null)
		{
			visualEyes = this.eyesBlinking(eyeRadius);
		};

		this.visualEyes = visualEyes;

		var visualEyesDirectional = new VisualDirectional
		(
			visualEyes, // visualForNoDirection
			new Visual[]
			{
				new VisualOffset(visualEyes, new Coords(1, 0).multiplyScalar(eyeRadius)),
				new VisualOffset(visualEyes, new Coords(0, 1).multiplyScalar(eyeRadius)),
				new VisualOffset(visualEyes, new Coords(-1, 0).multiplyScalar(eyeRadius)),
				new VisualOffset(visualEyes, new Coords(0, -1).multiplyScalar(eyeRadius))
			}
		);

		var circleWithEyes = new VisualGroup
		(
			new Visual[]
			{
				new VisualCircle(circleRadius, circleColor),
				visualEyesDirectional
			}
		);

		return circleWithEyes;
	}

	public Visual eyesBlinking(double visualEyeRadius)
	{
		var visualPupilRadius = visualEyeRadius / 2;

		var visualEye = new VisualGroup
		(
			new Visual[]
			{
				new VisualCircle(visualEyeRadius, "White"),
				new VisualCircle(visualPupilRadius, "Black")
			}
		);

		var visualEyes = new VisualGroup
		(
			new Visual[]
			{
				new VisualOffset
				(
					visualEye, new Coords(-visualEyeRadius, 0)
				),
				new VisualOffset
				(
					visualEye, new Coords(visualEyeRadius, 0)
				)
			}
		);

		var visualEyesBlinking = new VisualAnimation
		(
			"EyesBlinking",
			new int[] { 50, 5 }, // ticksToHoldFrames
			new Visual[] { visualEyes, new VisualNone() }
		);

		return visualEyesBlinking;
	}
}
