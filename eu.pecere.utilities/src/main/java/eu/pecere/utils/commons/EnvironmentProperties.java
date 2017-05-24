package eu.pecere.utils.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to manage properties files.<br>
 * It makes available the System properties and the Environment variables, and furthermore offers the possibility to use
 * into the properties files a syntax with place holders to refers to System properties or the Environment variables.
 * <br>
 * <br>
 * For example into your properties file you can use this declaration:<br>
 * <code>
 * property.name = ${ENV.environment_property_name}
 * </code><br>
 * <br>
 * The program will look into the System properties and the Environment variables for the value assigned to the key
 * "environment_property_name", and use this value to be assigned to the key "property.name".
 * 
 * @author Antonio Pecere
 *
 */
public class EnvironmentProperties extends Properties
{
	private static final long serialVersionUID = 7687172600672819024L;
	
	private static Log log = LogFactory.getLog( EnvironmentProperties.class );
	
	/**
	 * The Properties object for System properties.
	 */
	private Properties system;
	
	/**
	 * The Properties object for Environment variables.
	 */
	private Properties environment;
	
	/**
	 * Creates an instance of EnvironmentProperties already enriched with properties read from System properties and
	 * Environment variables, but without default values.
	 */
	public EnvironmentProperties()
	{
		this( null );
	}
	
	/**
	 * Creates an instance of EnvironmentProperties already enriched with properties read from System properties and
	 * Environment variables, with the specified default values to be used for any keys not found in this instance.
	 * 
	 * @param defaults
	 *            The property list of the [Key,Value] pairs with the defaults values to be used.
	 */
	public EnvironmentProperties( Properties defaults )
	{
		super( defaults );
		this.initSystemProperties();
		this.initEnvironmentProperties();
	}
	
	/**
	 * Initialize the properties retrieved from the System properties.
	 */
	private void initSystemProperties()
	{
		this.system = System.getProperties();
		
		if( log.isDebugEnabled() ) {
			log.debug( "Retrieved system properties:" );
			for( Entry<Object, Object> entry : this.system.entrySet() ) {
				log.debug( "\t" + entry.getKey().toString() + "\t = \t" + entry.getValue().toString() );
			}
		}
	}
	
	/**
	 * Initialize the properties retrieved from the Environment variables.
	 */
	private void initEnvironmentProperties()
	{
		this.environment = new Properties();
		
		if( log.isDebugEnabled() )
			log.debug( "Retrieved environment variables:" );
		
		for( Entry<String, String> entry : System.getenv().entrySet() ) {
			if( log.isDebugEnabled() )
				log.debug( "\t" + entry.getKey() + "\t = \t" + entry.getValue() );
			
			this.environment.setProperty( entry.getKey(), entry.getValue() );
		}
	}
	
	/**
	 * Convenience overload method to load a property file directly from its pathname. It attempts to retrieve the
	 * resource file as a stream, and then invokes the inherited method {@link Properties#load(InputStream)}.
	 * 
	 * @param resourceFilePathname
	 *            The pathname of the resource file to be loaded.
	 * 
	 * @throws IOException
	 *             If the load from the retrieved stream fails.
	 */
	public synchronized void load( String resourceFilePathname ) throws IOException
	{
		InputStream resourceStream = ResourceUtils.getResourceAsStream( resourceFilePathname );
		super.load( resourceStream );
		
		if( log.isDebugEnabled() ) {
			log.debug( "Properties retrieved from properties file:" );
			for( Entry<Object, Object> entry : this.entrySet() ) {
				log.debug( "\t" + entry.getKey().toString() + "\t = \t" + entry.getValue().toString() );
			}
		}
	}
	
	/**
	 * Override of the inherited method. The behavior is modified as follow:<br>
	 * <ul>
	 * <li>Search the value in this Properties object using the specified key.</li>
	 * <li>If the value is not a place holder, it is returned.</li>
	 * <li>If the value is a place holder whit the expected syntax, it looks for the value using the key enclosed in the
	 * place holder.</li>
	 * <li>Then returns the value retrieved, even if it is <code>null</code>.</li>
	 * </ul>
	 * 
	 * @param key
	 *            The key to retrieve the value to look for.
	 * 
	 * @return The value retrieved using the key or <code>null</code> if no value is found.
	 */
	@Override
	public String getProperty( String key )
	{
		if( log.isDebugEnabled() )
			log.debug( "Property Name: " + key );
		
		String value = this.searchValue( key );
		
		if( EnvironmentProperties.isPlaceholder( value ) ) {
			String placeholderKeyName = EnvironmentProperties.getPlaceholderKeyName( value );
			
			if( log.isDebugEnabled() )
				log.debug( "Placeholder Name: " + placeholderKeyName );
			
			value = this.searchValue( placeholderKeyName );
		}
		
		if( log.isDebugEnabled() )
			log.debug( "Property Value: " + value );
		
		return value;
	}
	
