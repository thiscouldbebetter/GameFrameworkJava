package Display;

public class Gradient
{
	private Gradient.Stop[] stops;

	public Gradient(Gradient.Stop[] stops)
	{
		this.stops = stops;
	}

	public class Stop
	{
		private double position;
		private String color;

		public Stop(double position, String color)
		{
			this.position = position;
			this.color = color;
		}
	}

}
