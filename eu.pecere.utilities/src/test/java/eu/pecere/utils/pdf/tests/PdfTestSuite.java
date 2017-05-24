package eu.pecere.utils.pdf.tests;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eu.pecere.utils.pdf.libreoffice.LibreOfficeStreamConverter;

/**
 * 
 * @author Antonio Pecere
 *
 */
@RunWith( Suite.class )
@SuiteClasses( {
		LibreOfficeFilterNamesTest.class,
		PdfUtilsTest.class,
		PdfReducerTest.class } )

public class PdfTestSuite
{
	private static final Log log = LogFactory.getLog( PdfTestSuite.class );
	
	@BeforeClass
	public static void runBeforeClass()
	{
		// Do nothing!
		log.info( "@BeforeClass method: PdfUtilsTestSuite.runBeforeClass()." );
	}
	
	@AfterClass
	public static void runAfterClass()
	{
		log.info( "@BeforeClass method: PdfUtilsTestSuite.runAfterClass()." );
		try {
			LibreOfficeStreamConverter.getInstance().reset();
		} catch( IOException e ) {
			// Do nothing!
		}
	}
	
}
