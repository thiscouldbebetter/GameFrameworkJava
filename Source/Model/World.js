// This class, as implemented, is only a demonstration.
// Its code is expected to be modified heavily in actual applications,
// including the constructor, the draw() and update() methods,
// and the World.new() method.

function World(name, dateCreated, defns, places)
{
	this.name = name;
	this.dateCreated = dateCreated;

	this.timerTicksSoFar = 0;

	this.defns = defns;

	this.places = places.addLookupsByName();
	this.placeCurrent = this.places[0];
}

{
	// static methods

	World.new = function(universe)
	{
		var now = DateTime.now();
		var nowAsString = now.toStringMMDD_HHMM_SS();

		// PlaceDefns.

		var coordsInstances = Coords.Instances();

		var actionsAll = Action.Instances();

		var entityAccelerateInDirection = function
		(
			universe, world, place, entity, directionToMove
		)
		{
			entity.locatable.loc.orientation.forwardSet(directionToMove);
			entity.movable.accelerate(universe, world, place, entity);
		};

		var actions =
		[
			actionsAll.DoNothing,
			actionsAll.ShowEquipment,
			actionsAll.ShowItems,
			actionsAll.ShowMenu,
			new Action
			(
				"MoveDown",
				function perform(universe, world, place, actor)
				{
					entityAccelerateInDirection
					(
						universe, world, place, actor, coordsInstances.ZeroOneZero
					);
				}
			),
			new Action
			(
				"MoveLeft",
				function perform(universe, world, place, actor)
				{
					entityAccelerateInDirection
					(
						universe, world, place, actor, coordsInstances.MinusOneZeroZero
					);
				}
			),
			new Action
			(
				"MoveRight",
				function perform(universe, world, place, actor)
				{
					entityAccelerateInDirection
					(
						universe, world, place, actor, coordsInstances.OneZeroZero
					);
				}
			),
			new Action
			(
				"MoveUp",
				function perform(universe, world, place, actor)
				{
					entityAccelerateInDirection
					(
						universe, world, place, actor, coordsInstances.ZeroMinusOneZero
					);
				}
			),
			new Action
			(
				"Fire",
				function perform(universe, world, place, actor)
				{
					var equipmentUser = actor.equipmentUser;
					var entityWeaponEquipped = equipmentUser.itemEntityInSocketWithName("Weapon");
					var actorHasWeaponEquipped = (entityWeaponEquipped != null);

					if (actorHasWeaponEquipped)
					{
						var deviceWeapon = entityWeaponEquipped.device;
						deviceWeapon.use(universe, world, place, actor, entityWeaponEquipped);
					}
				}
			),
		];

		var inputNames = Input.Names();

		var actionToInputsMappings =
		[
			new ActionToInputsMapping("ShowMenu", [ inputNames.Escape ]),
			new ActionToInputsMapping("ShowItems", [ inputNames.Tab ]),
			new ActionToInputsMapping("ShowEquipment", [ "`" ]),

			new ActionToInputsMapping("MoveDown", 	[ inputNames.ArrowDown, inputNames.GamepadMoveDown + "0" ]),
			new ActionToInputsMapping("MoveLeft", 	[ inputNames.ArrowLeft, inputNames.GamepadMoveLeft + "0" ]),
			new ActionToInputsMapping("MoveRight", 	[ inputNames.ArrowRight, inputNames.GamepadMoveRight + "0" ]),
			new ActionToInputsMapping("MoveUp", 	[ inputNames.ArrowUp, inputNames.GamepadMoveUp + "0" ]),
			new ActionToInputsMapping("Fire", 		[ inputNames.Enter, inputNames.GamepadButton0 + "0" ]),
		];

		var placeDefnDemo = new PlaceDefn
		(
			"Demo",
			actions,
			actionToInputsMappings
		);

		var placeDefns = [ placeDefnDemo ]; // todo

		var itemUseEquip = function (universe, world, place, entityUser, entityItem, item)
		{
			var equipmentUser = entityUser.equipmentUser;
			var message = equipmentUser.equipEntityWithItem
			(
				universe, world, place, entityUser, entityItem, item
			);
			return message;
		};

		var itemDefns =
		[
			new ItemDefn("Ammo"),
			ItemDefn.fromNameCategoryNameAndUse
			(
				"Armor",
				"Armor", // categoryName
				itemUseEquip
			),
			new ItemDefn("Coin"),
			new ItemDefn("Key"),
			ItemDefn.fromNameAndUse
			(
				"Medicine",
				function use(universe, world, place, entityUser, entityItem, item)
				{
					var integrityToRestore = 10;
					entityUser.killable.integrityAdd(integrityToRestore);
					entityUser.itemHolder.itemSubtractDefnNameAndQuantity(item.defnName, 1);
					var message = "The medicine restores " + integrityToRestore + " points.";
					return message;
				}
			),
			ItemDefn.fromNameCategoryNameAndUse
			(
				"Speed Booster",
				"Accessory", // categoryName
				itemUseEquip
			),
			ItemDefn.fromNameCategoryNameAndUse
			(
				"Weapon",
				"Weapon", // categoryName
				itemUseEquip
			)
		];

		var defns = new Defns(itemDefns, placeDefns);

		var displaySize = universe.display.sizeInPixels;
		var cameraViewSize = displaySize.clone();
		var placeBuilder = new PlaceBuilderDemo();

		var randomizer = null; // Use default.

		var placeMain = placeBuilder.build
		(
			"Battlefield",
			displaySize.clone().double(), // size
			cameraViewSize,
			null, // placeNameToReturnTo
			randomizer,
			itemDefns
		);

		var placeBase = placeBuilder.build
		(
			"Base",
			displaySize.clone(), // size
			cameraViewSize,
			placeMain.name, // placeNameToReturnTo
			randomizer,
			itemDefns
		);

		var places = [ placeMain, placeBase ];

		var returnValue = new World
		(
			"World-" + nowAsString,
			now, // dateCreated
			defns,
			places
		);
		return returnValue;
	};

	// instance methods

	World.prototype.draw = function(universe)
	{
		this.placeCurrent.draw(universe, this);
	};

	World.prototype.initialize = function(universe)
	{
		this.placeCurrent.initialize(universe, this);
	};

	World.prototype.updateForTimerTick = function(universe)
	{
		this.placeCurrent.updateForTimerTick(universe, this);
		this.timerTicksSoFar++;
	};
}
