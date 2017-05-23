package eu.pecere.utils.email;

import javax.mail.Session;

public class EmlUtils
{
	public EmailMessage parseEmlMessage( javax.mail.Message emlMessage )
	{
		return new EmailMessage();
	}
	
	public javax.mail.Message parseEmlMessage( EmailMessage message )
	{
		return new javax.mail.internet.MimeMessage( Session.getInstance( System.getProperties() ) );
	}
	
}
