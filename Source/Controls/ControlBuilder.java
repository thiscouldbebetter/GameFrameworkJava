package Controls;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import Display.*;
import Display.Visuals.*;
import Input.*;
import Geometry.*;
import Media.*;
import Model.*;
import Profiles.*;
import Utility.*;

public class ControlBuilder
{
	public Map<String,ControlStyle> stylesByName;

	private int fontHeightInPixelsBase;
	private Coords sizeBase;

	// Helper variables.
	private Coords _zeroes = new Coords(0, 0, 0);
	private Coords _scaleMultiplier = new Coords();

	public ControlBuilder(ControlStyle[] styles)
	{
		this.stylesByName = ArrayHelper.addLookupsByName(styles);

		this.fontHeightInPixelsBase = 10;
		this.sizeBase = new Coords(200, 150, 1);
	}

	public Control choice
	(
		Universe universe, Coords size, String message,
		String[] optionNames, Runnable[] optionFunctions, boolean showMessageOnly
	)
	{
		size = (size == null ? universe.display.sizeDefault() : size);

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);
		var fontHeight = this.fontHeightInPixelsBase;

		var numberOfLinesInMessageMinusOne = message.split("\n").length - 1;
		var labelSize = new Coords
		(
			200, fontHeight * numberOfLinesInMessageMinusOne
		);

		var numberOfOptions = optionNames.length;

		if (showMessageOnly && numberOfOptions == 1)
		{
			numberOfOptions = 0; // Is a single option really an option?
		}

		var labelPosYBase = (numberOfOptions > 0 ? 65 : 75); // hack

		var labelPos = new Coords
		(
			100, labelPosYBase - fontHeight * (numberOfLinesInMessageMinusOne / 4)
		);

		var labelMessage = new ControlLabel
		(
			"labelMessage",
			labelPos,
			labelSize,
			true, // isTextCentered
			message,
			fontHeight
		);

		var childControls = new ArrayList<Control>( Arrays.asList( labelMessage ) );

		if (showMessageOnly == false)
		{
			var buttonWidth = 55;
			var buttonSize = new Coords(buttonWidth, fontHeight * 2);
			var spaceBetweenButtons = 5;
			var buttonMarginLeftRight =
				(
					this.sizeBase.x
					- (buttonWidth * numberOfOptions)
					- (spaceBetweenButtons * (numberOfOptions - 1))
				) / 2;

			for (var i = 0; i < numberOfOptions; i++)
			{
				var button = new ControlButton
				(
					"buttonOption" + i,
					new Coords
					(
						buttonMarginLeftRight + i * (buttonWidth + spaceBetweenButtons),
						100
					), // pos
					buttonSize.clonify(),
					optionNames[i],
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					optionFunctions[i]
				);

				childControls.add(button);
			}
		}

		var containerSizeScaled = size.clonify().clearZ().divide(scaleMultiplier);
		var display = universe.display;
		var displaySize = display.sizeDefault().clonify().clearZ().divide(scaleMultiplier);
		var containerPosScaled = displaySize.clonify().subtract(containerSizeScaled).half();
		Action[] actions = null;
		if (numberOfOptions <= 1)
		{
			var acknowledge = optionFunctions[0];
			actions = new Action[]
			{
				new Action( ControlActionNames.ControlCancel, (UniverseWorldPlaceEntities uwpe) -> acknowledge.run() ),
				new Action( ControlActionNames.ControlConfirm, (UniverseWorldPlaceEntities uwpe) -> acknowledge.run() ),
			};
		}

		var controlContainer = new ControlContainer
		(
			"containerChoice",
			containerPosScaled,
			containerSizeScaled,
			childControls.toArray(new Control[0]),
			actions
		);

		Control returnValue = controlContainer;

		returnValue.scalePosAndSize(scaleMultiplier);

		if (showMessageOnly)
		{
			returnValue = new ControlContainerTransparent(controlContainer);
		}

