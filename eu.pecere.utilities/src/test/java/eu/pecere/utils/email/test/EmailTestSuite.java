package eu.pecere.utils.email.test;

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
		MailboxUtilsTestCase.class,
		StoreManagerTest.class } ) // ,
// MsgToEmlConverterTest.class } )

public class EmailTestSuite
{
	private static final Log log = LogFactory.getLog( EmailTestSuite.class );
	
	@BeforeClass
	public static void runBeforeClass()
	{
		log.info( "@BeforeClass method: EmailTestSuite.runBeforeClass()." );
		// Do nothing!
	}
	
	@AfterClass
	public static void runAfterClass()
	{
		// Do nothing!
		log.info( "@BeforeClass method: EmailTestSuite.runAfterClass()." );
	}
	
}
