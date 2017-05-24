package eu.pecere.utils.archives.test;

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
		ArchiveUtilsTest.class } )
public class ArchiveTestSuite
{
	private static final Log log = LogFactory.getLog( ArchiveTestSuite.class );
	
	@BeforeClass
	public static void runBeforeClass()
	{
		log.info( "@BeforeClass method: ArchiveTestSuite.runBeforeClass()." );
		// Do nothing!
	}
	
	@AfterClass
	public static void runAfterClass()
	{
		// Do nothing!
		log.info( "@BeforeClass method: ArchiveTestSuite.runAfterClass()." );
	}
	
}
