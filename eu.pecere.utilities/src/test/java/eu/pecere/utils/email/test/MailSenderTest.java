package eu.pecere.utils.email.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import eu.pecere.utils.email.ErrorMailbox;
import eu.pecere.utils.email.ErrorMailboxUtils;
import eu.pecere.utils.email.sender.SendMailException;
import eu.pecere.utils.email.sender.SendMessage;
import eu.pecere.utils.email.sender.SendMessageImpl;

public class MailSenderTest extends EmailMasterTestCase
{
	@Test
	public void testMailSender() throws IOException, SendMailException
	{
		ErrorMailbox errMailboxObj = new ErrorMailbox();
		InputStream is = new FileInputStream( thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "mailConfig.json" );
		String jsonToMap = IOUtils.toString( is );
		errMailboxObj = ErrorMailboxUtils.loadFromJson( jsonToMap );
		SendMessage sm = new SendMessageImpl( errMailboxObj.getSmtpHost(), errMailboxObj.getSmtpPort(), errMailboxObj.agetAthorization() );
		sm.message( errMailboxObj.getMailSender(),
				errMailboxObj.getMailRecipients(),
				errMailboxObj.getMailObj(),
				errMailboxObj.getMailBody(),
				new String[] { "C:\\temp\\12309.pdf" } );
	}
	
}
