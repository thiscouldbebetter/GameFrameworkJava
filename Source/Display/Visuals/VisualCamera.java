package Display.Visuals;

public class VisualCamera implements Visual
{
	private Visual child;
	private Supplier<Camera> cameraFactory;

	// Helper variables.
	private Coords _posSaved = new Coords();

	public VisualCamera(Visual child, Supplier<Camera> cameraFactory)
	{
		this.child = child;
		this.cameraFactory = cameraFactory;
	}

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var drawablePos = entity.locatable().loc.pos;
		this._posSaved.overwriteWith(drawablePos);

		var camera = this.cameraFactory(universe, world);
		camera.coordsTransformWorldToView(drawablePos);

		if (entity.Boundable == null) // todo
		{
			this.child.draw(universe, world, display, entity);
		}
		else
		{
			var drawableCollider = entity.boundable().bounds;
			var cameraViewCollider = camera.viewCollider;
			var isInCameraBox =
				universe.collisionHelper.doCollidersCollide(drawableCollider, cameraViewCollider);
			if (isInCameraBox)
			{
				this.child.draw(universe, world, display, entity);
			}
		}

		drawablePos.overwriteWith(this._posSaved);
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
