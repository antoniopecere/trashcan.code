package eu.pecere.utils.filetypedetect.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * @author Antonio Pecere
 *
 */
@RunWith( Suite.class )
@SuiteClasses( {
		FileTypeDetectorTest.class
} )
public class FileTypeDetectTestSuite
{
	private static final Log log = LogFactory.getLog( FileTypeDetectTestSuite.class );
	
	@BeforeClass
	public static void runBeforeClass()
	{
		log.info( "@BeforeClass method: CommonsTestSuite.runBeforeClass()." );
		// Do nothing!
	}
	
	@AfterClass
	public static void runAfterClass()
	{
		// Do nothing!
		log.info( "@BeforeClass method: CommonsTestSuite.runAfterClass()." );
	}
	
}
