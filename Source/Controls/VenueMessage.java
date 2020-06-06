package Controls;

import java.util.*;
import Display.*;
import Geometry.*;
import Model.*;

public class VenueMessage implements Venue
{
	private String messageToShow;
	private Runnable acknowledge;
	private Venue venuePrev;
	private Coords sizeInPixels;
	private boolean showMessageOnly;

	private Coords _sizeInPixels;
	private Venue _venueInner;

	public VenueMessage(String messageToShow)
	{
		this(messageToShow, null, null, null);
	}

	public VenueMessage
	(
		String messageToShow, Runnable acknowledge, Venue venuePrev,
		Coords sizeInPixels
	)
	{
		this(messageToShow, acknowledge, venuePrev, sizeInPixels, false);
	}

	public VenueMessage
	(
		String messageToShow, Runnable acknowledge, Venue venuePrev,
		Coords sizeInPixels, boolean showMessageOnly
	)
	{
		this.messageToShow = messageToShow;
		this.acknowledge = acknowledge;
		this.venuePrev = venuePrev;
		this._sizeInPixels = sizeInPixels;
		this.showMessageOnly = showMessageOnly;
	}

	// instance methods

	public void draw(Universe universe)
	{
		this.venueInner(universe).draw(universe);
	}

	public Coords sizeInPixels(Universe universe)
	{
		return (this._sizeInPixels == null ? universe.display.sizeInPixels : this._sizeInPixels);
	}

	public void finalize(Universe universe)
	{}

	public void initialize(Universe universe)
	{}

	public void updateForTimerTick(Universe universe)
	{
		this.venueInner(universe).updateForTimerTick(universe);
	}

	public Venue venueInner(Universe universe)
	{
		if (this._venueInner == null)
		{
			var sizeInPixels = this.sizeInPixels(universe);

			var controlMessage = universe.controlBuilder.message
			(
				universe,
				sizeInPixels,
				this.messageToShow,
				this.acknowledge,
				this.showMessageOnly
			);

			var venuesToLayer = new ArrayList<Venue>();

			if (this.venuePrev != null)
			{
				venuesToLayer.add(this.venuePrev);
			}

			venuesToLayer.add(new VenueControls(controlMessage));

			this._venueInner = new VenueLayered
			(
				venuesToLayer.toArray(new Venue[0])
			);
		}

		return this._venueInner;
	}
}
