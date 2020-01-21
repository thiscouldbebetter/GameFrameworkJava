
function Equippable(socketDefnGroup)
{
	this.socketGroup = new EquipmentSocketGroup(socketDefnGroup);
}

{
	Equippable.prototype.equipEntityWithItem = function
	(
		universe, world, place, entityEquippable, itemEntityToEquip
	)
	{
		var sockets = this.socketGroup.sockets;
		var socketDefnGroup = this.socketGroup.defnGroup;
		var itemToEquip = itemEntityToEquip.Item;
		var itemDefn = itemToEquip.defn(world);

		var socketFound = sockets.filter
		(
			function(socket)
			{
				var socketDefn = socket.defn(socketDefnGroup);
				var isItemAllowedInSocket = socketDefn.categoriesAllowedNames.some
				(
					y => itemDefn.categoryNames.contains(y)
				);
				return isItemAllowedInSocket;
			}
		)[0];

		var message = itemDefn.appearance;

		if (socketFound == null)
		{
			message += " cannot be equipped."
		}
		else if (socketFound.itemEntityEquipped == itemEntityToEquip)
		{
			socketFound.itemEntityEquipped = null;
			message += " unequipped."
		}
		else
		{
			socketFound.itemEntityEquipped = itemEntityToEquip;
			message += " equipped."
		}

		return message;
	};

	Equippable.prototype.unequipItemFromSocket = function
	(
		universe, world, place, entityEquippable, socketToUnequipFrom
	)
	{
		var message;
		if (socketToUnequipFrom == null)
		{
			message = "Nothing to unequip!";
		}
		else
		{
			var itemEntityToUnequip = socketToUnequipFrom.itemEntityEquipped;
			if (itemEntityToUnequip == null)
			{
				message = "Nothing to unequip!";
			}
			else
			{
				socketToUnequipFrom.itemEntityEquipped = null;
				var itemToUnequip = itemEntityToUnequip.Item;
				var itemDefn = itemToUnequip.defn(world);
				message = itemDefn.appearance + " unequipped."
			}
		}
		return message;
	};

	// control

	Equippable.prototype.toControl = function(universe, size, entityEquippable, venuePrev)
	{
		this.statusMessage = "-";

		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var sizeBase = new Coords(200, 150, 1);
		var scaleMultiplier = size.clone().divide(sizeBase);

		var fontHeight = 10;
		var fontHeightSmall = fontHeight * .6;
		var fontHeightLarge = fontHeight * 1.5;

		var itemHolder = entityEquippable.ItemHolder;
		var equippable = this;
		var sockets = this.socketGroup.sockets;
		var socketDefnGroup = this.socketGroup.defnGroup;

		var itemCategoriesForAllSockets = [];
		for (var i = 0; i < sockets.length; i++)
		{
			var socket = sockets[i];
			var socketDefn = socket.defn(socketDefnGroup);
			var socketCategoryNames = socketDefn.categoriesAllowedNames;
			for (var j = 0; j < socketCategoryNames.length; j++)
			{
				var categoryName = socketCategoryNames[j];
				if (itemCategoriesForAllSockets.contains(categoryName) == false)
				{
					itemCategoriesForAllSockets.push(categoryName);
				}
			}
		}
		var itemEntitiesEquippable = itemHolder.itemEntities;
		// todo

		var world = universe.world;
		var place = world.placeCurrent;

		var listEquippables = new ControlList
		(
			"listEquippables",
			new Coords(10, 30), // pos
			new Coords(70, 80), // size
			new DataBinding(itemEntitiesEquippable), // items
			new DataBinding
			(
				null,
				function get(c) { return c.Item.toString(world); }
			), // bindingForItemText
			fontHeightSmall,
			new DataBinding
			(
				this,
				function get(c) { return c.itemEntitySelected; },
				function set(c, v) { c.itemEntitySelected = v; }
			), // bindingForItemSelected
			new DataBinding(null, function(c) { return c; } ), // bindingForItemValue
			null, // bindingForIsEnabled
			function confirm()
			{
				var itemEntityToEquip = equippable.itemEntitySelected;
				var itemToEquip = itemEntityToEquip.Item;
				var itemToEquipName = itemToEquip.appearance;

				var message = equippable.equipEntityWithItem
				(
					universe, world, place, entityEquippable, itemEntityToEquip
				);
				equippable.statusMessage = message;
			}
		);

		var listEquipped = new ControlList
		(
			"listEquipped",
			new Coords(90, 30), // pos
			new Coords(100, 80), // size
			new DataBinding(sockets), // items
			new DataBinding
			(
				null,
				function get(c) { return c.toString(world); }
			), // bindingForItemText
			fontHeightSmall,
			new DataBinding
			(
				this,
				function get(c) { return c.socketSelected; },
				function set(c, v) { c.socketSelected = v; }
			), // bindingForItemSelected
			new DataBinding(null, function(c) { return c; } ), // bindingForItemValue
			null, // bindingForIsEnabled
			function confirm()
			{
				var socketToUnequipFrom = equippable.socketSelected;

				var message = equippable.unequipItemFromSocket
				(
					universe, world, place, entityEquippable, socketToUnequipFrom
				);
				equippable.statusMessage = message;
			}
		);

		var returnValue = new ControlContainer
		(
			"containerItems",
			Coords.Instances().Zeroes, // pos
			sizeBase.clone(), // size
			// children
			[
				new ControlLabel
				(
					"labelEquipment",
					new Coords(100, 10), // pos
					new Coords(100, 25), // size
					true, // isTextCentered
					"Equipment",
					fontHeightLarge
				),

				new ControlLabel
				(
					"labelEquippable",
					new Coords(10, 20), // pos
					new Coords(70, 25), // size
					false, // isTextCentered
					"Equippable:",
					fontHeightSmall
				),

				listEquippables,

				new ControlLabel
				(
					"labelEquipped",
					new Coords(90, 20), // pos
					new Coords(100, 25), // size
					false, // isTextCentered
					"Equipped:",
					fontHeightSmall
				),

				listEquipped,

				new ControlLabel
				(
					"infoStatus",
					new Coords(100, 120), // pos
					new Coords(200, 15), // size
					true, // isTextCentered
					new DataBinding
					(
						this,
						function get(c)
						{
							return c.statusMessage;
						}
					), // text
					fontHeight
				),

				new ControlButton
				(
					"buttonDone",
					new Coords(75, 130), // pos
					new Coords(50, 15), // size
					"Done",
					fontHeight,
					true, // hasBorder
					true, // isEnabled
					function click(universe)
					{
						var venueNext = venuePrev;
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					},
					universe // context
				)
			]
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};
}
