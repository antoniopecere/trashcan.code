package eu.pecere.utils.email;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auxilii.msgparser.Message;
import com.auxilii.msgparser.RecipientEntry;
import com.auxilii.msgparser.attachment.Attachment;
import com.auxilii.msgparser.attachment.FileAttachment;
import com.auxilii.msgparser.attachment.MsgAttachment;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileExtension;
import eu.pecere.utils.commons.FileUtils;
import eu.pecere.utils.commons.StreamConverter;

/**
 * @author Antonio Pecere
 *
 */
public class MsgIntoEmlConverter
{
	private static final Log log = LogFactory.getLog( MsgIntoEmlConverter.class );
	
	/**
	 * Override of the method {@link #msgIntoEmlConverter(MimeBodyPart, Session)}
	 * This method is used to convert recursively the .msg inside the first body part received.
	 * 
	 * @see http://auxilii.com/msgparser/javadoc/
	 * @param bodyPart
	 * @param session
	 * @return javax.mail.Message convertedMessage
	 * @throws Exception
	 */
	public static javax.mail.Message msgIntoEmlConverter( File file, Session session )
			throws Exception
	{
		log.info( "Method msgIntoEmlConverter( File file, Session session ): START!" );
		javax.mail.Message convertedMessage;
		try( InputStream is = new FileInputStream( file ); ) {
			com.auxilii.msgparser.Message parsedMessage = new com.auxilii.msgparser.MsgParser().parseMsg( is );
			convertedMessage = new MimeMessage( session );
			convertMessage( parsedMessage, convertedMessage );
		} catch( Exception e ) {
			log.error( e.getMessage(), e );
			throw e;
		}
		log.info( "Method msgIntoEmlConverter( File file, Session session ): STOP!" );
		return convertedMessage;
	}
	
	/**
	 * Override of the method {@link #msgIntoEmlConverter(MimeBodyPart, Session)}
	 * This method is used to convert recursively the .msg inside the first body part received.
	 * 
	 * @see http://auxilii.com/msgparser/javadoc/
	 * @param com.auxilii.msgparser.Message
	 * @param session
	 * @return javax.mail.Message convertedMessage
	 * @throws Exception
	 */
	public static javax.mail.Message msgIntoEmlConverter( com.auxilii.msgparser.Message parsedMessage, Session session )
			throws Exception
	{
		log.info( "Method msgIntoEmlConverter(parsedMessage, session): START!" );
		javax.mail.Message convertedMessage = new MimeMessage( session );
		convertMessage( parsedMessage, convertedMessage );
		log.info( "Method msgIntoEmlConverter(parsedMessage, session): STOP!" );
		return convertedMessage;
	}
	
	/**
	 * @param parsedMessage
	 * @param convertedMessage
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 * @throws Exception
	 */
	private static void convertMessage( com.auxilii.msgparser.Message parsedMessage, javax.mail.Message convertedMessage )
			throws MessagingException, UnsupportedEncodingException, AddressException, Exception
	{
		setHeadersFromParsedMessage( parsedMessage, convertedMessage );
		setMultipartContent( parsedMessage, convertedMessage );
	}
	
