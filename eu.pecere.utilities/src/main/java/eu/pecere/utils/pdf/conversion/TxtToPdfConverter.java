/**
 * 
 */
package eu.pecere.utils.pdf.conversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Antonio Pecere
 *
 */
class TxtToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( JpgToPdfConverter.class );
	
	protected TxtToPdfConverter( File inputFile )
	{
		super( inputFile );
	}
	
	/**
	 * @see eu.pecere.utils.pdf.conversion.ToPdfConverter#convert(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Method TxtToPdfConverter.convert( InputStream, OutputStream ): START!" );
		BufferedReader br = null;
		
		try {
			Document pdfDoc = new Document( PageSize.A4 );
			PdfWriter.getInstance( pdfDoc, outputStream ).setPdfVersion( PdfWriter.VERSION_1_7 );
			
			pdfDoc.open();
			
			Font myfont = new Font();
			myfont.setStyle( Font.NORMAL );
			myfont.setSize( 11 );
			
			pdfDoc.add( new Paragraph( "\n" ) );
			
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			String strLine;
			
			while( ( strLine = br.readLine() ) != null ) {
				Paragraph para = new Paragraph( strLine + "\n", myfont );
				para.setAlignment( Element.ALIGN_JUSTIFIED );
				pdfDoc.add( para );
			}
			
			pdfDoc.close();
		}
		
		catch( Exception e ) {
			log.error( e );
			throw new IOException( e );
		} finally {
			if( br != null )
				br.close();
			log.info( "Method TxtToPdfConverter.convert( InputStream, OutputStream ): STOP!" );
		}
	}
	
}
