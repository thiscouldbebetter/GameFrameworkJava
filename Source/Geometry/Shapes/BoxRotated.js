
function BoxRotated(box, angleInTurns)
{
	this.box = box;
	this.angleInTurns = angleInTurns;
}
{
	BoxRotated.prototype.sphereSwept = function()
	{
		return new Sphere(this.box.center, this.box.sizeHalf.magnitude());
	};

	BoxRotated.prototype.surfaceNormalNearPos = function(posToCheck)
	{
		var returnValue = new Coords();

		var plane = new Plane(new Coords(), 0);
		var polar = new Polar(0, 1);
		var box = this.box;
		var center = box.center;
		var sizeHalf = box.sizeHalf;
		var displacementToSurface = new Coords();
		var distanceMinSoFar = Number.POSITIVE_INFINITY;

		for (var d = 0; d < 2; d++)
		{
			polar.azimuthInTurns = this.angleInTurns + (d * .25);
			var dimensionHalf = sizeHalf.dimensionGet(d);

			for (var m = 0; m < 2; m++)
			{
				var directionToSurface = polar.toCoords(plane.normal);
				displacementToSurface.overwriteWith
				(
					directionToSurface
				).multiplyScalar
				(
					dimensionHalf
				);
				var pointOnSurface = displacementToSurface.add(center);
				plane.distanceFromOrigin = pointOnSurface.dotProduct(plane.normal);

				var distanceOfPosToCheckFromPlane = Math.abs
				(
					plane.distanceToPointAlongNormal(posToCheck)
				);
				if (distanceOfPosToCheckFromPlane < distanceMinSoFar)
				{
					distanceMinSoFar = distanceOfPosToCheckFromPlane;
					returnValue.overwriteWith(plane.normal);
				}

				polar.azimuthInTurns += .5;
				polar.azimuthInTurns.wrapToRangeZeroOne();
			}
		}

		return returnValue;
	};

	// cloneable

	BoxRotated.prototype.clone = function()
	{
		return new BoxRotated(this.box.clone(), this.angleInTurns);
	};

	BoxRotated.prototype.overwriteWith = function(other)
	{
		this.box.overwriteWith(other.box);
		this.angleInTurns = other.angleInTurns;
		return this;
	};

	// transformable

	BoxRotated.prototype.coordsGroupToTranslate = function()
	{
		return [ this.box.center ];
	};
}
