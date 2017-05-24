package eu.pecere.utils.email.sender;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMessageImpl implements SendMessage
{
	private static Properties properties = System.getProperties();
	private Session session;
	
	public SendMessageImpl( String host, String port, boolean auth )
	{
		properties.setProperty( "mail.smtp.host", host );
		properties.setProperty( "mail.smtp.port", port );
		if( auth )
			throw new UnsupportedOperationException();
		session = Session.getDefaultInstance( properties );
	}
	
	@Override
	public void message( String mailFrom, String[] mailTo, String subject, String body, String[] attach )
	{
		try {
			Message message = setMessage( mailFrom, mailTo, subject, body, attach );
			Transport.send( message );
		} catch( Exception e ) {
			throw new SendMailException( e );
		}
	}
	
	private Message setMessage( String from, String[] to, String subject, String body, String[] attach )
			throws AddressException, MessagingException, UnsupportedEncodingException
	{
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap( "text/html;; x-java-content-handler=com.sun.mail.handlers.text_html" );
		mc.addMailcap( "text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml" );
		mc.addMailcap( "text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain" );
		mc.addMailcap( "multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed" );
		mc.addMailcap( "message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822" );
		
		Message result = new MimeMessage( this.session );
		result.setFrom( new InternetAddress( from ) );
		for( String mail : to ) {
			result.addRecipient( Message.RecipientType.TO,
					new InternetAddress( SendMailUtil.getAddress( mail ), SendMailUtil.getName( mail ) ) );
		}
		result.setSubject( subject );
		BodyPart b = new MimeBodyPart();
		b.setText( body );
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart( b );
		if( attach != null && attach.length > 0 )
			for( String filename : attach ) {
				if( filename != null && !"".equals( filename ) ) {
					b = new MimeBodyPart();
					DataSource source = new FileDataSource( filename );
					b.setDataHandler( new DataHandler( source ) );
					b.setFileName( new File( filename ).getName() );
					multipart.addBodyPart( b );
				}
			}
		result.setContent( multipart );
		return result;
	}
	
}