	/**
	 * This method set the new java message headers from the parsed message header
	 * 
	 * @param parsedMessage
	 * @param convertedMessage
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 */
	@SuppressWarnings( "unchecked" )
	private static void setHeadersFromParsedMessage( com.auxilii.msgparser.Message parsedMessage, javax.mail.Message convertedMessage )
			throws MessagingException, UnsupportedEncodingException, AddressException
	{
		log.info( "Method setHeadersFromParsedMessage: START!" );
		if( parsedMessage.getHeaders() != null ) {
			
			InternetHeaders newHeaders = new InternetHeaders( new ByteArrayInputStream( parsedMessage.getHeaders().getBytes() ) );
			newHeaders.removeHeader( "Content-Type" );
			Enumeration<Header> allHeaders = newHeaders.getAllHeaders();
			while( allHeaders.hasMoreElements() ) {
				Header header = allHeaders.nextElement();
				convertedMessage.addHeader( header.getName(), header.getValue() );
			}
			
		} else {
			
			convertedMessage.setSubject( parsedMessage.getSubject() );
			convertedMessage.setSentDate( parsedMessage.getClientSubmitTime() );
			
			InternetAddress fromMailbox = new InternetAddress();
			fromMailbox.setAddress( parsedMessage.getFromEmail() );
			if( parsedMessage.getFromName() != null && parsedMessage.getFromName().length() > 0 ) {
				fromMailbox.setPersonal( parsedMessage.getFromName() );
			} else {
				fromMailbox.setPersonal( parsedMessage.getFromEmail() );
			}
			
			convertedMessage.setFrom( fromMailbox );
			List<RecipientEntry> listToConvert = parsedMessage.getRecipients();
			Address[] recipientArray = recipientListToAddressesArrayConverter( listToConvert );
			convertedMessage.setRecipients( RecipientType.TO, recipientArray );
			
			//@formatter:off
			boolean validCcRecipients = parsedMessage.getCcRecipients() != null && parsedMessage.getCcRecipients().size() > 0;
			//@formatter:on
			if( validCcRecipients ) {
				Address[] ccRecipientsArray = recipientListToAddressesArrayConverter( parsedMessage.getCcRecipients() );
				convertedMessage.setRecipients( RecipientType.CC, ccRecipientsArray );
			}
			//@formatter:off
			boolean validBccRecipients =
					parsedMessage.getBccRecipients() != null
					&& parsedMessage.getBccRecipients().size() > 0;
			//@formatter:on
			if( validBccRecipients ) {
				Address[] bccRecipientsArray = recipientListToAddressesArrayConverter( parsedMessage.getBccRecipients() );
				convertedMessage.setRecipients( RecipientType.BCC, bccRecipientsArray );
			}
		}
		log.info( "Method setHeadersFromParsedMessage: STOP!" );
	}
	
	/**
	 * This method receive a parsedMessage from {@link #msgIntoEmlConverter(com.auxilii.msgparser.Message, Session)},
	 * get the attachments list from parsedMessage and the message's body content and convert them into Java mail format
	 * adding them to the MultiPart content that will be returned to the caller method
	 * {@link #msgIntoEmlConverter(com.auxilii.msgparser.Message, Session)}
	 * and setted as content of the new Java Mail Message
	 * 
	 * @param parsedMessage
	 * @return Multipart content composed by the message Body (TXT or HTML) and the attachments
	 * @throws Exception
	 * @see {@link #getBodyPartList(List)}
	 */
	private static void setMultipartContent( com.auxilii.msgparser.Message parsedMessage, javax.mail.Message convertedMessage )
			throws Exception
	{
		log.info( "Method getAttachmentsMultiPart: START!" );
		
		Multipart mixedContent = new MimeMultipart( "mixed" );
		
		setContent: {
			Multipart multipartAlternative = new MimeMultipart( "alternative" );
			BodyPart mbpIntoContent = new MimeBodyPart();
			mixedContent.addBodyPart( mbpIntoContent );
			mbpIntoContent.setContent( multipartAlternative );
			
			setText: {
				String bodyText = null;
				if( parsedMessage.getBodyText() != null && parsedMessage.getBodyText().length() > 0 )
					bodyText = parsedMessage.getBodyText();
				
				BodyPart textBodypart = new MimeBodyPart();
				textBodypart.setContent( bodyText, "text/plain; charset=utf-8" );
				multipartAlternative.addBodyPart( textBodypart );
			}
			
			setHtml: {
				String bodyHtml = null;
				if( parsedMessage.getBodyHTML() != null && parsedMessage.getBodyHTML().length() > 0 ) {
					bodyHtml = parsedMessage.getBodyHTML();
				} else if( parsedMessage.getBodyRTF() != null && parsedMessage.getBodyRTF().length() > 0 ) {
					String bodyRtf2convert = parsedMessage.getBodyRTF();
					bodyHtml = RtfUtils.convertRtfToHtml( bodyRtf2convert );
				}
				
				Multipart multipartRelated = null;
				createRelated: {
					multipartRelated = new MimeMultipart( "related" );
					BodyPart mbpIntoAlternative = new MimeBodyPart();
					multipartAlternative.addBodyPart( mbpIntoAlternative );
					mbpIntoAlternative.setContent( multipartRelated );
				}
				
				BodyPart htmltBodypart = new MimeBodyPart();
				htmltBodypart.setContent( bodyHtml, "text/html; charset=utf-8" );
				multipartRelated.addBodyPart( htmltBodypart );
			}
			
			setAttachments: {
				List<Attachment> attachmentList = parsedMessage.getAttachments();
				List<BodyPart> bodyPartsList = getBodyPartAttachmentsList( attachmentList, convertedMessage, parsedMessage );
				
				for( BodyPart bodyPartAttachment : bodyPartsList ) {
					mixedContent.addBodyPart( bodyPartAttachment );
				}
			}
			
		}
		
		convertedMessage.setContent( mixedContent );
		log.info( "Method getAttachmentsMultiPart: STOP!" );
	}
	
