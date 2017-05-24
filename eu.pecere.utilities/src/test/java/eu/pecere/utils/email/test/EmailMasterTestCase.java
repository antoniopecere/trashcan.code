package eu.pecere.utils.email.test;

import java.util.ArrayList;
import java.util.List;

import eu.pecere.utils.email.Mailbox;
import eu.pecere.utils.email.Protocol;
import eu.pecere.utils.test.AbstractTestCase;

/**
 * 
 * @author Antonio Pecere
 *
 */
public abstract class EmailMasterTestCase extends AbstractTestCase
{
	//@formatter:off
	final String jsonMailboxes = "[{"
				+ "\"protocol\":\"imap\","
				+ "\"host\":\"imap.dominio1.it\","
				+ "\"port\":143,"
				+ "\"address\":\"mail1@dominio1.it\","
				+ "\"password\":\"password1\""
			+ "},{"
				+ "\"protocol\":\"imap\","
				+ "\"host\":\"imap.dominio2.it\","
				+ "\"port\":143,"
				+ "\"address\":\"mail2@dominio2.it\","
				+ "\"password\":\"password2\""
			+ "}]";
	//@formatter:on
	
	@SuppressWarnings( "serial" )
	final List<Mailbox> mailboxList = new ArrayList<Mailbox>() {
		{
			Mailbox mailbox1 = new Mailbox();
			mailbox1.setAddress( "mail1@dominio1.it" );
			mailbox1.setHost( "imap.dominio1.it" );
			mailbox1.setPort( 143 );
			mailbox1.setPassword( "password1" );
			mailbox1.setProtocol( Protocol.imap );
			add( mailbox1 );
			
			Mailbox mailbox2 = new Mailbox();
			mailbox2.setAddress( "mail2@dominio2.it" );
			mailbox2.setHost( "imap.dominio2.it" );
			mailbox2.setPort( 143 );
			mailbox2.setPassword( "password2" );
			mailbox2.setProtocol( Protocol.imap );
			add( mailbox2 );
		}
	};
	
	final Mailbox testMailbox = new Mailbox() {
		{
			setHost( "192.168.221.150" );
			setPort( 143 );
			setAddress( "test2@multiserass.com" );
			setPassword( "password" );
			setProtocol( Protocol.imap );
		}
	};
	
	@Override
	protected String getThisTestFolderRootName()
	{
		// Root resource folder relative to tests for eu.pecere.utils.email.test
		return super.getThisTestFolderRootName();
	}
}
