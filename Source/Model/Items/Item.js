
function Item(defnName, quantity)
{
	this.defnName = defnName;
	this.quantity = quantity;
}
{
	Item.prototype.defn = function(world)
	{
		return world.defns.itemDefns[this.defnName];
	};

	Item.prototype.isUsable = function(world)
	{
		return (this.defn(world).use != null);
	};

	Item.prototype.toEntity = function()
	{
		// todo
		return new Entity(this.defnName, [ this ]);
	};

	Item.prototype.toString = function(world)
	{
		return this.defn(world).appearance + " (" + this.quantity + ")";
	};

	Item.prototype.tradeValue = function(world)
	{
		return this.quantity * this.defn(world).tradeValue;
	};

	Item.prototype.use = function(universe, world, place, userEntity, itemEntity)
	{
		var returnValue = null;
		var defn = this.defn(world);
		if (defn.use != null)
		{
			returnValue = defn.use(universe, world, place, userEntity, itemEntity, this);
		}
		return returnValue;
	};

	// cloneable

	Item.prototype.clone = function()
	{
		return new Item(this.defnName, this.quantity);
	};
}
