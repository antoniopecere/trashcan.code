package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;

import eu.pecere.utils.commons.FileUtils;

/**
 * This class implements a specific converter for images in JPG formats.
 * It uses iTextPdf library, and the utility method {@link ToPdfConverter#convertAndAddImage(Image, OutputStream)}.
 * 
 * @author Antonio Pecere
 * 
 */
class JpgToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( JpgToPdfConverter.class );
	
	protected JpgToPdfConverter( File inputFile )
	{
		super( inputFile );
	}
	
	@Override
	protected void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Java conversion from Jpg to PDF: Started!" );
		
		try {
			inputStream = FileUtils.thumbnailator( inputStream, "jpg" );
			
			java.awt.Image awtImage = ImageIO.read( inputStream );
			Image image = Image.getInstance( awtImage, null );
			// Image image = Image.getInstance( awtImage, java.awt.Color.WHITE );
			ToPdfConverter.convertAndAddImage( image, outputStream );
		} catch( DocumentException e ) {
			log.error( e );
			throw new IOException( e.getMessage(), e );
		}
		
		log.info( "Java conversion from Jpg to PDF: Completed!" );
	}
	
}