	/**
	 * Method to search the value binded to the specified key.
	 * It searches the value into this Properties object eventually using the defaults. If the value hasn't been found,
	 * it searches for the value in the Environment variables, and then in the System properties, and returns the first
	 * value found or null.
	 * 
	 * @param key
	 *            The key to retrieve the value to look for.
	 * 
	 * @return The value retrieved using the key or <code>null</code> if no value is found.
	 */
	private String searchValue( String key )
	{
		// Search into property list and then into defaults.
		String value = super.getProperty( key );
		
		if( log.isDebugEnabled() )
			log.debug( "Found into properties file? " + ( value != null ) );
		
		// If not found and if there's not a default value, search into Environment variables.
		if( value == null ) {
			value = this.environment.getProperty( key );
			
			if( log.isDebugEnabled() )
				log.debug( "Found into environment variables? " + ( value != null ) );
		}
		
		// If not found, search into System properties.
		if( value == null ) {
			value = this.system.getProperty( key );
			
			if( log.isDebugEnabled() )
				log.debug( "Found into system properties? " + ( value != null ) );
		}
		
		// If not found, returns null.
		if( log.isDebugEnabled() && ( value == null ) )
			log.debug( "Property not found!" );
		
		return value;
	}
	
	/**
	 * Check if the value retrieved in this properties file has the expected syntax used to declaring a placeholder.
	 * 
	 * @param value
	 *            The retrieved value to be checked.
	 * 
	 * @return <code>true</code> if the value is a placeholder, <code>false</code> otherwise.
	 */
	private static boolean isPlaceholder( String value )
	{
		if( log.isDebugEnabled() )
			log.debug( "Value to check: " + value );
		
		//@formatter:off
		boolean isPlaceholder =
				value != null &&
				value.trim().startsWith( "${ENV." ) &&
				value.trim().endsWith( "}" );
		//@formatter:on
		
		if( log.isDebugEnabled() )
			log.debug( "Is it a placeholder: " + isPlaceholder );
		
		return isPlaceholder;
	}
	
	/**
	 * Utility method to extract from a place holder with the expected syntax, the name of the key to be used to look
	 * for the value into the System properties and the Environment variables.
	 * 
	 * @param placeholder
	 *            The place holder from which extract the key name.
	 * 
	 * @return The name of the key contained into the place holder.
	 */
	private static String getPlaceholderKeyName( String placeholder )
	{
		if( log.isDebugEnabled() )
			log.debug( "Property Name: " + placeholder );
		
		int beginIndex = 6;
		int endIndex = placeholder.lastIndexOf( "}" );
		String placeholderKeyName = placeholder.substring( beginIndex, endIndex );
		
		if( log.isDebugEnabled() )
			log.debug( "Placeholder Key Name: " + placeholderKeyName );
		
		return placeholderKeyName;
	}
	
	/**
	 * Method to copy a property from this Properties object to a destination Properties object.
	 * 
	 * @param destination
	 *            The destination Properties object to copy the property into.
	 * @param key
	 *            The key of the property to be copied.
	 */
	public void copy( Properties destination, String key )
	{
		destination.setProperty( key, this.getProperty( key ) );
	}
	
	/**
	 * Method to copy all the properties included into this Properties object, to the destination Properties object.
	 * 
	 * @param destination
	 *            The destination Properties object to copy all the properties into.
	 */
	public void copyAll( Properties destination )
	{
		Set<Entry<Object, Object>> coppie = this.entrySet();
		
		for( Entry<Object, Object> entry : coppie ) {
			this.copy( destination, entry.getKey().toString() );
		}
	}
	
	/**
	 * Method to copy a property from a source Properties object to this Properties object.
	 * 
	 * @param source
	 *            The source Properties object to copy the property from.
	 * @param key
	 *            The key of the property to be copied.
	 */
	public void load( Properties source, String key )
	{
		this.setProperty( key, source.getProperty( key ) );
	}
	
	/**
	 * Method to copy all the properties included into a source Properties object, to this Properties object.
	 * 
	 * @param source
	 *            The source Properties object to copy the properties from.
	 */
	public void loadAll( Properties source )
	{
		Set<Entry<Object, Object>> pairs = source.entrySet();
		
		for( Entry<Object, Object> entry : pairs ) {
			this.load( source, entry.getKey().toString() );
		}
	}
	
}