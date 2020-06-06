package Display.Visuals;

import Display.*;
import Geometry.*;
import Geometry.Transforms.*;
import Model.*;

public class VisualJump2D implements Visual
{
	private Visual visualJumper;
	private Visual visualShadow;

	private Coords _posSaved = new Coords();

	public VisualJump2D(Visual visualJumper, Visual visualShadow)
	{
		this.visualJumper = visualJumper;
		this.visualShadow = visualShadow;

		this._posSaved = new Coords();
	}

	// Cloneable.

	public VisualJump2D clonify()
	{
		return new VisualJump2D
		(
			this.visualJumper.clonify(), this.visualShadow.clonify()
		);
	};

	public VisualJump2D overwriteWith(VisualJump2D other)
	{
		this.visualJumper.overwriteWith(other.visualJumper);
		this.visualShadow.overwriteWith(other.visualShadow);
		return this;
	};

	// Transformable.

	public VisualJump2D transform(Transform transformToApply)
	{
		transformToApply.transform( (Transformable)(this.visualJumper) );
		transformToApply.transform( (Transformable)(this.visualShadow) );
		return this;
	};

	// Visual.

	public void draw(Universe universe, World world, Display display, Entity entity)
	{
		var entityPos = entity.locatable().loc.pos;
		var entityPosZ = entityPos.z;
		var camera = world.placeCurrent.camera(); // hack
		entityPosZ -= camera.focalLength;
		var height = 0 - entityPosZ;
		if (height <= 0)
		{
			this.visualJumper.draw(universe, world, display, entity);
		}
		else
		{
			this.visualShadow.draw(universe, world, display, entity);
			this._posSaved.overwriteWith(entityPos);
			entityPos.y -= height;
			this.visualJumper.draw(universe, world, display, entity);
			entityPos.overwriteWith(this._posSaved);
		}
	};

	// Clonable.

	public Visual overwriteWith(Visual other)
	{
		var otherAsVisualJump2D = (VisualJump2D)other;
		this.visualJumper.overwriteWith(otherAsVisualJump2D.visualJumper);
		this.visualShadow.overwriteWith(otherAsVisualJump2D.visualShadow);
		return this;
	}
}
