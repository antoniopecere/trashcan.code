package eu.pecere.utils.email;

import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class StoreManager
{
	private static Log log = LogFactory.getLog( StoreManager.class );
	
	private Mailbox mailbox;
	private Store store;
	private Folder folder;
	
	private String folderSeparator;
	
	public StoreManager( Mailbox mailbox )
	{
		this.mailbox = mailbox;
	}
	
	public void open() throws NoSuchProviderException, MessagingException
	{
		String protocol = this.mailbox.getProtocol().name();
		boolean ssl = this.mailbox.isSsl();
		boolean pec = this.mailbox.isPec();
		String host = this.mailbox.getHost();
		Integer port = this.mailbox.getPort();
		String address = this.mailbox.getAddress();
		String password = this.mailbox.getPassword();
		
		Properties props = System.getProperties();
		
		if( ssl ) {
			props.setProperty( "mail.imap.ssl.enable", "true" );
			props.setProperty( "mail.imap.ssl.socketFactory.class", "com.msa.utils.email.DummySSLSocketFactory" );
			props.setProperty( "mail.imap.ssl.socketFactory.fallback", "false" );
		} else {
			props.setProperty( "mail.imap.ssl.enable", "false" );
		}
		
		Session session = Session.getInstance( props );
		this.store = session.getStore( protocol );
		
		log.info( "Try to connect!" );
		
		if( log.isDebugEnabled() ) {
			log.debug( "store.connect( "
					+ "host " + ( host != null ? "= |" + host + "|" : "IS NULL" )
					+ ", username " + ( address != null ? "= |" + address + "|" : "IS NULL" )
					+ ", password " + ( password != null ? "= |" + password + "|" : "IS NULL" )
					+ " )" );
		}
		
		this.store.connect( host, port, address, password );
		
		log.info( "Connection enstablished!" );
	}
	
	public Store getStore()
	{
		return this.store;
	}
	
	private void initFolder( String folderName, int openMode ) throws MessagingException
	{
		if( log.isDebugEnabled() )
			log.debug( "store.getFolder(folderName = " + ( folderName != null ? "|" + folderName + "|" : "IS NULL" ) + ")" );
		this.folder = this.store.getFolder( folderName );
		
		if( log.isDebugEnabled() )
			log.debug( "folder.open(Folder.mode = " + openMode + ")" );
		this.folder.open( openMode );
		
		this.folderSeparator = this.folder.getSeparator() + "";
	}
	
	public Message[] getMessages( String folderName, int openMode ) throws MessagingException
	{
		this.initFolder( folderName, openMode );
		Message[] messages = this.folder.getMessages();
		FetchProfile fp = new FetchProfile();
		fp.add( FetchProfile.Item.ENVELOPE );
		fp.add( "X-mailer" );
		this.folder.fetch( messages, fp );
		return messages;
	}
	
	public void close()
	{
		try {
			if( this.folder != null )
				this.folder.close( true );
			if( this.folder != null )
				this.store.close();
		} catch( Exception e ) {
			log.error( e );
			this.folder = null;
			this.store = null;
		}
	}
	
	public void moveMessage( Message message, String destinationFolderName, Boolean delete )
	{
		log.debug( "Executing class: " + StoreManager.class.getSimpleName() );
		
		if( message == null )
			return;
		
		try {
			Folder destinationFolder = this.folder.getFolder( destinationFolderName );
			log.info( "Retrieving destination folder : " + destinationFolderName );
			
			if( !destinationFolder.exists() ) {
				boolean isCreated = createFolder( destinationFolder );
				log.info( "Unexisting destination folder has " + ( isCreated ? "" : "NOT " ) + "been created" );
			}
			
			if( !destinationFolder.isOpen() ) {
				destinationFolder.open( Folder.READ_WRITE );
			}
			
			int initialMessageCount = destinationFolder.getMessageCount();
			if( !destinationFolder.isSubscribed() ) {
				destinationFolder.setSubscribed( true );
			}
			
			this.folder.copyMessages( new Message[] { message }, destinationFolder );
			log.info( "The message has been copied succesfully to destination folder :" + destinationFolder.getFullName() );
			
			if( delete ) {
				message.setFlag( Flag.DELETED, true );
				log.info( "Old message deleted succesfully from source folder: " + this.folder.getFullName() );
			}
			
			this.folder.expunge();
			
			// TODO: Questo while dovrebbe trasformarsi in un Thread, vedi:
			// http://codereview.stackexchange.com/questions/56403/wait-for-messages-in-imap-gmail-mailbox
			int finalMessageCount = 0;
			while( ( finalMessageCount = destinationFolder.getMessageCount() ) == initialMessageCount ) {
				Thread.sleep( 1000 );
			}
			
			Message movedMessage = destinationFolder.getMessage( finalMessageCount );
			movedMessage.setFlag( Flag.SEEN, false );
			
			if( destinationFolder.isOpen() )
				destinationFolder.close( false );
			
		} catch( MessagingException | InterruptedException e ) {
			log.error( "An error occurred while moving the message", e );
			
		}
		
		log.debug( "Executing class: " + StoreManager.class.getSimpleName() );
	}
	
	private boolean createFolder( Folder newFolder )
	{
		boolean isCreated = true;
		
		try {
			isCreated = newFolder.create( Folder.HOLDS_MESSAGES );
			log.info( "created: " + isCreated );
		} catch( Exception e ) {
			log.error( "Error creating folder: " + e.getMessage() );
			e.printStackTrace();
			isCreated = false;
		}
		
		return isCreated;
	}
	
	public String getFolderSeparator()
	{
		return this.folderSeparator;
	}
	
}
