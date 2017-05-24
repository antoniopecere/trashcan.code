package eu.pecere.utils.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;

public class HtmlUtils
{
	private static final Log log = LogFactory.getLog( HtmlUtils.class );
	
	public static String extractText( String htmlContent )
	{
		try {
			Source source = new Source( htmlContent );
			SourceFormatter formatter = source.getSourceFormatter();
			formatter.setNewLine( "\r\n" );
			
			String renderedText = source.getRenderer().toString();
			return renderedText;
		} catch( Throwable e ) {
			log.error( e.getMessage(), e );
			throw e;
		}
	}
}
