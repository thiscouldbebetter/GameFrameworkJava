
function Place(entities)
{
	this.entities = [];
	this.entitiesByPropertyName = [];
	this.entitiesToSpawn = entities.slice();
	this.entitiesToRemove = [];
}
{
	Place.prototype.draw = function(universe, world)
	{
		var entitiesDrawable = this.entitiesByPropertyName["drawable"];
		for (var i = 0; i < entitiesDrawable.length; i++)
		{
			var entity = entitiesDrawable[i];
			var drawable = entity.drawable;
			drawable.updateForTimerTick(universe, world, this, entity);
		}
	}
	
	Place.prototype.entitiesRemove = function()
	{
		for (var i = 0; i < this.entitiesToRemove.length; i++)
		{
			var entity = this.entitiesToRemove[i];
			this.entities.remove(entity);
			delete this.entities[entity.name];
		}
		this.entitiesToRemove.length = 0;
	}
	
	Place.prototype.entitiesSpawn = function(universe, world)
	{
		for (var i = 0; i < this.entitiesToSpawn.length; i++)
		{
			var entity = this.entitiesToSpawn[i];
			this.entities.push(entity);
			this.entities[entity.name] = entity;
			
			var entityProperties = entity.properties;
			for (var p = 0; p < entityProperties.length; p++)
			{
				var property = entityProperties[p];
				var propertyName = property.constructor.name.toLowerCase();
				var entitiesWithProperty = this.entitiesByPropertyName[propertyName];
				if (entitiesWithProperty == null)
				{
					entitiesWithProperty = [];
					this.entitiesByPropertyName[propertyName] = entitiesWithProperty;
				}
				entitiesWithProperty.push(entity);
			}
		}
		
		this.entitiesToSpawn.length = 0;
	}
	
	Place.prototype.updateForTimerTick = function(universe, world)
	{
		this.entitiesSpawn();
		
		var propertyNamesToProcess = 
		[
			"locatable",
			"collidable",
			"actor",
			"playable"
		];

		for (var p = 0; p < propertyNamesToProcess.length; p++)
		{
			var propertyName = propertyNamesToProcess[p];
			var entitiesWithProperty = this.entitiesByPropertyName[propertyName];
			for (var i = 0; i < entitiesWithProperty.length; i++)
			{
				var entity = entitiesWithProperty[i];
				var entityProperty = entity[propertyName];
				entityProperty.updateForTimerTick(universe, world, this, entity);
			}
		}

		this.entitiesRemove();
	}
}