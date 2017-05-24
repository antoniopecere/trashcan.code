package eu.pecere.utils.pdf.conversion;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import eu.pecere.utils.commons.FileExtension;

/**
 * @author Antonio Pecere
 * 
 */
public class PdfReducer
{
	private static float FACTOR = 0.5f;
	private static final Log log = LogFactory.getLog( PdfReducer.class );
	
	/**
	 * This utility method reduce the pdf file size from a string path and write the reduced pdf to the passed path.
	 * 
	 * @param srcPath
	 * @param destPath
	 * @throws Exception
	 */
	public static void reducePdf( String srcPath, String destPath ) throws Exception
	{
		log.info( "Method reducePdf(String,String): START!" );
		
		InputStream inputStream = new FileInputStream( srcPath );
		OutputStream outputStream = new FileOutputStream( destPath );
		PdfReducer.reducePdf( inputStream, outputStream );
		
		log.info( "Method reducePdf(String,String): STOP!" );
	}
	
	public static void reducePdf( InputStream inputStream, OutputStream outputStream ) throws Exception
	{
		log.info( "Method reducePdf(InputStream,OutputStream): START!" );
		
		PdfName key = new PdfName( "ITXT_SpecialId" );
		PdfName value = new PdfName( "123456789" );
		
		// Read the file
		PdfReader reader = new PdfReader( inputStream );
		int n = reader.getXrefSize();
		
		PdfObject object;
		PRStream stream;
		
		// Look for image and manipulate image stream
		for( int i = 0; i < n; i++ ) {
			
			object = reader.getPdfObject( i );
			if( object == null || !object.isStream() )
				continue;
			
			stream = (PRStream) object;
			// if (value.equals(stream.get(key))) {
			PdfObject pdfsubtype = stream.get( PdfName.SUBTYPE );
			
			try {
				if( pdfsubtype != null && pdfsubtype.toString().equals( PdfName.IMAGE.toString() ) ) {
					
					PdfImageObject image = new PdfImageObject( stream );
					BufferedImage bi = image.getBufferedImage();
					
					if( bi == null )
						continue;
					
					int width = (int) ( bi.getWidth() * FACTOR );
					int height = (int) ( bi.getHeight() * FACTOR );
					
					BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
					AffineTransform at = AffineTransform.getScaleInstance( FACTOR, FACTOR );
					
					Graphics2D g = img.createGraphics();
					g.drawRenderedImage( bi, at );
					
					ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
					ImageIO.write( img, FileExtension.JPG.getMainExtension(), imgBytes );
					
					stream.clear();
					stream.setData( imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION );
					stream.put( PdfName.TYPE, PdfName.XOBJECT );
					stream.put( PdfName.SUBTYPE, PdfName.IMAGE );
					stream.put( key, value );
					stream.put( PdfName.FILTER, PdfName.DCTDECODE );
					stream.put( PdfName.WIDTH, new PdfNumber( width ) );
					stream.put( PdfName.HEIGHT, new PdfNumber( height ) );
					stream.put( PdfName.BITSPERCOMPONENT, new PdfNumber( 8 ) );
					stream.put( PdfName.COLORSPACE, PdfName.DEVICERGB );
				}
			} catch( Exception e ) {
				throw new Exception( "Errore compressione: " + e );
			}
		}
		
		// Save altered PDF
		PdfStamper stamper = new PdfStamper( reader, outputStream );
		stamper.close();
		reader.close();
		
		log.info( "Method reducePdf(InputStream,OutputStream): STOP!" );
	}
	
	/**
	 * Return the reduced file size afeter calling the method {@link #reducePdf(String, String)} to reduce the file size
	 * 
	 * @param filePathIn
	 * @param filePathRed
	 * @param filePathOut
	 * @return
	 * @throws Exception
	 */
	public static String getReducedFileUrl( String filePathIn, String filePathRed, String filePathOut ) throws Exception
	{
		log.info( "Method getReducedFileUrl: START!" );
		// createPdf(RESULT);
		PdfReducer.reducePdf( filePathIn, filePathRed );
		PdfReader reader = new PdfReader( filePathRed );
		PdfStamper stamper = new PdfStamper( reader, new FileOutputStream( filePathOut ), PdfWriter.VERSION_1_5 );
		stamper.setFullCompression();
		stamper.close();
		reader.close();
		File fileRed = new File( filePathRed );
		FileUtils.forceDelete( fileRed );
		
		log.info( "Method getReducedFileUrl: STOP!" );
		return filePathOut;
	}
	
}
