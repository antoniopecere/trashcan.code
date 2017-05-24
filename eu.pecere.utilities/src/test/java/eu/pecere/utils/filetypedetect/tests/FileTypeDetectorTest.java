package eu.pecere.utils.filetypedetect.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.ResourceUtils;
import eu.pecere.utils.filetypedetect.FileTypeDetector;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class FileTypeDetectorTest extends FileTypeDetectMasterTestCase
{
	@Test
	public void testFileTypeDetection() throws Exception
	{
		File testDirectory = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath );
		File[] files = testDirectory.listFiles();
		
		List<String> paths = new ArrayList<String>();
		for( File file : files ) {
			paths.add( file.getAbsolutePath() );
		}
		
		for( String path : paths ) {
			InputStream inputStream = new FileInputStream( path );
			log.info( path.substring( path.lastIndexOf( File.separator ) + 1 ) + ": " );
			FileTypeDetector detector = new FileTypeDetector( inputStream );
			String actualExtension = detector.getExtension();
			String expectedExtension = ExtensionUtils.extractExtension( path ).getMainExtension();
			assertEquals( expectedExtension, actualExtension );
		}
		
	}
}
