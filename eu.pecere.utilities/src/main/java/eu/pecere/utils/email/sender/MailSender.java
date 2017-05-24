package eu.pecere.utils.email.sender;

public class MailSender
{
	// @formatter:off
	public static void sendMail(
			String mailSender,
			String[] mailRecipients,
			String mailSubject,
			String mailBody,
			String attachmentFilePath,
			String host,
			String port )
	// @formatter:on
	{
		SendMessage sm = new SendMessageImpl( host, port, false );
		sm.message( mailSender, mailRecipients, mailSubject, mailBody, new String[] { attachmentFilePath } );
	}
	
}
