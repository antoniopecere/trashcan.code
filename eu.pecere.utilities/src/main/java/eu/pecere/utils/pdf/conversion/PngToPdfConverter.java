package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.PngImage;

import eu.pecere.utils.commons.FileUtils;

/**
 * This class implements a specific converter for images in PNG format.
 * It uses iTextPdf library, and the utility method {@link ToPdfConverter#convertAndAddImage(Image, OutputStream)}.
 * 
 * @author Antonio Pecere
 */
class PngToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( PngToPdfConverter.class );
	
	protected PngToPdfConverter( File inputFile )
	{
		super( inputFile );
	}
	
	@Override
	protected void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Java conversion from Png to PDF: Started!" );
		
		try {
			inputStream = FileUtils.thumbnailator( inputStream, "png" );
			
			Image image = PngImage.getImage( inputStream );
			ToPdfConverter.convertAndAddImage( image, outputStream );
		} catch( DocumentException e ) {
			log.error( e );
			throw new IOException( e.getMessage(), e );
		}
		
		log.info( "Java conversion from Png to PDF: Completed!" );
	}
	
}