	/**
	 * This Method receive an attachmenteList and convert all the attachments one by one converting them into a
	 * BodyPart and adding them to a bodyPartList to be returned to the caller method
	 * 
	 * @param attachmentList
	 * @return The bodyParts list converted from attachmentList of parsedMessage
	 * @throws Exception
	 */
	private static List<BodyPart> getBodyPartAttachmentsList( List<Attachment> attachmentList, javax.mail.Message convertedMessage,
			Message parsedMessage )
			throws Exception
	{
		log.info( "Method getBodyPartAttachmentsList: START!" );
		List<BodyPart> bodyPartsList = new ArrayList<BodyPart>();
		for( Attachment attachment : attachmentList ) {
			
			BodyPart bodyPart = null;
			String filename = null;
			if( attachment instanceof FileAttachment ) {
				
				FileAttachment fileAttachment = (FileAttachment) attachment;
				byte[] arrayByte = fileAttachment.getData();
				InputStream attachmentIs = new ByteArrayInputStream( arrayByte );
				String mimeTag = fileAttachment.getMimeTag();
				
				filename = fileAttachment.getLongFilename();
				if( ExtensionUtils.isEmail( filename ) ) {
					
					File tempFile = FileUtils.createTempFile();
					OutputStream osTmpMsg = new FileOutputStream( tempFile );
					StreamConverter.copy( attachmentIs, osTmpMsg );
					attachmentIs.close();
					osTmpMsg.close();
					
					Session session = convertedMessage.getSession();
					
					javax.mail.Message attachedJavaMessage = null;
					boolean isEML = ExtensionUtils.extractExtension( filename ) == FileExtension.EML;
					if( isEML ) {
						InputStream isFromTmpMsg = new FileInputStream( tempFile );
						attachedJavaMessage = new MimeMessage( session, isFromTmpMsg );
						isFromTmpMsg.close();
					} else
						attachedJavaMessage = MsgIntoEmlConverter.msgIntoEmlConverter( tempFile, session );
					
					filename = filename != null ? filename : attachedJavaMessage.getFileName();
					filename = filename != null ? filename : attachedJavaMessage.getSubject();
					filename = filename != null && isEML ? filename : filename + FileExtension.EML.getMainDotExtension();
					
					setHeadersFromParsedMessage( parsedMessage, attachedJavaMessage );
					
					InputStream newIsFromJmessage = attachedJavaMessage.getInputStream();
					bodyPart = new MimeBodyPart( newIsFromJmessage );
					newIsFromJmessage.close();
					bodyPart.setDataHandler( new DataHandler( attachedJavaMessage, "message/rfc822" ) );
					
					if( tempFile != null && tempFile.exists() )
						tempFile.delete();
					
				} else if( mimeTag != null && mimeTag.contains( "multipart/signed" ) ) {
					
					bodyPart = new MimeBodyPart( attachmentIs );
					setBodyPartParams( bodyPart, fileAttachment );
					
				} else {
					
					bodyPart = new MimeBodyPart( attachmentIs );
					setBodyPartParams( bodyPart, fileAttachment );
					
				}
				
			} else if( attachment instanceof MsgAttachment ) {
				
				MsgAttachment msgAttachment = (MsgAttachment) attachment;
				com.auxilii.msgparser.Message attachedMessage = msgAttachment.getMessage();
				filename = filename != null ? filename : attachedMessage.getSubject();
				
				Session session = convertedMessage.getSession();
				javax.mail.Message javaMessage = MsgIntoEmlConverter.msgIntoEmlConverter( attachedMessage, session );
				
				boolean isEML = ExtensionUtils.extractExtension( filename ) == FileExtension.EML;
				
				filename = filename != null ? filename : javaMessage.getFileName();
				filename = filename != null ? filename : javaMessage.getSubject();
				filename = filename != null && isEML ? filename : filename + FileExtension.EML.getMainDotExtension();
				
				InputStream is = javaMessage.getInputStream();
				bodyPart = new MimeBodyPart( is );
				is.close();
				bodyPart.setDataHandler( new DataHandler( javaMessage, "message/rfc822" ) );
			}
			
			filename = MessageUtils.cleanForbiddenChars( filename );
			bodyPart.setDisposition( BodyPart.ATTACHMENT );
			bodyPart.setFileName( filename );
			bodyPartsList.add( bodyPart );
		}
		log.info( "Method getBodyPartAttachmentsList: STOP!" );
		return bodyPartsList;
	}
	
