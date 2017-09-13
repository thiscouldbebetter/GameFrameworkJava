
function Map(sizeInCells, cellSize, cellPrototype, cellAtPosInCells, cellSource)
{
	this.sizeInCells = sizeInCells;
	this.cellSize = cellSize;
	this.cellPrototype = cellPrototype;
	this.cellAtPosInCells = cellAtPosInCells.bind(this);
	this.cellSource = cellSource;

	this.sizeInCellsMinusOnes = this.sizeInCells.clone().subtract
	(
		Coords.Instances.Ones
	);
	this.size = this.sizeInCells.clone().multiply(this.cellSize);
	this.sizeHalf = this.size.clone().divideScalar(2);
	this.cellSizeHalf = this.cellSize.clone().divideScalar(2);

	// Helper variables.

	this.posInCells = new Coords();
}
{
	Map.prototype.cellAtPos = function(cellPos, cellToOverwrite)
	{
		this.posInCells.overwriteWith(cellPos).divide(this.cellSize).floor();
		return this.cellAtPosInCells(this.posInCells);
	}

	Map.prototype.numberOfCells = function()
	{
		return this.sizeInCells.x * this.sizeInCells.y;
	}
}