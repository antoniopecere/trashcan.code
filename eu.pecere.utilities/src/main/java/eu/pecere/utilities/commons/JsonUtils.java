package eu.pecere.utilities.commons;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to serialize objects into json values and viceversa.<br>
 * <br>
 * Example of usage:
 * 
 * <pre>
 * <code>
 * // Instantiate a class that extends this JsonUtils class,
 * // with the object that you want to serialize as the assigned generic parameter.
 * 
 * JsonUtils&lt;SerializableObject> jsu = new JsonUtils&lt;SerializableObject>() {
 * 	// DECLARING NOTHING!!
 * };
 * 
 * // Than you can use the methods to serialize or to
 * // un-serialize objects of the type set as generic parameter.
 * 
 * SerializableObject[] objectsArray = // ...some initialization ... //;
 * String serialization = jsu.serializeToJson( objectsArray );
 * 
 * List&lt;SerializableObject> objectsList = // ...some initialization ... //;
 * String serialization = jsu.serializeToJson( objectsList );
 * 
 * String jsonObj = // ...some json value... //;
 * SerializableObject object = jsu.loadFromJson( jsonObj );
 * 
 * String jsonObjs = // ...some json value... //;
 * SerializableObject[] objectsArray = jsu.arrayFromJson( jsonObjs );
 * 
 * String jsonObjs = // ...some json value... //;
 * List&lt;SerializableObject> objectsList = jsu.listFromJson( jsonObjs );
 * <code>
 * </pre>
 * 
 * @param <T>
 *            The class type of the object to be serialized.
 * 
 * @author Antonio Pecere: 04 ago 2016
 *
 */
@SuppressWarnings( "unchecked" )
public abstract class JsonUtils<T extends Serializable>
{
	private ObjectMapper mapper;
	
	private Class<T> TClass;
	private T[] TArrayInstance;
	private Class<T[]> TArrayClass;
	
	public JsonUtils()
	{
		this.mapper = new ObjectMapper();
		
		// (new JsonUtils<SerializableObject>(){}).getClass();
		Class<?> inheritingClass = this.getClass();
		
		// JsonUtils<SerializableObject>
		Type thisAsSuperclass = inheritingClass.getGenericSuperclass();
		
		// SerializableObject.class
		Type TType = ( (ParameterizedType) thisAsSuperclass ).getActualTypeArguments()[0];
		
		this.TClass = (Class<T>) TType;
		
		// new SerializableObject[0];
		this.TArrayInstance = (T[]) Array.newInstance( this.TClass, 0 );
		
		// (new SerializableObject[0]).getClass();
		this.TArrayClass = (Class<T[]>) this.TArrayInstance.getClass();
	}
	
	public String serializeToJson( T obj )
	{
		try {
			return this.mapper.writeValueAsString( obj );
		} catch( JsonProcessingException e ) {
			throw new JsonException( e );
		}
	}
	
	public T loadFromJson( String jsonObj )
	{
		try {
			return this.mapper.readValue( jsonObj, this.TClass );
		} catch( IOException e ) {
			throw new JsonException( e );
		}
	}
	
	public String serializeArrayToJson( T[] objs )
	{
		try {
			return this.mapper.writeValueAsString( objs );
		} catch( JsonProcessingException e ) {
			throw new JsonException( e );
		}
	}
	
	public T[] loadArrayFromJson( String jsonObjs )
	{
		try {
			return this.mapper.readValue( jsonObjs, this.TArrayClass );
		} catch( IOException e ) {
			throw new JsonException( e );
		}
	}
	
	public String serializeListToJson( List<T> objList )
	{
		T[] objs = objList.toArray( this.TArrayInstance );
		return this.serializeArrayToJson( objs );
	}
	
	public List<T> loadListFromJson( String jsonObjs )
	{
		T[] objs = this.loadArrayFromJson( jsonObjs );
		return Arrays.asList( objs );
	}
	
}
