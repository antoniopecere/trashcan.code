/**
 * 
 */
package eu.pecere.utils.pdf.conversion;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * Utility class that expose the public void method {@link #addMetadataToPdf(InputStream, OutputStream, Map)} to set
 * tags to an existing PDF file.
 * 
 * @author Antonio Pecere
 *
 */
class PdfManipulator
{
	private static final Log log = LogFactory.getLog( PdfManipulator.class );
	
	static void addMetadataToPdf( InputStream inputStream, OutputStream outputStream, TreeMap<String, String> metadata ) throws Exception
	{
		log.info( "Method PdfManipulator.addMetadataToPdf(): START" );
		
		PdfReader reader = new PdfReader( inputStream );
		PdfStamper stamper = new PdfStamper( reader, outputStream );
		
		TreeMap<String, String> originalPdfTags = new TreeMap<>( reader.getInfo() );
		metadata.putAll( originalPdfTags );
		stamper.setMoreInfo( metadata );
		
		stamper.close();
		reader.close();
		
		log.info( "Method PdfManipulator.addMetadataToPdf(): STOP" );
	}
	
}
