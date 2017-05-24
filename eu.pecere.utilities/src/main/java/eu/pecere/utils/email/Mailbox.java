package eu.pecere.utils.email;

import java.io.Serializable;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class Mailbox implements Comparable<Mailbox>, Serializable
{
	private static final long serialVersionUID = 2312580067981029389L;
	
	private Protocol protocol;
	private boolean ssl;
	private boolean pec;
	private String host;
	private Integer port;
	private String address;
	private String password;
	
	public Protocol getProtocol()
	{
		return protocol;
	}
	
	public void setProtocol( Protocol protocol )
	{
		this.protocol = protocol;
	}
	
	public void setProtocol( String protocolName )
	{
		this.protocol = Protocol.valueOf( protocolName );
	}
	
	public boolean isSsl()
	{
		return ssl;
	}
	
	public void setSsl( boolean ssl )
	{
		this.ssl = ssl;
	}
	
	public boolean isPec()
	{
		return pec;
	}
	
	public void setPec( boolean pec )
	{
		this.pec = pec;
	}
	
	public String getHost()
	{
		return this.host;
	}
	
	public void setHost( String host )
	{
		this.host = host;
	}
	
	public Integer getPort()
	{
		return this.port;
	}
	
	public void setPort( Integer port )
	{
		this.port = port;
	}
	
	public String getAddress()
	{
		return this.address;
	}
	
	public void setAddress( String address )
	{
		this.address = address;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public void setPassword( String password )
	{
		this.password = password;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ( ( this.protocol == null ) ? 0 : this.protocol.hashCode() );
		result = prime * result + ( ( this.host == null ) ? 0 : this.host.hashCode() );
		result = prime * result + ( ( this.port == null ) ? 0 : this.port.hashCode() );
		result = prime * result + ( ( this.address == null ) ? 0 : this.address.hashCode() );
		result = prime * result + ( ( this.password == null ) ? 0 : this.password.hashCode() );
		return result;
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		
		if( obj == null )
			return false;
		
		if( getClass() != obj.getClass() )
			return false;
		
		Mailbox other = (Mailbox) obj;
		
		if( this.protocol != other.protocol )
			return false;
		
		if( this.host == null ) {
			if( other.host != null )
				return false;
		} else if( !this.host.equals( other.host ) )
			return false;
		
		if( this.port == null ) {
			if( other.port != null )
				return false;
		} else if( !this.port.equals( other.port ) )
			return false;
		
		if( this.address == null ) {
			if( other.address != null )
				return false;
		} else if( !this.address.equals( other.address ) )
			return false;
		
		if( this.password == null ) {
			if( other.password != null )
				return false;
		} else if( !this.password.equals( other.password ) )
			return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		// @formatter:off
		return "Mailbox ["
				+ "protocol=" + protocol + ", "
				+ "ssl=" + ssl + ", "
				+ "pec=" + pec + ", "
				+ "host=" + host + ", "
				+ "port=" + port + ", "
				+ "address=" + address + ", "
				+ "password=" + password
				+ "]";
		// @formatter:on
	}
	
	@Override
	public int compareTo( Mailbox mailbox )
	{
		return this.address.compareTo( mailbox.getAddress() );
	}
	
}
