package eu.pecere.utils.email.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import eu.pecere.utils.commons.JsonUtils;
import eu.pecere.utils.email.Mailbox;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class MailboxUtilsTestCase extends EmailMasterTestCase
{
	@Test
	public void testJsonWrite() throws Exception
	{
		log.info( "Expected json: " + this.jsonMailboxes );
		
		JsonUtils<Mailbox> jsu = new JsonUtils<Mailbox>() {
			// DECLARING NOTHING!!
		};
		
		String serializedJson = jsu.serializeListToJson( mailboxList );
		log.info( "Actual json: " + serializedJson );
		
		assertEquals( this.jsonMailboxes, serializedJson );
	}
	
	@Test
	public void testJsonRead() throws Exception
	{
		log.info( "Expected mailboxes list: " + this.mailboxList );
		
		JsonUtils<Mailbox> jsu = new JsonUtils<Mailbox>() {
			// DECLARING NOTHING!!
		};
		
		List<Mailbox> loadedMailboxes = jsu.loadListFromJson( jsonMailboxes );
		log.info( "Actual mailboxes list: " + loadedMailboxes );
		
		assertEquals( this.mailboxList.size(), loadedMailboxes.size() );
		assertEquals( this.mailboxList, loadedMailboxes );
	}
}
