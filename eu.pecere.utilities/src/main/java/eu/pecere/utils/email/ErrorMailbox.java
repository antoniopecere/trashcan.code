/**
 * 
 */
package eu.pecere.utils.email;

/**
 * @author Antonio Pecere
 *
 */
public class ErrorMailbox
{
	private String smtpHost;
	private String smtpPort;
	boolean authorization;
	private String mailSender;
	private String mailRecipients[];
	private String mailObj;
	private String mailBody;
	
	private String password;
	
	public String getSmtpHost()
	{
		return this.smtpHost;
	}
	
	public void setSmtpHost( String smtpHost )
	{
		this.smtpHost = smtpHost;
	}
	
	public String getSmtpPort()
	{
		return this.smtpPort;
	}
	
	public void setSmtpPort( String smtpPort )
	{
		this.smtpPort = smtpPort;
	}
	
	public boolean agetAthorization()
	{
		return this.authorization;
	}
	
	public void setAuthorization( boolean authorization )
	{
		this.authorization = authorization;
	}
	
	public String getMailSender()
	{
		return this.mailSender;
	}
	
	public void setMailSender( String mailSender )
	{
		this.mailSender = mailSender;
	}
	
	public String[] getMailRecipients()
	{
		return this.mailRecipients;
	}
	
	public void setMailRecipients( String[] mailRecipients )
	{
		this.mailRecipients = mailRecipients;
	}
	
	public String getMailObj()
	{
		return this.mailObj;
	}
	
	public void setMailObj( String mailObj )
	{
		this.mailObj = mailObj;
	}
	
	public String getMailBody()
	{
		return this.mailObj;
	}
	
	public void setMailBody( String mailBody )
	{
		this.mailBody = mailBody;
	}
	
}
