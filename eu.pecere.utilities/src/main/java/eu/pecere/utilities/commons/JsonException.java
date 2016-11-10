package eu.pecere.utilities.commons;

public class JsonException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public JsonException()
	{
		super();
	}
	
	public JsonException( String message )
	{
		super( message );
	}
	
	public JsonException( Throwable e )
	{
		super( e );
	}
	
	public JsonException( String message, Throwable e )
	{
		super( message, e );
	}
}
