package eu.pecere.utils.email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageUtils
{
	private static boolean TextIsHtml = false;
	
	private static final Log log = LogFactory.getLog( MessageUtils.class );
	
	/**
	 * Return the primary text content of the message.
	 * 
	 * @throws IOException
	 * @throws MessagingException
	 */
	public static String getBodyContent( Part p ) throws MessagingException, IOException
	{
		if( p.isMimeType( "text/*" ) ) {
			String s = (String) p.getContent();
			TextIsHtml = p.isMimeType( "text/html" );
			return s;
		}
		
		if( p.isMimeType( "multipart/alternative" ) ) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for( int i = 0; i < mp.getCount(); i++ ) {
				Part bp = mp.getBodyPart( i );
				if( bp.isMimeType( "text/plain" ) ) {
					if( text == null )
						text = getBodyContent( bp );
					continue;
				} else if( bp.isMimeType( "text/html" ) ) {
					String s = getBodyContent( bp );
					if( s != null )
						return s;
				} else {
					return getBodyContent( bp );
				}
			}
			return text;
		} else if( p.isMimeType( "multipart/*" ) ) {
			Multipart mp = (Multipart) p.getContent();
			for( int i = 0; i < mp.getCount(); i++ ) {
				String s = getBodyContent( mp.getBodyPart( i ) );
				if( s != null )
					return s;
			}
		}
		
		return null;
	}
	
	public static String getBodyText( Message message ) throws IOException, MessagingException
	{
		String bodyContent = null;
		Object objContent = message.getContent();
		if( objContent instanceof String ) {
			// Il testo viene valorizzato in questo blocco quando e' il corpo di una mail senza allegati!
			bodyContent = (String) objContent;
		} else if( objContent instanceof Multipart ) {
			bodyContent = MessageUtils.getBodyContent( message );
		}
		
		// @formatter:off
		boolean isHtmlFormat =
				bodyContent.contains( "<HTML" ) ||
				bodyContent.contains( "<html" ) ||
				bodyContent.contains( "<BODY" ) ||
				bodyContent.contains( "<body" ) ||
				bodyContent.contains( "<DIV" ) ||
				bodyContent.contains( "<div" ) ||
				bodyContent.contains( "<br" ) ||
				bodyContent.contains( "<BR" ) ||
				bodyContent.contains( "<font" ) ||
				bodyContent.contains( "<FONT" );
		// @formatter:on
		
		if( isHtmlFormat ) {
			bodyContent = HtmlUtils.extractText( bodyContent );
		}
		
		bodyContent = cleanText( bodyContent );
		
		return bodyContent;
	}
	
	private static String cleanText( String bodyContent )
	{
		bodyContent = bodyContent.replace( "<![if !supportLists]>", "" ).replace( "<![endif]>", "" );
		bodyContent = bodyContent.replaceAll( "<mailto:.*@.*\\..*>", "" );
		bodyContent = bodyContent.replaceAll( "  +", " " );
		
		if( bodyContent.startsWith( "\r\n\r\n" ) )
			bodyContent = bodyContent.replaceFirst( "\r\n\r\n", "" );
		
		bodyContent = bodyContent.replaceAll( "(( +)?\r?\n)((\\s+)?\r?\n)+", "\r\n\r\n" );
		return bodyContent;
	}
	
	private static String prepareHtml( String bodyContent )
	{
		if( !bodyContent.contains( "<html" ) && !bodyContent.contains( "<body" ) ) {
			bodyContent = bodyContent.replace( "<", "[" ).replace( ">", "]" );
			bodyContent = bodyContent.replace( "\r\n", "<br />" );
			bodyContent = "<html><body>" + bodyContent + "</body></html>";
		}
		return bodyContent;
	}
	
	/**
	 * 
	 * @param args
	 * @param index
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static String parseMessageIdRoot( Message message ) throws MessagingException, UnsupportedEncodingException
	{
		String messageId = MessageUtils.extractMessageId( message );
		return parseMessageIdRoot( messageId );
	}
	
	public static String parseMessageIdRoot( String messageId ) throws MessagingException
	{
		messageId = messageId.replace( "<", "" ).replace( ">", "" ).substring( 0, messageId.indexOf( "@" ) - 1 );
		return messageId;
	}
	
	public static String extractMessageId( Message message ) throws MessagingException, UnsupportedEncodingException
	{
		String id = null;
		String[] idHeaders = message.getHeader( "Message-ID" );
		if( idHeaders != null && idHeaders.length > 0 ) {
			id = idHeaders[0];
		} else {
			String[] addressFrom = toStringAddresses( message.getFrom() );
			String subject = message.getSubject();
			Date sentDate = message.getSentDate();
			String prefix = ( subject == null ? "nessun-oggetto" : subject.replace( " ", "" ).replace( "'", "" ) + "-" );
			String postfix = ( sentDate == null ? new Date() : sentDate ).toString();
			String domain = ( addressFrom == null ? "no-domain"
					: addressFrom[0].substring( addressFrom[0].indexOf( "@" ), addressFrom[0].length() ) );
			id = prefix.concat( postfix ).concat( domain );
			id = id.replace( "/", "-" ).replace( "\\", "-" ).replace( ":", "-" );
		}
		
		return id;
	}
	
	public static String cleanForbiddenChars( String text )
	{
		if( text == null )
			return null;
		
		return text.replaceAll( "[:\\\\/*?|<> \"='\\t]", "_" );
	}
	
	public static String[] toStringAddresses( Address[] addresses ) throws UnsupportedEncodingException
	{
		if( addresses == null )
			return null;
		
		String[] stringAddresses = new String[addresses.length];
		for( int i = 0; i < addresses.length; i++ ) {
			Address address = addresses[i];
			stringAddresses[i] = MimeUtility.decodeText( address.toString() );
		}
		
		return stringAddresses;
	}
	
	public static String[] getAttachmentNames( Message message ) throws MessagingException, IOException
	{
		List<String> attachmentNameList = new ArrayList<>();
		
		String contentType = message.getContentType();
		
		// Check no attachment present.
		if( !contentType.contains( "multipart" ) )
			return null;
		
		Multipart multiPart = (Multipart) message.getContent();
		int numberOfParts = multiPart.getCount();
		for( int partCount = 0; partCount < numberOfParts; partCount++ ) {
			MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart( partCount );
			//@formatter:off
			boolean isAttachmentToAdd = ( Part.ATTACHMENT.equalsIgnoreCase( part.getDisposition() ) ||
										( Part.INLINE.equalsIgnoreCase( part.getDisposition() ) && part.getContentType().contains( "message/rfc822" ) ) );
			//@formatter:on
			if( isAttachmentToAdd ) {
				String fileName = part.getFileName();
				if( fileName != null )
					attachmentNameList.add( MimeUtility.decodeText( fileName ) );
			}
		}
		
		if( attachmentNameList.size() < 1 )
			return null;
		
		return attachmentNameList.toArray( new String[0] );
	}
	
	/**
	 * Convenience overload of method writeMail.
	 *
	 * 
	 * @param message
	 * @param folder
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static File writeMail( javax.mail.Message message, File folder, String filename ) throws IOException
	{
		return MessageUtils.writeMail( message, folder.getAbsolutePath(), filename );
	}
	
	/**
	 * Convenience overload of method writeMail.
	 * 
	 * @param message
	 * @param folderPathname
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static File writeMail( javax.mail.Message message, String folderPathname, String filename ) throws IOException
	{
		return MessageUtils.writeMail( message, folderPathname + File.separator + filename );
	}
	
	/**
	 * Convenience method to save an .eml object on file system.
	 * 
	 * @param message
	 * @param filePathname
	 * @return
	 * @throws IOException
	 */
	public static File writeMail( javax.mail.Message message, String filePathname ) throws IOException
	{
		File writtenIntoFolderFile = new File( filePathname );
		log.info( "JavaMail creation path: " + writtenIntoFolderFile.getAbsolutePath() );
		
		try( OutputStream outputStream = new FileOutputStream( writtenIntoFolderFile ); ) {
			message.writeTo( outputStream );
		} catch( MessagingException e ) {
			writtenIntoFolderFile.delete();
			throw new IOException( e );
		}
		
		return writtenIntoFolderFile;
	}
	
}
