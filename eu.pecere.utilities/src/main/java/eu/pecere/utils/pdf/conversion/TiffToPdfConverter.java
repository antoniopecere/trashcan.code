package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//Document object to add logical image files to PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
//The image class to extract separate images from Tiff image
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
//PdfWriter object to write the PDF document
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
//Read Tiff File, Get number of Pages
import com.itextpdf.text.pdf.codec.TiffImage;

/**
 * This class implements a specific converter for images in TIFF formats, usually documents produced by FAXes.
 * It converts every page included in the TIFF in a single pdf document.
 * It uses iTextPdf library.
 * 
 * @author Antonio Pecere
 *
 */
class TiffToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( TiffToPdfConverter.class );
	
	protected TiffToPdfConverter( File inputFile )
	{
		super( inputFile );
	}
	
	@Override
	public void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Java conversion from Tiff to PDF: Started!" );
		
		RandomAccessSourceFactory rasFactory = new RandomAccessSourceFactory();
		RandomAccessSource ras = rasFactory.createSource( inputStream );
		
		RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray( ras );
		
		int numberOfPages = TiffImage.getNumberOfPages( myTiffFile );
		log.info( "Number of Images in Tiff File " + numberOfPages );
		
		try {
			Document destinationDoc = new Document( PageSize.A4, 0, 0, 0, 0 );
			
			@SuppressWarnings( "unused" )
			PdfWriter writer = PdfWriter.getInstance( destinationDoc, outputStream );
			
			destinationDoc.open();
			
			// Iterate to extract images from Tiff file into a Image object to add into PDF
			for( int i = 1; i <= numberOfPages; i++ ) {
				Image image = TiffImage.getTiffImage( myTiffFile, i );
				float imgWidth = image.getScaledWidth();
				float imgHeight = image.getScaledHeight();
				int xResolution = image.getDpiX();
				int yResolution = image.getDpiY();
				
				float docWidth = destinationDoc.getPageSize().getWidth();
				float docHeight = destinationDoc.getPageSize().getHeight();
				
				if( imgWidth / xResolution > imgHeight / yResolution )
					image.setRotationDegrees( 90 );
				
				// Scale if too big to fit on page
				if( imgWidth > docWidth || imgHeight > docHeight ) {
					
					//@formatter:off
					boolean xyHasDifferentResolutions =
							xResolution != 0 &&
							yResolution != 0 &&
							xResolution != yResolution;
					//@formatter:on
					
					if( xyHasDifferentResolutions ) {
						image.scalePercent( 100 );
						float percentX = ( docWidth * 100 ) / imgWidth;
						float percentY = ( ( docHeight * 100 ) / imgHeight );
						image.scalePercent( percentX, percentY );
						image.setWidthPercentage( 0 );
					} else {
						// no customized scaling is needed for the image if DPI values for x and y are the same
						image.scaleToFit( docWidth, docHeight );
					}
				}
				
				// Add scaled image to document
				destinationDoc.newPage();
				destinationDoc.add( image );
			}
			
			destinationDoc.close();
			
		} catch( ExceptionConverter | DocumentException e ) {
			log.error( e );
			throw new IOException( e.getMessage(), e );
		}
		
		myTiffFile.close();
		ras.close();
		
		log.info( "Java conversion from Tiff to PDF: Completed!" );
	}
	
}
