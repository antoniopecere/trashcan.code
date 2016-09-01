package eu.pecere.utilities.commons;

public class JsonException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public JsonException()
	{
		super();
	}
	
	public JsonException( Throwable e )
	{
		super( e );
	}
	
}
