package eu.pecere.utils.commons.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import eu.pecere.utils.commons.FileUtils;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class FileUtilsTest extends CommonsMasterTestCase
{
	@Test
	public void testCommonFolderPathname()
	{
		String[] paths1 = {
				"/home/user1/tmp/coverage/test",
				"/home/user1/tmp/covert/operator",
				"/home/user1/tmp/coven/members"
		};
		
		String actualPath1 = FileUtils.commonFolderPathname( paths1, "/" );
		String expectedPath1 = "/home/user1/tmp/";
		
		assertEquals( expectedPath1, actualPath1 );
		
		String[] paths2 = {
				"C:\\Program Files\\Git\\etc\\ssh",
				"C:\\Program Files\\Java\\jdk1.8.0_74",
				"C:\\Program Files\\Java\\jdk1.6.0_45\\include\\win32"
		};
		
		String actualPath2 = FileUtils.commonFolderPathname( paths2, File.separator );
		String expectedPath2 = "C:\\Program Files\\";
		
		assertEquals( expectedPath2, actualPath2 );
	}
}
