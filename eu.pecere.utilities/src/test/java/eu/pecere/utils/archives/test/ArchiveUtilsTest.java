package eu.pecere.utils.archives.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import eu.pecere.utils.archives.ArchiveUtils;
import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileExtension;
import eu.pecere.utils.commons.FileUtils;
import eu.pecere.utils.commons.ResourceUtils;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class ArchiveUtilsTest extends ArchiveMasterTestCase
{
	@Test
	public void testUncompress() throws FileNotFoundException, IOException
	{
		log.info( "Method testDecompress(): START!" );
		
		File inputFolder = new File( thisTestFolderAbsolutePath + INPUT_FOLDER_NAME );
		List<File> prova = FileUtils.listFiles( inputFolder, true, FileExtension.ARCHIVE_FILE_EXTENSIONS );
		
		for( File zipFile : prova ) {
			String decompressionFolder = ArchiveUtils.explode( zipFile, false );
			log.info( "Parent directory for decompression: " + decompressionFolder );
			
			List<String> actualNameList = FileUtils.fileNameOrderedList( ExtensionUtils.removeExtension( decompressionFolder ) );
			FileUtils.deleteDirectory( ExtensionUtils.removeExtension( decompressionFolder ) );
			log.info( "Actual file name list: " + actualNameList );
			
			assertEquals( expectedFileNameOrderedList(), actualNameList );
		}
		log.info( "Method testDecompress(): STOP!" );
	}
	
	private List<String> expectedFileNameOrderedList()
	{
		File expectedFileDir = ResourceUtils.getResourceAsFile( thisTestFolderAbsolutePath + EXPECTED_FOLDER_NAME );
		List<String> expectedNameList = FileUtils.fileNameOrderedList( expectedFileDir );
		log.info( "Expected file name list: " + expectedNameList );
		return expectedNameList;
	}
	
}
