package eu.pecere.utils.email;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class ErrorMailboxUtils
{
	public static ErrorMailbox loadFromJson( String jsonMailbox ) throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue( jsonMailbox, ErrorMailbox.class );
	}
	
	public static ErrorMailbox[] arrayFromJson( String jsonMailboxes ) throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue( jsonMailboxes, ErrorMailbox[].class );
	}
}
