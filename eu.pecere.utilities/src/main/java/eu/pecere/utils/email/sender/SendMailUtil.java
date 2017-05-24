package eu.pecere.utils.email.sender;

import org.apache.commons.lang3.StringUtils;

public class SendMailUtil
{
	
	public static String getName( String line )
	{
		return StringUtils.substringBetween( line, "\'" );
	}
	
	public static String getAddress( String line )
	{
		return StringUtils.substringBetween( line, "<", ">" );
	}
}
