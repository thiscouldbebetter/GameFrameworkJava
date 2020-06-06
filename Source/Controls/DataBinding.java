package Controls;

import java.util.function.*;

public class DataBinding<T, U>
{
	private T context;
	private Function<T,U> _get;
	private BiConsumer<T,U> _set;

	public DataBinding(T context)
	{
		this(context, null, null);
	}

	public DataBinding(T context, Function<T,U> get)
	{
		this(context, get, null);
	}

	public DataBinding(T context, Function<T,U> get, BiConsumer<T,U> set)
	{
		this.context = context;
		this._get = get;
		this._set = set;
	}

	public DataBinding contextSet(T value)
	{
		this.context = value;
		return this;
	}

	public U get()
	{
		return this._get.apply(this.context);
	}

	public void set(U value)
	{
		this._set.accept(this.context, value);
	}
}
