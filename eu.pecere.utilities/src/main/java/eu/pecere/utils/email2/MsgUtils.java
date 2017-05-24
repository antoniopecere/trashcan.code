package eu.pecere.utils.email2;

public class MsgUtils
{
	public EmailMessage parseMsgMessage( com.auxilii.msgparser.Message msgMessage )
	{
		return new EmailMessage();
	}
	
	public com.auxilii.msgparser.Message buildMsgMessage( EmailMessage message )
	{
		return new com.auxilii.msgparser.Message();
	}
}
