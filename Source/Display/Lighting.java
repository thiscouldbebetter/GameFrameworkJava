package Display;

public class Lighting
{
	public Lighting(ambientIntensity, direction, directionalIntensity)
	{
		this.ambientIntensity = ambientIntensity;
		this.direction = direction.clonify().normalize();
		this.directionalIntensity = directionalIntensity;
	}
}
