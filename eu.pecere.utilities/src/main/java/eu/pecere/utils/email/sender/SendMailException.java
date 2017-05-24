package eu.pecere.utils.email.sender;

@SuppressWarnings( "serial" )
public class SendMailException extends RuntimeException
{
	public SendMailException( Throwable e )
	{
		super( "Send Mail Exception:", e );
	}
}
