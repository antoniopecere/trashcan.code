package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.BmpImage;

import eu.pecere.utils.commons.FileUtils;

/**
 * This class implements a specific converter for images in BMP format.
 * It uses iTextPdf library, and the utility method {@link ToPdfConverter#convertAndAddImage(Image, OutputStream)}.
 * 
 * @author Antonio Pecere
 * 
 */
class BmpToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( BmpToPdfConverter.class );
	
	protected BmpToPdfConverter( File inputFile )
	{
		super( inputFile );
	}
	
	@Override
	protected void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Java conversion from Bmp to PDF: Started!" );
		
		try {
			inputStream = FileUtils.thumbnailator( inputStream, "bmp" );
			
			Image image = BmpImage.getImage( inputStream );
			ToPdfConverter.convertAndAddImage( image, outputStream );
		} catch( DocumentException e ) {
			log.error( e );
			throw new IOException( e.getMessage(), e );
		} catch( RuntimeException e ) {
			log.error( e );
			String errorMessage = e.getMessage();
			if( "Invalid magic value for BMP file.".equals( errorMessage ) ) {
				throw new IOException( errorMessage, e );
			}
			throw e;
		}
		
		log.info( "Java conversion from Bmp to PDF: Completed!" );
	}
	
}
