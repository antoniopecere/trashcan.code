package eu.pecere.utils.commons.tests;

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
		EnvironmentPropertiesTest.class,
		FileUtilsTest.class,
		ProcessUtilsTest.class,
		ResourceUtilsTest.class,
		StreamConverterTest.class,
		P7MextractorTest.class
} )
public class CommonsTestSuite
{
	private static final Log log = LogFactory.getLog( CommonsTestSuite.class );
	
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
