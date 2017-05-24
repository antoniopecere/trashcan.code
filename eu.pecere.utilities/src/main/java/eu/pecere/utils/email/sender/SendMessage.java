package eu.pecere.utils.email.sender;

public interface SendMessage
{
	void message( String mailFrom, String[] mailTo, String subject, String body, String[] attach ) throws SendMailException;
}
