/**
 * 
 */
package eu.pecere.utils.commons.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileUtils;
import eu.pecere.utils.commons.P7Mextractor;
import eu.pecere.utils.commons.ResourceUtils;

/**
 * @author Antonio Pecere
 *
 */
public class P7MextractorTest extends CommonsMasterTestCase
{
	@Test
	public void testP7Mextractor() throws IOException
	{
		log.info( "Method testP7Mextractor: START!" );
		
		String inputFolderPath = thisTestFolderAbsolutePath + INPUT_FOLDER_NAME;
		String actualFolderPath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME;
		String expectedFolderPath = thisTestFolderAbsolutePath + EXPECTED_FOLDER_NAME;
		
		log.info( "Input folder path: " + inputFolderPath );
		List<File> inputFolderList = FileUtils.unfilteredListFiles( new File( inputFolderPath ), true );
		
		for( File inputFile : inputFolderList ) {
			String outputFilePathname = actualFolderPath + ExtensionUtils.removeExtension( inputFile.getName() );
			File outputFile = new File( outputFilePathname );
			long start = ( new Date() ).getTime();
			P7Mextractor.p7mExtractor( inputFile, outputFile );
			long stop = ( new Date() ).getTime();
			log.debug( "Extracted file: " + inputFile.getName() + " Time spent: " + ( stop - start ) );
		}
		
		List<File> expectedList = FileUtils.unfilteredListFiles( expectedFolderPath, true );
		List<File> actualList = FileUtils.unfilteredListFiles( new File( actualFolderPath ), true );
		
		log.debug( "Actual collection size:" + actualList.size() + " Expected collection size: " + expectedList.size() );
		assertEquals( actualList.size(), expectedList.size() );
		
		File[] arrayExpected = ResourceUtils.getResourceAsFile( thisTestFolderAbsolutePath + EXPECTED_FOLDER_NAME ).listFiles();
		File[] arrayActual = ResourceUtils.getResourceAsFile( thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME ).listFiles();
		
		for( int i = 0; i < arrayExpected.length; i++ ) {
			log.debug( arrayExpected[i].getName() + ": " + "[" + arrayExpected[i].length() + "], " + arrayActual[i].getName()
					+ "[" + arrayActual[i].length() + "]" );
			
			assertTrue( arrayExpected[i].length() == arrayActual[i].length() );
			
		}
		log.info( "Method testP7Mextractor: STOP!" );
	}
}
