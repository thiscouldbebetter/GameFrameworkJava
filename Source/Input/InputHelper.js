
function InputHelper()
{
	// Helper variables.

	this.mouseClickPos = new Coords();
	this.mouseMovePos = new Coords(0, 0);
	this.mouseMovePosPrev = new Coords(0, 0);
	this.mouseMovePosNext = new Coords(0, 0);

	var inputNames = Input.Names();
	this.inputNames = inputNames;
	this.keysToPreventDefaultsFor =
	[
		inputNames.ArrowDown, inputNames.ArrowLeft, inputNames.ArrowRight,
		inputNames.ArrowUp, inputNames.Tab
	];
}

{
	InputHelper.prototype.actionsFromInput = function(actions, actionToInputsMappings)
	{
		var returnValues = [];

		var inputsPressed = this.inputsPressed;
		for (var i = 0; i < inputsPressed.length; i++)
		{
			var inputPressed = inputsPressed[i];
			if (inputPressed.isActive)
			{
				var mapping = actionToInputsMappings[inputPressed.name];
				if (mapping != null)
				{
					var actionName = mapping.actionName;
					var action = actions[actionName];
					returnValues.push(action);
					if (mapping.inactivateInputWhenActionPerformed)
					{
						inputPressed.isActive = false;
					}
				}
			}
		}

		return returnValues;
	};

	InputHelper.prototype.initialize = function(universe)
	{
		this.inputsPressed = [];
		this.gamepadsConnected = [];

		this.isMouseMovementTracked = true; // hack

		if (universe == null)
		{
			// hack - Allows use of this class
			// without including PlatformHelper or Universe.
			this.toDomElement();
		}
		else
		{
			universe.platformHelper.platformableAdd(this);
		}

		this.gamepadsCheck();
	};

	InputHelper.prototype.inputAdd = function(inputPressedName)
	{
		if (this.inputsPressed[inputPressedName] == null)
		{
			var inputPressed = new Input(inputPressedName);
			this.inputsPressed[inputPressedName] = inputPressed;
			this.inputsPressed.push(inputPressed);
		}
	};

	InputHelper.prototype.inputRemove = function(inputReleasedName)
	{
		if (this.inputsPressed[inputReleasedName] != null)
		{
			var inputReleased = this.inputsPressed[inputReleasedName];
			delete this.inputsPressed[inputReleasedName];
			this.inputsPressed.remove(inputReleased);
		}
	};

	InputHelper.prototype.inputsActive = function()
	{
		return this.inputsPressed.filter( (x) => x.isActive );
	};

	InputHelper.prototype.inputsRemoveAll = function()
	{
		for (var i = 0; i < this.inputsPressed.length; i++)
		{
			var input = this.inputsPressed[i];
			this.inputRemove(input);
		}
	};

	InputHelper.prototype.isMouseClicked = function(value)
	{
		var inputNameMouseClick = this.inputNames.MouseClick;
		if (value == null)
		{
			var inputPressed = this.inputsPressed[inputNameMouseClick];
			var returnValue = (inputPressed != null && inputPressed.isActive);
			return returnValue;
		}
		else
		{
			if (value == true)
			{
				this.inputAdd(inputNameMouseClick);
			}
			else
			{
				this.inputRemove(inputNameMouseClick);
			}
		}
	};

	InputHelper.prototype.updateForTimerTick = function(universe)
	{
		this.updateForTimerTick_Gamepads(universe);
	};

	InputHelper.prototype.updateForTimerTick_Gamepads = function(universe)
	{
		var systemGamepads = this.systemGamepads();
		var inputNames = this.inputNames;

		for (var i = 0; i < this.gamepadsConnected.length; i++)
		{
			var gamepad = this.gamepadsConnected[i];
			var systemGamepad = systemGamepads[gamepad.index];
			gamepad.updateFromSystemGamepad(systemGamepad);

			var axisDisplacements = gamepad.axisDisplacements;
			for (var a = 0; a < axisDisplacements.length; a++)
			{
				var axisDisplacement = axisDisplacements[a];
				if (axisDisplacement == 0)
				{
					if (a == 0)
					{
						this.inputRemove(inputNames.gamepadMoveLeft + i);
						this.inputRemove(inputNames.gamepadMoveRight + i);
					}
					else
					{
						this.inputRemove(inputNames.gamepadMoveUp + i);
						this.inputRemove(inputNames.gamepadMoveDown + i);
					}
				}
				else
				{
					var directionName;
					if (a == 0)
					{
						directionName = (axisDisplacement < 0 ? "Left" : "Right");
					}
					else
					{
						directionName = (axisDisplacement < 0 ? "Up" : "Down");
					}

					this.inputAdd(gamepadIDMove + directionName);
				}
			} // end for

			var gamepadIDButton = gamepadID + "Button";
			var areButtonsPressed = gamepad.buttonsPressed;
			for (var b = 0; b < areButtonsPressed.length; b++)
			{
				var isButtonPressed = areButtonsPressed[b];

				if (isButtonPressed)
				{
					this.inputAdd(gamepadIDButton + b);
				}
				else
				{
					this.inputRemove(gamepadIDButton + b);
				}
			}
		}
	};

	// events

	// events - keyboard

	InputHelper.prototype.handleEventKeyDown = function(event)
	{
		var inputPressed = event.key;

		if (this.keysToPreventDefaultsFor.contains(inputPressed))
		{
			event.preventDefault();
		}

		if (inputPressed == " ")
		{
			inputPressed = "_";
		}
		else if (inputPressed == "_")
		{
			inputPressed = "__";
		}
		else if (isNaN(inputPressed) == false)
		{
			inputPressed = "_" + inputPressed;
		}

		this.inputAdd(inputPressed);
	};

	InputHelper.prototype.handleEventKeyUp = function(event)
	{
		var inputReleased = event.key;
		if (inputReleased == " ")
		{
			inputReleased = "_";
		}
		else if (inputReleased == "_")
		{
			inputReleased = "__";
		}
		else if (isNaN(inputReleased) == false)
		{
			inputReleased = "_" + inputReleased;
		}

		this.inputRemove(inputReleased);
	};

	// events - mouse

	InputHelper.prototype.handleEventMouseDown = function(event)
	{
		var canvas = event.target;
		var canvasBox = canvas.getBoundingClientRect();
		this.mouseClickPos.overwriteWithDimensions
		(
			event.clientX - canvasBox.left,
			event.clientY - canvasBox.top,
			0
		);
		this.inputAdd(this.inputNames.MouseClick);
	};

	InputHelper.prototype.handleEventMouseMove = function(event)
	{
		var canvas = event.target;
		var canvasBox = canvas.getBoundingClientRect();
		this.mouseMovePosNext.overwriteWithDimensions
		(
			event.clientX - canvasBox.left,
			event.clientY - canvasBox.top,
			0
		);

		if (this.mouseMovePosNext.equals(this.mouseMovePos) == false)
		{
			this.mouseMovePosPrev.overwriteWith(this.mouseMovePos);
			this.mouseMovePos.overwriteWith(this.mouseMovePosNext);
			this.inputAdd(this.inputNames.MouseMove);
		}
	};

	InputHelper.prototype.handleEventMouseUp = function(event)
	{
		this.inputRemove(this.inputNames.MouseClick);
	};

	// gamepads

	InputHelper.prototype.gamepadsCheck = function()
	{
		var systemGamepads = this.systemGamepads();
		for (var i = 0; i < systemGamepads.length; i++)
		{
			var systemGamepad = systemGamepads[i];
			if (systemGamepad != null)
			{
				var gamepad = new Gamepad(i);
				this.gamepadsConnected.push(gamepad);
			}
		}
	};

	InputHelper.prototype.systemGamepads = function()
	{
		return navigator.getGamepads();
	};

	// Platformable.

	InputHelper.prototype.toDomElement = function(platformHelper)
	{
		document.body.onkeydown = this.handleEventKeyDown.bind(this);
		document.body.onkeyup = this.handleEventKeyUp.bind(this);
		var divMain = (platformHelper == null ? document.getElementById("divMain") : platformHelper.divMain);
		divMain.onmousedown = this.handleEventMouseDown.bind(this);
		divMain.onmouseup = this.handleEventMouseUp.bind(this);
		divMain.onmousemove = (this.isMouseMovementTracked ? this.handleEventMouseMove.bind(this) : null);
	};

}
