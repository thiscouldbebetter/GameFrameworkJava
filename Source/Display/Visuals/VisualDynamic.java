package Display.Visuals;

public class VisualDynamic implements Visual
{
	private Function<VisualDynamic,Universe,World,Entity,Visual> methodForVisual;

	public VisualDynamic(Function<VisualDynamic,Universe,World,Entity,Visual> methodForVisual)
	{
		this.methodForVisual = methodForVisual;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var visual = this.methodForVisual.call(this, universe, world, entity);
		visual.draw(universe, world, display, entity);
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
