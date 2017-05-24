/**
 * 
 */
package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

/**
 * @author Antonio Pecere
 *
 */
class XmlToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( XmlToPdfConverter.class );
	
	protected XmlToPdfConverter( File inputFile )
	{
		super( inputFile );
	}
	
	/**
	 * @see eu.pecere.utils.pdf.conversion.ToPdfConverter#convert(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Method XmlToPdfConverter.convert( InputStream, OutputStream ): START!" );
		try {
			
			PdfDocument document = new PdfDocument();
			// document.open();
			// document.newPage();
			
			PdfWriter writer = PdfWriter.getInstance( document, outputStream );
			// writer.open();
			
			// document.add( new Chunk( "" ) );
			XMLWorkerHelper.getInstance().parseXHtml( writer, document, inputStream );
			// writer.flush();
			writer.close();
			// document.close();
		} catch( DocumentException e ) {
			String errorMessage = "Error on method XmlToPdfConverter.convert() while trying to convert XML file into PDF.";
			log.error( errorMessage, e );
			throw new IOException( e );
		}
		
		log.info( "Method XmlToPdfConverter.convert( InputStream, OutputStream ): STOP!" );
	}
	
}
