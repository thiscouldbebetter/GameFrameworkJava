package Display.Visuals;

public class VisualMesh implements Visual
{
	private Mesh mesh;

	public VisualMesh(Mesh mesh)
	{
		this.mesh = mesh;
	}

	// Cloneable.

	public VisualMesh clonify()
	{
		return new VisualMesh(this.mesh.clonify());
	};

	public VisualMesh overwriteWith(other)
	{
		this.mesh.overwriteWith(other.mesh);
		return this;
	};

	// Transformable.

	public VisualMesh transform(Transform transformToApply)
	{
		return transformToApply.transform(this.mesh);
	}

	// Visual.

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		display.drawMeshWithOrientation(this.mesh, entity.locatable().loc.orientation);
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
