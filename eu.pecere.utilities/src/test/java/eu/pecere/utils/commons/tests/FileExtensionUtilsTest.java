package eu.pecere.utils.commons.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import eu.pecere.utils.commons.FileExtension;

public class FileExtensionUtilsTest extends CommonsMasterTestCase
{
	private static final Log log = LogFactory.getLog( FileExtensionUtilsTest.class );
	
	@Test
	public void test()
	{
		for( FileExtension extension : FileExtension.values() ) {
			log.info( extension );
		}
	}
}
