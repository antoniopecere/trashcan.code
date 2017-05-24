package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.GifImage;

/**
 * This class implements a specific converter for images in GIF format.
 * GIF images are made of many frames like animations, so the converter assume to retrieve and convert the first frame
 * only; usually the one that contains the full image.
 * It uses iTextPdf library, and the utility method {@link ToPdfConverter#convertAndAddImage(Image, OutputStream)}.
 * 
 * @author Antonio Pecere
 *
 */
class GifToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( GifToPdfConverter.class );
	
	protected GifToPdfConverter( File inputFile )
	{
		super( inputFile );
	}
	
	@Override
	protected void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Java conversion from Gif to PDF: Started!" );
		
		GifImage myGifFrames = new GifImage( inputStream );
		int numberOfFrames = myGifFrames.getFrameCount();
		log.info( "Number of frames in Gif File " + numberOfFrames );
		
		try {
			// Gif are made of many frames like animations.
			
			// Retrieve the first frame, usually containing the full image.
			Image image = myGifFrames.getImage( 1 );
			ToPdfConverter.convertAndAddImage( image, outputStream );
			
			// @formatter:off
			// Snippet code to retrieve every frame composing the gif...
			// for( int i = 1; i <= numberOfFrames; i++ ) {
			// 	Image image = myGifFrames.getImage( i );
			// 	ToPdfConverter.convertAndAddImage( image, outputStream );
			// }
			// @formatter:on
			
		} catch( DocumentException e ) {
			log.error( e );
			throw new IOException( e.getMessage(), e );
		}
		
		log.info( "Java conversion from Gif to PDF: Completed!" );
	}
	
}
