package eu.pecere.utils.email.test;

import static org.junit.Assert.fail;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.junit.Test;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class StoreManagerTest extends EmailMasterTestCase
{
	@Test
	public void testStoreManager()
	{
		try {
			Properties props = System.getProperties();
			
			Session session = Session.getInstance( props );
			Store store = session.getStore( testMailbox.getProtocol().name() );
			
			store.connect( testMailbox.getHost(), testMailbox.getAddress(), testMailbox.getPassword() );
			
			Folder folder = store.getDefaultFolder();
			if( folder != null ) {
				folder = folder.getFolder( "INBOX" );
				if( folder != null ) {
					folder.open( Folder.READ_ONLY );
					Message[] elencoMessaggi = folder.getMessages();
					
					int max = elencoMessaggi.length > 10 ? 10 : elencoMessaggi.length;
					
					for( int indice = 0; indice < max; indice++ ) {
						Message messaggio = elencoMessaggi[indice];
						InternetAddress fromAddress = (InternetAddress) messaggio.getFrom()[0];
						String from = fromAddress.getPersonal();
						if( from == null ) {
							from = fromAddress.toString();
						}
						log.info( "DA:" + from +
								"\r\nOGGETTO: " + messaggio.getSubject() +
								"\r\nDATA: " + messaggio.getSentDate() +
								"\r\n" );
					}
					folder.close( false );
				} else {
					log.info( "Folder non trovato" );
				}
			} else {
				log.info( "Folder di default non trovato" );
			}
			store.close();
		} catch( Exception e ) {
			log.error( e );
			fail( e.getMessage() );
		}
	}
}
