package eu.pecere.utils.email.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.junit.Test;

import eu.pecere.utils.commons.FileExtension;
import eu.pecere.utils.commons.ResourceUtils;
import eu.pecere.utils.email.MsgIntoEmlConverter;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class MsgToEmlConverterTest extends EmailMasterTestCase
{
	/**
	 * This test case get an .msg file from path, parse the .msg with messageParser, call the
	 * {@link #MsgIntoEmlConverter.msgInToEmlConverter()} passing it the parsedMessage that will be converted and will
	 * return
	 * a javaMessage and will write it in the same folder of the originary .msg
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMsgIntoEmlConverter() throws Exception
	{
		log.info( "Method testMsgIntoEmlConverter: START!" );
		
		try {
			Properties props = System.getProperties();
			Session session = Session.getInstance( props );
			
			File[] fileMsgsToConvert = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + INPUT_FOLDER_NAME ).listFiles();
			
			String actualFolderAbsolutePath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME;
			log.info( actualFolderAbsolutePath );
			File actualFolder = ResourceUtils.getOrCreateResourceFolder( actualFolderAbsolutePath );
			
			try {
				for( File file : fileMsgsToConvert ) {
					com.auxilii.msgparser.Message parsedMessage = new com.auxilii.msgparser.MsgParser().parseMsg( file );
					javax.mail.Message convertedMessage = MsgIntoEmlConverter.msgIntoEmlConverter( parsedMessage, session );
					String fileName = file.getName().substring( 0, file.getName().lastIndexOf( '.' ) );
					writeMail( convertedMessage, fileName );
				}
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
			
			File[] actualFiles = actualFolder.listFiles();
			Arrays.sort( actualFiles );
			// Retrieve the expected files list from path
			File[] expectedFiles = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + EXPECTED_FOLDER_NAME ).listFiles();
			Arrays.sort( expectedFiles );
			
			assertEquals( expectedFiles.length, actualFiles.length );
			
			for( int i = 0; i < expectedFiles.length; i++ ) {
				log.debug( expectedFiles[i].getName() + ": " + "[" + expectedFiles[i].length() + "], " + actualFiles[i].getName()
						+ "[" +
						actualFiles[i].length() + "]" );
				// assertTrue( expectedFiles[i].length() == actualFiles[i].length() );
				
			}
			for( File file : actualFiles ) {
				file.delete();
			}
			
		} catch( Exception ex ) {
			log.error( ex.toString() );
		}
		
		log.info( "Method testMsgIntoEmlConverter: END!" );
	}
	
	/**
	 * Convenience method to save the obtained .eml objects on file system.
	 * 
	 * @param convertedMessage
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MessagingException
	 */
	private void writeMail( javax.mail.Message convertedMessage, String fileName )
			throws FileNotFoundException, IOException, MessagingException
	{
		fileName = fileName + FileExtension.EML.getMainDotExtension();
		File convertedMailFile = new File( thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + fileName );
		log.info( "JavaMail creation path: " + convertedMailFile.getAbsolutePath() );
		OutputStream ooutputStream = new FileOutputStream( convertedMailFile );
		convertedMessage.writeTo( ooutputStream );
		ooutputStream.close();
	}
	
}
