package Display.Visuals;

public class VisualInvisible implements Visual
{
	private Visual child;
	
	public VisualInvisible(Visual child)
	{
		this.child = child;
	}

	// Cloneable.

	public VisualInvisible clonify()
	{
		return new VisualInvisible(this.child.clonify());
	};

	public VisualInvisible overwriteWith(VisualInvisible other)
	{
		this.child.overwriteWith(other.child);
	};

	// Transformable.

	public Object transform(Transform transformToApply)
	{
		return transformToApply.transform(this.child);
	}

	// Visual.

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		// Do nothing.
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
