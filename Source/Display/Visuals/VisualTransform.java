package Display.Visuals;

public class VisualTransform implements Visual
{
	private Transform transformToApply;
	private Visual child;

	public VisualTransform(Transform transformToApply, Visual child)
	{
		this.transformToApply = transformToApply;
		this.child = child;
	}

	// Cloneable.

	public VisualTransform clonify()
	{
		return new VisualTransform(this.transformToApply, this.child.clonify());
	}

	public VisualTransform overwriteWith(VisualTransform other)
	{
		this.child.overwriteWith(other.child);
		return this;
	}

	// Transformable.

	public VisualTransform transform(Transform transformToApply)
	{
		return this.child.transform(transformToApply);
	}

	// Visual.

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		this.child.transform(this.transformToApply);
		this.child.draw(universe, world, display, entity);
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
