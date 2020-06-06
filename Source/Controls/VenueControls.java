package Controls;

import java.util.*;
import Geometry.*;
import Input.*;
import Model.*;
import Utility.*;

public class VenueControls implements Venue
{
	private Control controlRoot;

	private ActionToInputsMapping[] actionToInputsMappings;
	private HashMap<String,ActionToInputsMapping> actionToInputsMappingsByName;

	// Helper variables.

	private Location _drawLoc = new Location(new Coords());
	private Coords _mouseClickPos = new Coords();
	private Coords _mouseMovePos = new Coords();
	private Coords _mouseMovePosPrev = new Coords();

	public VenueControls(Control controlRoot)
	{
		this.controlRoot = controlRoot;

		var inactivate = true;
		var inputs = Input.Instances();
		var gamepadCount = 2; // hack
		this.actionToInputsMappings = new ActionToInputsMapping[]
		{
			new ActionToInputsMapping(ControlActionNames.ControlIncrement, 	new ArrayList<String>(ArrayHelper.addAll(Arrays.asList(inputs.ArrowDown.name), buildGamepadInputs(gamepadCount, inputs.GamepadMoveDown.name))), inactivate),
			new ActionToInputsMapping(ControlActionNames.ControlPrev, 		new ArrayList<String>(ArrayHelper.addAll(Arrays.asList(inputs.ArrowLeft.name), buildGamepadInputs(gamepadCount, inputs.GamepadMoveLeft.name))), inactivate),
			new ActionToInputsMapping(ControlActionNames.ControlNext, 		new ArrayList<String>(ArrayHelper.addAll(Arrays.asList(inputs.ArrowRight.name, inputs.Tab.name), buildGamepadInputs(gamepadCount, inputs.GamepadMoveRight.name))), inactivate),
			new ActionToInputsMapping(ControlActionNames.ControlDecrement, 	new ArrayList<String>(ArrayHelper.addAll(Arrays.asList(inputs.ArrowUp.name), buildGamepadInputs(gamepadCount, inputs.GamepadMoveUp.name))), inactivate),
			new ActionToInputsMapping(ControlActionNames.ControlConfirm, 	new ArrayList<String>(ArrayHelper.addAll(Arrays.asList(inputs.Enter.name), buildGamepadInputs(gamepadCount, inputs.GamepadButton1.name))), inactivate),
			new ActionToInputsMapping(ControlActionNames.ControlCancel, 	new ArrayList<String>(ArrayHelper.addAll(Arrays.asList(inputs.Escape.name), buildGamepadInputs(gamepadCount, inputs.GamepadButton0.name))), inactivate)
		};

		if (this.controlRoot.actionToInputsMappings != null)
		{
			this.actionToInputsMappings.addMany(this.controlRoot.actionToInputsMappings());
		}

		this.actionToInputsMappingsByName = ArrayHelper.addLookupsMultiple
		(
			this.actionToInputsMappings,
			(ActionToInputsMapping x) -> { return x.inputNames.toArray(new String[0]); } 
		);
	}

	public ArrayList<String> buildGamepadInputs(int numberOfGamepads, String inputName)
	{
		var returnValues = new ArrayList<String>();

		for (var i = 0; i < numberOfGamepads; i++)
		{
			var inputNameForGamepad = inputName + i;
			returnValues.add(inputNameForGamepad);
		}

		return returnValues;
	}

	public void draw(Universe universe)
	{
		var display = universe.display;
		var drawLoc = this._drawLoc;
		drawLoc.pos.clear();
		this.controlRoot.draw(universe, display, drawLoc);
	}

	public void finalize(Universe universe)
	{}

	public void initialize(Universe universe)
	{}

	public void updateForTimerTick(Universe universe)
	{
		this.draw(universe);

		var inputHelper = universe.inputHelper;
		var inputsPressed = inputHelper.inputsPressed;
		var inputs = Input.Instances();

		for (var i = 0; i < inputsPressed.size(); i++)
		{
			var inputPressed = inputsPressed.get(i);
			if (inputPressed.isActive == true)
			{
				var inputPressedName = inputPressed.name;

				var mapping = this.actionToInputsMappingsByInputName.get(inputPressedName);

				if (inputPressedName.startsWith("Mouse") == false)
				{
					if (mapping == null)
					{
						// Pass the raw input, to allow for text entry.
						var wasActionHandled = this.controlRoot.actionHandle(inputPressedName, universe);
						if (wasActionHandled)
						{
							inputPressed.isActive = false;
						}
					}
					else
					{
						var actionName = mapping.actionName;
						this.controlRoot.actionHandle(actionName, universe);
						if (mapping.inactivateInputWhenActionPerformed)
						{
							inputPressed.isActive = false;
						}
					}
				}
				else if (inputPressedName == inputs.MouseClick.name)
				{
					this._mouseClickPos.overwriteWith
					(
						inputHelper.mouseClickPos
					).divide
					(
						universe.display.scaleFactor()
					);
					var wasClickHandled = this.controlRoot.mouseClick(this._mouseClickPos);
					if (wasClickHandled)
					{
						//inputHelper.inputRemove(inputPressed);
						inputPressed.isActive = false;
					}
				}
				else if (inputPressedName == inputs.MouseMove.name)
				{
					this._mouseMovePos.overwriteWith
					(
						inputHelper.mouseMovePos
					).divide
					(
						universe.display.scaleFactor()
					);
					this._mouseMovePosPrev.overwriteWith
					(
						inputHelper.mouseMovePosPrev
					).divide
					(
						universe.display.scaleFactor()
					);

					this.controlRoot.mouseMove
					(
						this._mouseMovePos //, this._mouseMovePosPrev
					);
				}

			} // end if isActive

		} // end for

	} // end method

} // end class
