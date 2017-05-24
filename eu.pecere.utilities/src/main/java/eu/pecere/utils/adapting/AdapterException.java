package eu.pecere.utils.adapting;

public class AdapterException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public AdapterException()
	{
		super();
	}
	
	public AdapterException( String message )
	{
		super( message );
	}
	
	public AdapterException( Throwable e )
	{
		super( e );
	}
	
	public AdapterException( String message, Throwable e )
	{
		super( message, e );
	}
}