	/**
	 * This method receive a bodyPart and the attachment from the caller method {@link #getBodyPartList(List)} and set
	 * the parameters of the passed attachment to the bodypart.
	 * 
	 * @param bodyPart
	 * @param attachment
	 * @throws MessagingException
	 */
	private static void setBodyPartParams( BodyPart bodyPart, FileAttachment attachment ) throws MessagingException
	{
		log.info( "Method setBodyPartParams: START!" );
		
		String fileName = attachment.getLongFilename();
		fileName = fileName != null ? fileName : attachment.getFilename();
		fileName = fileName != null ? fileName : "DEFAULT_ATTACHMENT_NAME";
		
		bodyPart.setFileName( fileName );
		
		String type = null;
		if( attachment.getMimeTag() != null && attachment.getMimeTag().contains( "multipart/signed" ) ) {
			type = "message/rfc822";
		} else if( attachment.getMimeTag() != null )
			type = attachment.getMimeTag();
		else
			type = "application/octet-stream";
		
		DataSource source = new ByteArrayDataSource( attachment.getData(), type );
		bodyPart.setDataHandler( new DataHandler( source ) );
		log.info( "Method setBodyPartParams: STOP!" );
	}
	
	/**
	 * This method receive a list of entry and convert it in an Address array.
	 * 
	 * @param listToConvert
	 * @return Address[] addresses
	 * @throws AddressException
	 */
	private static Address[] recipientListToAddressesArrayConverter( List<RecipientEntry> listToConvert ) throws AddressException
	{
		log.info( "Method recipientListToAddressesArrayConverter: START!" );
		
		int i = 0;
		Address[] addresses = new InternetAddress[listToConvert.size()];
		for( RecipientEntry recipientEntry : listToConvert ) {
			String emailAddress = recipientEntry.getToEmail();
			emailAddress = ( emailAddress != null ? emailAddress : recipientEntry.getToName() );
			addresses[i++] = new InternetAddress( emailAddress );
		}
		
		return addresses;
	}
}
