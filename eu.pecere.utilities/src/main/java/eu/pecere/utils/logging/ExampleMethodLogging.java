package eu.pecere.utils.logging;

public class ExampleMethodLogging
{
	private final LogWrapper log = LogWrapper.build( this.getClass() );
	
	public void chiamataDiEsempio()
	{
		log.info( "[ UserID, PassWord ] -> [ ||, || ]" );
	}
	
	public static void main( String[] args )
	{
		new ExampleMethodLogging().chiamataDiEsempio();
	}
}
