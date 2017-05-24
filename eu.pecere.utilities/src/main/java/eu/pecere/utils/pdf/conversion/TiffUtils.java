package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

public class TiffUtils
{
	private static final Log log = LogFactory.getLog( TiffUtils.class );
	
	public static Integer extractTotalNumberOfPages( InputStream inputStream ) throws IOException
	{
		log.info( "Extraction of pages from Tiff file: Started!" );
		
		RandomAccessSourceFactory rasFactory = new RandomAccessSourceFactory();
		RandomAccessSource ras = rasFactory.createSource( inputStream );
		
		RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray( ras );
		
		Integer numberOfPages = TiffImage.getNumberOfPages( myTiffFile );
		log.info( "Number of Images in Tiff File " + numberOfPages );
		
		myTiffFile.close();
		ras.close();
		
		log.info( "Extraction of pages from Tiff file: Completed!" );
		return numberOfPages;
	}
	
	public static Integer extractTotalNumberOfPages( File inputFile ) throws IOException
	{
		try( InputStream inputStream = new FileInputStream( inputFile ) ) {
			return TiffUtils.extractTotalNumberOfPages( inputStream );
		} catch( IOException e ) {
			throw e;
		}
	}
	
	public static Integer extractTotalNumberOfPages( String inputFilePathname ) throws IOException
	{
		File inputFile = new File( inputFilePathname );
		return TiffUtils.extractTotalNumberOfPages( inputFile );
	}
}
