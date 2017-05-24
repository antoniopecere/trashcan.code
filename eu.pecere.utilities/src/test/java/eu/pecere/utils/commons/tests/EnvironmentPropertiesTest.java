package eu.pecere.utils.commons.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import eu.pecere.utils.commons.EnvironmentProperties;
import eu.pecere.utils.commons.ResourceUtils;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class EnvironmentPropertiesTest extends CommonsMasterTestCase
{
	private static final String PROPERTY_FILE = "test.properties";
	
	private static final String PROPERTY_KEY_1 = "mia.property.1";
	private static final String PROPERTY_KEY_2 = "mia.property.2";
	private static final String PROPERTY_KEY_3 = "mia.property.3";
	
	@Test
	public void testEnvironmentProperties() throws IOException
	{
		Properties properties = new EnvironmentProperties();
		properties.load( ResourceUtils.getResourceAsStream( thisTestFolderPartialPath + PROPERTY_FILE ) );
		
		String value1 = properties.getProperty( PROPERTY_KEY_1 );
		String value2 = properties.getProperty( PROPERTY_KEY_2 );
		String value3 = properties.getProperty( PROPERTY_KEY_3 );
		
		log.debug( PROPERTY_KEY_1 + ": " + value1 );
		log.debug( PROPERTY_KEY_2 + ": " + value2 );
		log.debug( PROPERTY_KEY_3 + ": " + value3 );
		
		assertEquals( value1 + File.separator, value2 );
		assertEquals( value1, value3 );
	}
}
