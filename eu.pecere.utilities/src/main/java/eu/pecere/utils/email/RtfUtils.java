package eu.pecere.utils.email;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RtfUtils
{
	private static final Log log = LogFactory.getLog( RtfUtils.class );
	
	public static String convertRtfToHtml( String rtfText ) throws IOException
	{
		Reader rtfReader = new StringReader( rtfText );
		return RtfUtils.convertRtfToHtml( rtfReader );
	}
	
	public static String convertRtfToHtml( File rtfFile ) throws IOException
	{
		Reader rtfReader = new FileReader( rtfFile );
		return RtfUtils.convertRtfToHtml( rtfReader );
	}
	
	public static String convertRtfToHtml( Reader rtfReader ) throws IOException
	{
		log.debug( "Executing class: " + RtfUtils.class.getSimpleName() );
		
		JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setContentType( "text/rtf" );
		
		EditorKit editorKitForRtf = jEditorPane.getEditorKitForContentType( "text/rtf" );
		
		try {
			
			editorKitForRtf.read( rtfReader, jEditorPane.getDocument(), 0 );
			editorKitForRtf = null;
			
			EditorKit editorKitForHtml = jEditorPane.getEditorKitForContentType( "text/html" );
			
			Writer writer = new StringWriter();
			editorKitForHtml.write( writer, jEditorPane.getDocument(), 0, jEditorPane.getDocument().getLength() );
			
			log.debug( "Executed class: " + RtfUtils.class.getSimpleName() );
			return writer.toString();
			
		} catch( IOException | BadLocationException e ) {
			String errorMessage = "Error while converting from RTF to HTML, processing class "
					+ RtfUtils.class.getSimpleName() + ".\r\n"
					+ "Exception: " + e.getClass().getSimpleName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw new IOException( e );
		}
		
	}
}
