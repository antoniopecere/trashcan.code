package eu.pecere.utils.pdf.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import eu.pecere.utils.commons.ResourceUtils;
import eu.pecere.utils.pdf.conversion.PdfReducer;

/**
 * @author Antonio Pecere
 * 
 */
public class PdfReducerTest extends PdfMasterTestCase
{
	private static final Log log = LogFactory.getLog( PdfReducerTest.class );
	
	@Test
	public void testPdfReducer() throws IOException
	{
		log.info( "Method testPdfReducer: START!" );
		
		String actualFolderAbsolutePath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME;
		log.info( actualFolderAbsolutePath );
		File actualFolder = ResourceUtils.getOrCreateResourceFolder( actualFolderAbsolutePath );
		
		// Retrieve the files list from the input directory
		File[] inputFiles = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + INPUT_FOLDER_NAME ).listFiles();
		
		log.info( "Input files number: " + inputFiles.length );
		
		for( File file : inputFiles ) {
			try {
				InputStream inputStream = new FileInputStream( file );
				File reducedFile = new File( actualFolderAbsolutePath + file.getName() );
				OutputStream outputStream = new FileOutputStream( reducedFile );
				
				PdfReducer.reducePdf( inputStream, outputStream );
				
				long sizeFileIn = file.length();
				long sizeFileOut = reducedFile.length();
				
				log.info( "FileIn: " + sizeFileIn + " - FileOut: " + sizeFileOut );
				// assertTrue( sizeFileOut <= sizeFileIn );
			} catch( Exception e ) {
				log.error( "Compression file error: " + file.getName() + " Exception: " + e );
			}
		}
		
		// Retrieve the compressed files list
		File[] actualFiles = actualFolder.listFiles();
		Arrays.sort( actualFiles );
		
		// Retrieve the expected files list from path
		File[] expectedFiles = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + EXPECTED_FOLDER_NAME ).listFiles();
		Arrays.sort( expectedFiles );
		
		assertEquals( expectedFiles.length, actualFiles.length );
		
		for( int i = 0; i < expectedFiles.length; i++ ) {
			log.debug( expectedFiles[i].getName() + ": " + "[" + expectedFiles[i].length() + ", " + actualFiles[i].length() + "]" );
			assertTrue( expectedFiles[i].length() == actualFiles[i].length() );
			actualFiles[i].delete();
		}
		
		log.info( "Method testPdfReducer: STOP!" );
	}
	
}