		return returnValue;
	};

	public Control choiceList
	(
		Universe universe, Coords size, String message, 
		DataBinding options, DataBinding bindingForOptionText, String buttonSelectText,
		BiConsumer<Universe,Object> select
	)
	{
		// todo - Variable sizes.

		var marginWidth = 10;
		var marginSize = new Coords(1, 1).multiplyScalar(marginWidth);
		var fontHeight = 20;
		var labelSize = new Coords(size.x - marginSize.x * 2, fontHeight);
		var buttonSize = new Coords(labelSize.x, fontHeight * 2);
		var listSize = new Coords
		(
			labelSize.x,
			size.y - labelSize.y - buttonSize.y - marginSize.y * 4
		);

		var listOptions = new ControlList
		(
			"listOptions",
			new Coords(marginSize.x, labelSize.y + marginSize.y * 2),
			listSize,
			options,
			bindingForOptionText,
			fontHeight,
			null, // bindingForItemSelected
			null // bindingForItemValue
		);

		var returnValue = new ControlContainer
		(
			"containerChoice",
			new Coords(0, 0),
			size,
			new Control[]
			{
				new ControlLabel
				(
					"labelMessage",
					new Coords(size.x / 2, marginSize.y + fontHeight / 2),
					labelSize,
					true, // isTextCentered
					message,
					fontHeight
				),

				listOptions,

				new ControlButton
				(
					"buttonSelect",
					new Coords(marginSize.x, size.y - marginSize.y - buttonSize.y),
					buttonSize,
					buttonSelectText,
					fontHeight,
					true, // hasBorder
					new DataBinding(new Boolean(true)), // isEnabled,
					() ->
					{
						var itemSelected = listOptions.itemSelected();
						if (itemSelected != null)
						{
							select.accept(universe, itemSelected);
						}
					},
					false // canBeHeldDown
				),
			}
		);

		return returnValue;
	};

	public Control confirm
	(
		Universe universe, Coords size, String message,
		Runnable confirm,
		Runnable cancel
	)
	{
		return this.choice
		(
			universe, size, message,
			new String[] {"Confirm", "Cancel"},
			new Runnable[] {confirm, cancel}, false
		);
	};

	public Control game(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault().clonify();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var buttonHeight = 20;
		var buttonSize = new Coords(60, buttonHeight);
		var margin = 15;
		var padding = 5;
		var labelPadding = 3;

		var posX = 70;
		var rowHeight = buttonHeight + padding;
		var row0PosY = margin;
		var row1PosY = row0PosY + rowHeight;
		var row2PosY = row1PosY + rowHeight;
		var row3PosY = row2PosY + rowHeight;
		var row4PosY = row3PosY + rowHeight;

		Runnable back = () ->
		{
			Venue venueNext = new VenueControls
			(
				universe.controlBuilder.gameAndSettings(universe, size)
			);
			venueNext = new VenueFader(venueNext, universe.venueCurrent);
			universe.venueNext = venueNext;
		};

		var returnValue = new ControlContainer
		(
			"containerStorage",
			this._zeroes, // pos
			this.sizeBase.clonify(),
			// children
			new Control[]
			{
				new ControlButton
				(
					"buttonSave",
					new Coords(posX, row0PosY), // pos
					buttonSize.clonify(),
					"Save",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					// click
					() ->
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.worldSave(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonLoad",
					new Coords(posX, row1PosY), // pos
					buttonSize.clonify(),
					"Load",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.worldLoad(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonAbout",
					new Coords(posX, row2PosY), // pos
					buttonSize.clonify(),
					"About",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						var venueCurrent = universe.venueCurrent;
						Runnable confirm = () ->
						{
							universe.venueNext = new VenueFader(venueCurrent);
						};

						Venue venueNext = new VenueMessage
						(
							universe.name + "\nv" + universe.version,
							confirm,
							universe.venueCurrent, // venuePrev
							size
						);
						venueNext = new VenueFader(venueNext, venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonQuit",
					new Coords(posX, row3PosY), // pos
					buttonSize.clonify(),
					"Quit",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						var controlConfirm = universe.controlBuilder.confirm
						(
							universe,
							size,
							"Are you sure you want to quit?",
							() -> // confirm
							{
								universe.reset();
								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.title(universe)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							},
							() -> // cancel
							{
								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.gameAndSettings(universe, size)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							}
						);

						Venue venueNext = new VenueControls(controlConfirm);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonBack",
					new Coords(posX, row4PosY), // pos
					buttonSize.clonify(),
					"Back",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					back,
					false // canBeHeldDown
				),
			},

			new Action[] { new Action("Back", (UniverseWorldPlaceEntities uwpe) -> back.run() ) },

			new ActionToInputsMapping[]
			{
				new ActionToInputsMapping
				(
					"Back", new String[] { Input.Instances().Escape.name }, true
				)
			}

		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control gameAndSettings(Universe universe)
	{
		return this.gameAndSettings(universe, null);
	}

	public Control gameAndSettings(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault().clonify();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var buttonHeight = 20;
		var margin = 15;
		var padding = 5;
		var labelPadding = 3;

		var rowHeight = buttonHeight + padding;
		var row0PosY = margin;
		var row1PosY = row0PosY + rowHeight;
		var row2PosY = row1PosY + rowHeight;
		var row3PosY = row2PosY + rowHeight;
		var row4PosY = row3PosY + rowHeight;

		Runnable back = () ->
		{
			Venue venueNext = new VenueWorld(universe.world);
			venueNext = new VenueFader(venueNext, universe.venueCurrent);
			universe.venueNext = venueNext;
		};

		var returnValue = new ControlContainer
		(
			"containerGameAndSettings",
			this._zeroes, // pos
			this.sizeBase.clonify(),
			// children
			new Control[]
			{
				new ControlButton
				(
					"buttonGame",
					new Coords(70, row1PosY), // pos
					new Coords(60, buttonHeight), // size
					"Game",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.game(universe, size)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonSettings",
					new Coords(70, row2PosY), // pos
					new Coords(60, buttonHeight), // size
					"Settings",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.settings(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonResume",
					new Coords(70, row3PosY), // pos
					new Coords(60, buttonHeight), // size
					"Resume",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					back
				)
			},

			new Action[] { new Action("Back", back) },

			new ActionToInputsMapping[]
			{
				new ActionToInputsMapping( "Back", new String[] { Input.Instances().Escape.name }, true )
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control inputs(Universe universe, Coords size, Venue venuePrev)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var profiles = universe.profileHelper.profiles();

		var world = universe.world;
		var placeCurrentDefnName = "Demo"; // hack
		var placeDefn = (PlaceDefn)(world.defns.defnByTypeNameAndDefnName(PlaceDefn.class.getName(), placeCurrentDefnName));
		placeDefn.actionToInputsMappingsEdit();

		var returnValue = new ControlContainer
		(
			"containerGameControls",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlLabel
				(
					"labelActions",
					new Coords(100, 15), // pos
					new Coords(100, 25), // size
					true, // isTextCentered
					"Actions:",
					fontHeight
				),

				new ControlList
				(
					"listActions",
					new Coords(50, 25), // pos
					new Coords(100, 40), // size
					new DataBinding(placeDefn.actionToInputsMappingsEdited), // items
					new DataBinding<ActionToInputsMapping,String>
					(
						null, (ActionToInputsMapping c) -> { return c.actionName; }
					), // bindingForItemText
					fontHeight,
					new DataBinding<PlaceDefn,ActionToInputsMapping>
					(
						placeDefn,
						(PlaceDefn c) -> { return c.actionToInputsMappingSelected; },
						(PlaceDefn c, ActionToInputsMapping v) -> { c.actionToInputsMappingSelected = v; }
					), // bindingForItemSelected
					new DataBinding<ActionToInputsMapping,ActionToInputsMapping>
					(
						null, (ActionToInputsMapping c) -> { return c; }
					) // bindingForItemValue
				),

				new ControlLabel
				(
					"labelInput",
					new Coords(100, 70), // pos
					new Coords(100, 15), // size
					true, // isTextCentered
					"Inputs:",
					fontHeight
				),

				new ControlLabel
				(
					"infoInput",
					new Coords(100, 80), // pos
					new Coords(200, 15), // size
					true, // isTextCentered
					new DataBinding
					(
						placeDefn,
						(PlaceDefn c) ->
						{
							var i = c.actionToInputsMappingSelected;
							return (i == null ? "-" : String.join(",", i.inputNames) );
						}
					), // text
					fontHeight
				),

				new ControlButton
				(
					"buttonClear",
					new Coords(25, 90), // pos
					new Coords(45, 15), // size
					"Clear",
					fontHeight,
					true, // hasBorder
					new DataBinding<PlaceDefn,Boolean>
					(
						placeDefn,
						(PlaceDefn c) -> { return c.actionToInputsMappingSelected != null; }
					), // isEnabled
					() ->
					{
						var mappingSelected = placeDefn.actionToInputsMappingSelected;
						if (mappingSelected != null)
						{
							mappingSelected.inputNames = new ArrayList<String>();
						}
					}
				),

				new ControlButton
				(
					"buttonAdd",
					new Coords(80, 90), // pos
					new Coords(45, 15), // size
					"Add",
					fontHeight,
					true, // hasBorder
					new DataBinding<PlaceDefn,Boolean>
					(
						placeDefn,
						(PlaceDefn c) -> { return c.actionToInputsMappingSelected != null; }
					), // isEnabled
					() ->
					{
						var mappingSelected = placeDefn.actionToInputsMappingSelected;
						if (mappingSelected != null)
						{
							var venueInputCapture = new VenueInputCapture
							(
								universe.venueCurrent,
								(Input inputCaptured) ->
								{
									var inputName = inputCaptured.name;
									mappingSelected.inputNames.add(inputName);
								}
							);
							universe.venueNext = venueInputCapture;
						}
					}
				),

				new ControlButton
				(
					"buttonRestoreDefault",
					new Coords(135, 90), // pos
					new Coords(45, 15), // size
					"Default",
					fontHeight,
					true, // hasBorder
					new DataBinding<PlaceDefn,Boolean>
					(
						placeDefn,
						(PlaceDefn c) -> { return c.actionToInputsMappingSelected != null; }
					), // isEnabled
					() ->
					{
						var mappingSelected = placeDefn.actionToInputsMappingSelected;
						if (mappingSelected != null)
						{
							var mappingDefault = Arrays.stream(placeDefn.actionToInputsMappingsDefault).filter
							(
								(ActionToInputsMapping x) -> { return x.actionName == mappingSelected.actionName; }
							).findFirst();
							mappingSelected.inputNames = new ArrayList<String>
							(
								Arrays.asList(mappingDefault.get().inputNames)
							);
						}
					}
				),

				new ControlButton
				(
					"buttonRestoreDefaultsAll",
					new Coords(50, 110), // pos
					new Coords(100, 15), // size
					"Default All",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						var venueInputs = universe.venueCurrent;
						var controlConfirm = universe.controlBuilder.confirm
						(
							universe,
							size,
							"Are you sure you want to restore defaults?",
							() -> // confirm
							{
								placeDefn.actionToInputsMappingsRestoreDefaults();
								Venue venueNext = venueInputs;
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							},
							() -> // cancel
							{
								Venue venueNext = venueInputs;
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							}
						);
						Venue venueNext = new VenueControls(controlConfirm);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonCancel",
					new Coords(50, 130), // pos
					new Coords(45, 15), // size
					"Cancel",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						Venue venueNext = venuePrev;
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonSave",
					new Coords(105, 130), // pos
					new Coords(45, 15), // size
					"Save",
					fontHeight,
					true, // hasBorder
					// isEnabled
					new DataBinding<PlaceDefn,Boolean>
					(
						placeDefn,
						(PlaceDefn c) ->
						{
							var mappings = c.actionToInputsMappingsEdited;
							var doAnyActionsLackInputs = Arrays.stream(mappings).anyMatch
							(
								(ActionToInputsMapping x) -> { return x.inputNames.size() == 0; }
							);
							return (doAnyActionsLackInputs == false);
						}
					),
					() -> // click
					{
						placeDefn.actionToInputsMappingsSave();
						Venue venueNext = venuePrev;
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				)
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control message
	(
		Universe universe, Coords size, String message,
		Runnable acknowledge, boolean showMessageOnly
	)
	{
		String[] optionNames;
		Runnable[] optionFunctions;

		if (acknowledge != null)
		{
			optionNames = new String[] { "Acknowledge" };
			optionFunctions = new Runnable[] { acknowledge };
		}

		return this.choice
		(
			universe, size, message, optionNames, optionFunctions, showMessageOnly
		);
	}

	public Control profileDetail(Universe universe)
	{
		return this.profileDetail(universe, null);
	}

	public Control profileDetail(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var listWorlds = new ControlList
		(
			"listWorlds",
			new Coords(25, 50), // pos
			new Coords(150, 50), // size
			new DataBinding<List<World>,List<World>>
			(
				universe.profileSelected.worlds,
				(List<World> c) -> { return c; }
			),
			new DataBinding<World,String>
			(
				null, (World c) -> { return c.name; }
			), // bindingForOptionText
			fontHeight,
			new DataBinding<Universe,World>
			(
				universe,
				(Universe c) -> { return c.world; }, // get
				(Universe c, World v) -> { c.world = v; } // set
			), // bindingForOptionSelected
			new DataBinding<World,World>
			(
				null, (World c) -> { return c; }
			) // value
		);

		var returnValue = new ControlContainer
		(
			"containerProfileDetail",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlLabel
				(
					"labelProfileName",
					new Coords(100, 25), // pos
					new Coords(120, 25), // size
					true, // isTextCentered
					"Profile: " + universe.profileSelected.name,
					fontHeight
				),

				new ControlLabel
				(
					"labelSelectAWorld",
					new Coords(100, 40), // pos
					new Coords(100, 25), // size
					true, // isTextCentered
					"Select a World:",
					fontHeight
				),

				listWorlds,

				new ControlButton
				(
					"buttonNew",
					new Coords(50, 110), // pos
					new Coords(45, 25), // size
					"New",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						var world = World.create(universe);

						var profile = universe.profileSelected;
						profile.worlds.add(world);

						universe.profileHelper.profileSave
						(
							profile
						);

						universe.world = world;
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.worldDetail(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonSelect",
					new Coords(105, 110), // pos
					new Coords(45, 25), // size
					"Select",
					fontHeight,
					true, // hasBorder
					// isEnabled
					new DataBinding<Universe,Boolean>
					(
						universe,
						(Universe c) -> { return (c.world != null); } // get
					),
					() -> // click
					{
						var worldSelected = listWorlds.itemSelected(); // hack
						if (worldSelected != null)
						{
							universe.world = (World)(worldSelected);
							Venue venueNext = new VenueControls
							(
								universe.controlBuilder.worldDetail(universe)
							);
							venueNext = new VenueFader(venueNext, universe.venueCurrent);
							universe.venueNext = venueNext;
						}
					}
				),

				new ControlButton
				(
					"buttonBack",
					new Coords(10, 10), // pos
					new Coords(15, 15), // size
					"<",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.profileSelect(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonDelete",
					new Coords(180, 10), // pos
					new Coords(15, 15), // size
					"x",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						var profile = universe.profileSelected;

						var controlConfirm = universe.controlBuilder.confirm
						(
							universe,
							size,
							"Delete profile \""
								+ profile.name
								+ "\"?",
							() -> // confirm
							{
								universe.profileHelper.profileDelete
								(
									profile
								);

								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.profileSelect(universe)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							},
							() -> // cancel
							{
								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.profileDetail(universe)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							}
						);

						Venue venueNext = new VenueControls(controlConfirm);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control profileNew(Universe universe)
	{
		return this.profileNew(universe, null);
	}

	public Control profileNew(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var textBoxName = new ControlTextBox
		(
			"textBoxName",
			new Coords(50, 50), // pos
			new Coords(100, 25), // size
			new DataBinding<Profile,String>
			(
				universe.profileSelected,
				(Profile c) -> { return c.name; },
				(Profile c, String v) -> { c.name = v; }
			), // text
			fontHeight,
			0 // charsMax
		);

		var returnValue = new ControlContainer
		(
			"containerProfileNew",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlLabel
				(
					"labelName",
					new Coords(100, 40), // pos
					new Coords(100, 25), // size
					true, // isTextCentered
					"Profile Name:",
					fontHeight
				),

				textBoxName, // hack

				new ControlButton
				(
					"buttonCreate",
					new Coords(50, 80), // pos
					new Coords(45, 25), // size
					"Create",
					fontHeight,
					true, // hasBorder
					// isEnabled
					new DataBinding<Universe,Boolean>
					(
						universe,
						(Universe c) -> { return c.profileSelected.name.length() > 0; }
					),
					() ->
					{
						var profileName = textBoxName.text();
						if (profileName == "")
						{
							return;
						}

						var profile = new Profile(profileName, new ArrayList<World>() );
						universe.profileHelper.profileAdd
						(
							profile
						);

						universe.profileSelected = profile;
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.profileDetail(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonCancel",
					new Coords(105, 80), // pos
					new Coords(45, 25), // size
					"Cancel",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.profileSelect(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control profileSelect(Universe universe)
	{
		return this.profileSelect(universe, null);
	}

	public Control profileSelect(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var profiles = universe.profileHelper.profiles();

		var listProfiles = new ControlList
		(
			"listProfiles",
			new Coords(35, 50), // pos
			new Coords(130, 40), // size
			new DataBinding<List<Profile>,List<Profile>>
			(
				profiles, (List<Profile> c) -> { return c; }
			), // items
			new DataBinding<Profile,String>(null, (Profile c) -> { return c.name; } ), // bindingForItemText
			fontHeight,
			new DataBinding<Universe,Profile>
			(
				universe,
				(Universe c) -> { return c.profileSelected; },
				(Universe c, Profile v) -> { c.profileSelected = v; }
			), // bindingForOptionSelected
			new DataBinding<Profile,Profile>(null, (Profile c) -> { return c; }) // value
		);

		var returnValue = new ControlContainer
		(
			"containerProfileSelect",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlLabel
				(
					"labelSelectAProfile",
					new Coords(100, 40), // pos
					new Coords(100, 25), // size
					true, // isTextCentered
					"Select a Profile:",
					fontHeight
				),

				listProfiles,

				new ControlButton
				(
					"buttonNew",
					new Coords(35, 95), // pos
					new Coords(40, 25), // size
					"New",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						universe.profileSelected = new Profile("");
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.profileNew(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonSelect",
					new Coords(80, 95), // pos
					new Coords(40, 25), // size
					"Select",
					fontHeight,
					true, // hasBorder
					// isEnabled
					new DataBinding<Universe,Boolean>
					(
						universe,
						(Universe c) -> { return (c.profileSelected != null); }
					),
					() ->
					{
						var profileSelected = (Profile)(listProfiles.itemSelected());
						universe.profileSelected = profileSelected;
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.profileDetail(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonSkip",
					new Coords(125, 95), // pos
					new Coords(40, 25), // size
					"Skip",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						Venue venueNext = new VenueMessage("Working...");

						venueNext = new VenueTask
						(
							venueNext,
							() -> 
							{
								return World.create(universe);
							},
							(Universe universe2, World world) -> 
							{
								universe2.world = world;

								var now = DateTime.now();
								var nowAsString = now.toStringMMDD_HHMM_SS();
								var profileName = "Anon-" + nowAsString;
								var profile = new Profile(profileName, new ArrayList<World>( Arrays.asList(world) ) );
								universe2.profileSelected = profile;

								Venue venueNext2 = new VenueWorld(universe2.world);
								venueNext2 = new VenueFader(venueNext, universe2.venueCurrent);
								universe.venueNext = venueNext2;
							}
						);

						venueNext = new VenueFader(venueNext, universe.venueCurrent);

						universe.venueNext = venueNext;

					} // end click
				),

				new ControlButton
				(
					"buttonBack",
					new Coords(10, 10), // pos
					new Coords(15, 15), // size
					"<",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.title(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonDelete",
					new Coords(180, 10), // pos
					new Coords(15, 15), // size
					"x",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						var profile = universe.profileSelected;

						var controlConfirm = universe.controlBuilder.confirm
						(
							universe,
							size,
							"Delete all profiles?",
							() -> // confirm
							{
								universe.profileHelper.profilesAllDelete();
								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.profileSelect(universe)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							},
							() -> // cancel
							{
								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.profileSelect(universe)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							}
						);

						Venue venueNext = new VenueControls(controlConfirm);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control settings(Universe universe)
	{
		return this.settings(universe, null);
	}

	public Control settings(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var buttonHeight = 20;
		var margin = 15;
		var padding = 5;
		var labelPadding = 3;

		var rowHeight = buttonHeight + padding;
		var row0PosY = margin;
		var row1PosY = row0PosY + rowHeight / 2;
		var row2PosY = row1PosY + rowHeight;
		var row3PosY = row2PosY + rowHeight;
		var row4PosY = row3PosY + rowHeight;

		Runnable back = () ->
		{
			var control = universe.controlBuilder.gameAndSettings(universe, size);
			Venue venueNext = new VenueControls(control);
			venueNext = new VenueFader(venueNext, universe.venueCurrent);
			universe.venueNext = venueNext;
		};

		var selectDisplaySize = new ControlSelect
		(
			"selectDisplaySize",
			new Coords(70, row2PosY), // pos
			new Coords(60, buttonHeight), // size
			universe.display.sizeInPixels, // valueSelected
			// options
			universe.display.sizesAvailable,
			new DataBinding<Coords,Coords>(null, (Coords c) -> { return c; } ), // bindingForOptionValues,
			new DataBinding<Coords,String>(null, (Coords c) -> { return c.toStringXY(); } ), // bindingForOptionText
			fontHeight
		);

		var returnValue = new ControlContainer
		(
			"containerSettings",
			this._zeroes, // pos
			this.sizeBase.clonify(),
			// children
			new Control[]
			{
				new ControlLabel
				(
					"labelMusicVolume",
					new Coords(30, row1PosY + labelPadding), // pos
					new Coords(75, buttonHeight), // size
					false, // isTextCentered
					"Music:",
					fontHeight
				),

				new ControlSelect
				(
					"selectMusicVolume",
					new Coords(65, row1PosY), // pos
					new Coords(30, buttonHeight), // size
					new DataBinding<SoundHelper,Double>
					(
						universe.soundHelper,
						(SoundHelper c) -> { return c.musicVolume; },
						(SoundHelper c, double v) -> { c.musicVolume = v; }
					), // valueSelected
					SoundHelper.controlSelectOptionsVolume(), // options
					new DataBinding<ControlSelectOption,Object>
					(
						null, (ControlSelectOption c) -> { return c.value; }
					), // bindingForOptionValues,
					new DataBinding<ControlSelectOption,String>
					(
						null, (ControlSelectOption c) -> { return c.text; }
					), // bindingForOptionText
					fontHeight
				),

				new ControlLabel
				(
					"labelSoundVolume",
					new Coords(105, row1PosY + labelPadding), // pos
					new Coords(75, buttonHeight), // size
					false, // isTextCentered
					"Sound:",
					fontHeight
				),

				new ControlSelect
				(
					"selectSoundVolume",
					new Coords(140, row1PosY), // pos
					new Coords(30, buttonHeight), // size
					new DataBinding<SoundHelper,Double>
					(
						universe.soundHelper,
						(SoundHelper c) -> { return c.soundVolume; },
						(SoundHelper c, double v) -> { c.soundVolume = v; }
					), // valueSelected
					SoundHelper.controlSelectOptionsVolume(), // options
					new DataBinding<ControlSelectOption,Object>(null, (ControlSelectOption c) -> { return c.value; } ), // bindingForOptionValues,
					new DataBinding<ControlSelectOption,String>(null, (ControlSelectOption c) -> { return c.text; } ), // bindingForOptionText
					fontHeight
				),

				new ControlLabel
				(
					"labelDisplaySize",
					new Coords(30, row2PosY + labelPadding), // pos
					new Coords(75, buttonHeight), // size
					false, // isTextCentered
					"Display:",
					fontHeight
				),

				selectDisplaySize, // hack

				new ControlButton
				(
					"buttonDisplaySizeChange",
					new Coords(140, row2PosY), // pos
					new Coords(30, buttonHeight), // size
					"Change",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						var venueCurrentAsVenueControls = (VenueControls)(universe.venueCurrent);
						var controlRoot = venueCurrentAsVenueControls.controlRoot;
						var displaySizeSpecified = selectDisplaySize.optionSelected();

						var display = universe.display;
						var platformHelper = universe.platformHelper;
						platformHelper.platformableRemove(display);
						// todo - Change size.
						platformHelper.initialize(universe);

						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.settings(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonInputs",
					new Coords(70, row3PosY), // pos
					new Coords(65, buttonHeight), // size
					"Inputs",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() ->
					{
						var venueCurrent = universe.venueCurrent;
						var controlGameControls =
							universe.controlBuilder.inputs(universe, size, venueCurrent);
						Venue venueNext = new VenueControls(controlGameControls);
						venueNext = new VenueFader(venueNext, venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonDone",
					new Coords(70, row4PosY), // pos
					new Coords(65, buttonHeight), // size
					"Done",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					back
				),
			},

			new Action[] { new Action("Back", back) },

			new ActionToInputsMapping[]
			{
				new ActionToInputsMapping( "Back", new String[] { Input.Instances().Escape.name }, true )
			}

		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control slideshow
	(
		Universe universe, Coords size, Object[][] imageNamesAndMessagesForSlides,
		Venue venueAfterSlideshow
	)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var controlsForSlides = new ArrayList<Control>();

		for (var i = 0; i < imageNamesAndMessagesForSlides.length; i++)
		{
			var imageNameAndMessage = imageNamesAndMessagesForSlides[i];
			var imageName = (String)(imageNameAndMessage[0]);
			var message = (String)(imageNameAndMessage[1]);

			var containerSlide = new ControlContainer
			(
				"containerSlide_" + i,
				this._zeroes, // pos
				this.sizeBase.clonify(), // size
				// children
				new Control[]
				{
					new ControlVisual
					(
						"imageSlide",
						this._zeroes,
						this.sizeBase.clonify(), // size
						new VisualImageFromLibrary(imageName, size)
					),

					new ControlLabel
					(
						"labelSlideText",
						new Coords(100, this.fontHeightInPixelsBase * 2), // pos
						this.sizeBase.clonify(), // size
						true, // isTextCentered,
						message,
						this.fontHeightInPixelsBase * 2
					),

					new ControlButton
					(
						"buttonNext",
						new Coords(75, 120), // pos
						new Coords(50, 40), // size
						"Next",
						this.fontHeightInPixelsBase,
						false, // hasBorder
						new DataBinding(true), // isEnabled
						(int slideIndexNext) -> 
						{
							Venue venueNext;
							if (slideIndexNext < controlsForSlides.size())
							{
								var controlForSlideNext = controlsForSlides.get(slideIndexNext);
								venueNext = new VenueControls(controlForSlideNext);
							}
							else
							{
								venueNext = venueAfterSlideshow;
							}
							venueNext = new VenueFader(venueNext, universe.venueCurrent);
							universe.venueNext = venueNext;
						}
					)
				}
			);

			containerSlide.scalePosAndSize(scaleMultiplier);

			controlsForSlides.add(containerSlide);
		}

		return controlsForSlides.get(0);
	};

	public Control title(Universe universe)
	{
		return this.title(universe, null);
	}

	public Control title(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		Runnable start = () ->
		{
			Venue venueNext = new VenueControls
			(
				universe.controlBuilder.profileSelect(universe)
			);
			venueNext = new VenueFader(venueNext, universe.venueCurrent);
			universe.venueNext = venueNext;
		};

		var returnValue = new ControlContainer
		(
			"containerTitle",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlVisual
				(
					"imageTitle",
					this._zeroes,
					this.sizeBase.clonify(), // size
					new VisualImageScaled(new VisualImageFromLibrary("Title"), size)
				),

				new ControlButton
				(
					"buttonStart",
					new Coords(75, 100), // pos
					new Coords(50, 40), // size
					"Start",
					fontHeight * 2,
					false, // hasBorder
					new DataBinding(true), // isEnabled
					start
				)
			}, // end children

			new Action[]
			{
				new Action( ControlActionNames.ControlCancel, (UniverseWorldPlaceEntities uwpe) -> start.run() ),
				new Action( ControlActionNames.ControlConfirm, (UniverseWorldPlaceEntities uwpe) -> start.run() )
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control worldDetail(Universe universe)
	{
		return this.worldDetail(universe, null);
	}

	public Control worldDetail(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var world = universe.world;

		var dateCreated = world.dateCreated;
		var dateSaved = world.dateSaved;

		var returnValue = new ControlContainer
		(
			"containerWorldDetail",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlLabel
				(
					"labelProfileName",
					new Coords(100, 40), // pos
					new Coords(100, 25), // size
					true, // isTextCentered
					"Profile: " + universe.profileSelected.name,
					fontHeight
				),
				new ControlLabel
				(
					"labelWorldName",
					new Coords(100, 55), // pos
					new Coords(150, 25), // size
					true, // isTextCentered
					"World: " + world.name,
					fontHeight
				),
				new ControlLabel
				(
					"labelStartDate",
					new Coords(100, 70), // pos
					new Coords(150, 25), // size
					true, // isTextCentered
					"Started:" + dateCreated.toStringTimestamp(),
					fontHeight
				),
				new ControlLabel
				(
					"labelSavedDate",
					new Coords(100, 85), // pos
					new Coords(150, 25), // size
					true, // isTextCentered
					"Saved:" + (dateSaved == null ? "[never]" : dateSaved.toStringTimestamp()),
					fontHeight
				),

				new ControlButton
				(
					"buttonStart",
					new Coords(50, 100), // pos
					new Coords(100, 25), // size
					"Start",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						var venueWorld = new VenueWorld(world);
						Venue venueNext;
						if (world.dateSaved != null)
						{
							venueNext = venueWorld;
						}
						else
						{
							var textInstructions =
								universe.mediaLibrary.textStringGetByName("Instructions");
							var instructions = textInstructions.value;
							var controlInstructions = universe.controlBuilder.message
							(
								universe,
								size,
								instructions,
								() -> // acknowledge
								{
									universe.venueNext = new VenueFader
									(
										venueWorld, universe.venueCurrent
									);
								}
							);

							var venueInstructions =
								new VenueControls(controlInstructions);

							var venueMovie = new VenueVideo
							(
								"Movie", // videoName
								venueInstructions // fader implicit
							);

							venueNext = venueMovie;
						}

						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonBack",
					new Coords(10, 10), // pos
					new Coords(15, 15), // size
					"<",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.profileDetail(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonDelete",
					new Coords(180, 10), // pos
					new Coords(15, 15), // size
					"x",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						var profile = universe.profile;
						var world = universe.world;

						var controlConfirm = universe.controlBuilder.confirm
						(
							universe,
							size,
							"Delete world \""
								+ world.name
								+ "\"?",
							() -> // confirm
							{
								var profile = universe.profileSelected;
								var world = universe.world;
								var worlds = profile.worlds;

								worlds.remove(world);
								universe.world = null;

								universe.profileHelper.profileSave
								(
									profile
								);

								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.profileDetail(universe)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							},
							() -> // cancel
							{
								Venue venueNext = new VenueControls
								(
									universe.controlBuilder.worldDetail(universe)
								);
								venueNext = new VenueFader(venueNext, universe.venueCurrent);
								universe.venueNext = venueNext;
							}
						);

						Venue venueNext = new VenueControls(controlConfirm);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);

						universe.venueNext = venueNext;
					}
				),
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	}

	public Control worldLoad(Universe universe)
	{
		return this.worldLoad(universe, null);
	}

	public Control worldLoad(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		var confirm = () ->
		{
			var profileOld = universe.profileSelected;
			var profilesReloaded = universe.profileHelper.profiles();
			for (var i = 0; i < profilesReloaded.length; i++)
			{
				var profileReloaded = profilesReloaded[i];
				if (profileReloaded.name == profileOld.name)
				{
					universe.profileSelected = profileReloaded;
					break;
				}
			}

			var worldOld = universe.world;
			var worldsReloaded = universe.profileSelected.worlds;
			var worldToReload = null;
			for (var i = 0; i < worldsReloaded.length; i++)
			{
				var worldReloaded = worldsReloaded[i];
				if (worldReloaded.name == worldOld.name)
				{
					worldToReload = worldReloaded;
					break;
				}
			}

			Venue venueNext = new VenueControls
			(
				universe.controlBuilder.worldLoad(universe)
			);
			venueNext = new VenueFader(venueNext, universe.venueCurrent);
			universe.venueNext = venueNext;

			if (worldToReload == null)
			{
				venueNext = new VenueControls
				(
					universe.controlBuilder.message
					(
						universe,
						size,
						"No save exists to reload!",
						() -> // acknowledge
						{
							Venue venueNext = new VenueControls
							(
								universe.controlBuilder.worldLoad(universe)
							);
							venueNext = new VenueFader(venueNext, universe.venueCurrent);
							universe.venueNext = venueNext;
						}
					)
				);
				venueNext = new VenueFader(venueNext, universe.venueCurrent);
				universe.venueNext = venueNext;
			}
			else
			{
				universe.world = worldReloaded;
			}
		};

		var cancel = () ->
		{
			Venue venueNext = new VenueControls
			(
				universe.controlBuilder.worldLoad(universe)
			);
			venueNext = new VenueFader(venueNext, universe.venueCurrent);
			universe.venueNext = venueNext;
		};

		var returnValue = new ControlContainer
		(
			"containerWorldLoad",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlButton
				(
					"buttonLoadFromServer",
					new Coords(30, 15), // pos
					new Coords(140, 25), // size
					"Reload from Local Storage",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						var controlConfirm = universe.controlBuilder.confirm
						(
							universe,
							size,
							"Abandon the current game?",
							confirm,
							cancel
						);

						Venue venueNext = new VenueControls(controlConfirm);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonLoadFromFile",
					new Coords(30, 50), // pos
					new Coords(140, 25), // size
					"Load from File",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						var profile = universe.profileSelected;
						var world = universe.world;

						var venueFileUpload = new VenueFileUpload(null);

						var venueMessageReadyToLoad = new VenueControls
						(
							universe.controlBuilder.message
							(
								universe,
								size,
								"Ready to load from file...",
								() ->
								{
									var callback = (String fileContentsAsString) ->
									{
										var worldAsJSON = fileContentsAsString;
										var worldDeserialized = universe.serializer.deserialize(worldAsJSON);
										universe.world = worldDeserialized;

										Venue venueNext = new VenueControls
										(
											universe.controlBuilder.game(universe, size)
										);
										venueNext = new VenueFader(venueNext, universe.venueCurrent);
										universe.venueNext = venueNext;
									};

									var inputFile = venueFileUpload.domElement.getElementsByTagName("input")[0];
									var fileToLoad = inputFile.files[0];
									new FileHelper().loadFileAsText
									(
										fileToLoad,
										callback,
										null // contextForCallback
									);

								}
							)
						);

						var venueMessageCancelled = new VenueControls
						(
							universe.controlBuilder.message
							(
								universe,
								size,
								"No file specified.",
								() ->
								{
									Venue venueNext = new VenueControls
									(
										universe.controlBuilder.game(universe, size)
									);
									venueNext = new VenueFader(venueNext, universe.venueCurrent);
									universe.venueNext = venueNext;
								}
							)
						);

						venueFileUpload.venueNextIfFileSpecified = venueMessageReadyToLoad;
						venueFileUpload.venueNextIfCancelled = venueMessageCancelled;

						universe.venueNext = venueFileUpload;
					}
				),

				new ControlButton
				(
					"buttonReturn",
					new Coords(30, 105), // pos
					new Coords(140, 25), // size
					"Return",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.game(universe, size)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				)
			}
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	};

	public Control worldSave(Universe universe)
	{
		return this.worldSave(universe, null);
	}

	public Control worldSave(Universe universe, Coords size)
	{
		if (size == null)
		{
			size = universe.display.sizeDefault();
		}

		var scaleMultiplier =
			this._scaleMultiplier.overwriteWith(size).divide(this.sizeBase);

		var fontHeight = this.fontHeightInPixelsBase;

		Runnable saveToLocalStorage = () ->
		{
			var profile = universe.profileSelected;
			var world = universe.world;

			world.dateSaved = DateTime.now();
			var wasSaveSuccessful = universe.profileHelper.profileSave
			(
				profile
			);

			var message =
			(
				wasSaveSuccessful ? "Profile saved to local storage." : "Save failed due to errors."
			);

			Venue venueNext = new VenueControls
			(
				universe.controlBuilder.message
				(
					universe,
					size,
					message,
					() -> // acknowledge
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.game(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				)
			);
			venueNext = new VenueFader(venueNext, universe.venueCurrent);
			universe.venueNext = venueNext;
		};

		var returnValue = new ControlContainer
		(
			"containerSave",
			this._zeroes, // pos
			this.sizeBase.clonify(), // size
			// children
			new Control[]
			{
				new ControlButton
				(
					"buttonSaveToLocalStorage",
					new Coords(30, 15), // pos
					new Coords(140, 25), // size
					"Save to Local Storage",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					saveToLocalStorage
				),

				new ControlButton
				(
					"buttonSaveToFile",
					new Coords(30, 50), // pos
					new Coords(140, 25), // size
					"Save to File",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						var profile = universe.profileSelected;
						var world = universe.world;

						world.dateSaved = DateTime.now();
						var worldSerialized = universe.serializer.serialize(world);

						new FileHelper().saveTextStringToFileWithName
						(
							worldSerialized,
							world.name + ".json"
						);

						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.message
							(
								universe,
								size,
								"Save must be completed manually.",
								(universe) ->
								{
									Venue venueNext = new VenueControls
									(
										universe.controlBuilder.game(universe)
									);
									venueNext = new VenueFader(venueNext, universe.venueCurrent);
									universe.venueNext = venueNext;
								}
							)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),

				new ControlButton
				(
					"buttonReturn",
					new Coords(30, 105), // pos
					new Coords(140, 25), // size
					"Return",
					fontHeight,
					true, // hasBorder
					new DataBinding(true), // isEnabled
					() -> // click
					{
						Venue venueNext = new VenueControls
						(
							universe.controlBuilder.game(universe)
						);
						venueNext = new VenueFader(venueNext, universe.venueCurrent);
						universe.venueNext = venueNext;
					}
				),
			} // end children
		);

		returnValue.scalePosAndSize(scaleMultiplier);

		return returnValue;
	}
}
