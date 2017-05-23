package eu.pecere.utils.commons;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.RandomUtils;

public class EncryptUtils
{
	
	private static final String ALGO = "AES";
	private static final byte[] keyValue = RandomUtils.nextBytes( 16 );
	
	public static String encrypt( String data, String encryptionKey )
	{
		try {
			Key key = EncryptUtils.generateKey( encryptionKey );
			Cipher c = Cipher.getInstance( ALGO );
			c.init( Cipher.ENCRYPT_MODE, key );
			byte[] encVal = c.doFinal( data.getBytes() );
			String encryptedValue = DatatypeConverter.printBase64Binary( encVal );
			return encryptedValue;
		} catch( Throwable e ) {
			throw new RuntimeException( e );
		}
		
	}
	
	public static String decrypt( String encryptedData, String encryptionKey )
	{
		try {
			Key key = EncryptUtils.generateKey( encryptionKey );
			Cipher c = Cipher.getInstance( ALGO );
			c.init( Cipher.DECRYPT_MODE, key );
			byte[] decordedValue = DatatypeConverter.parseBase64Binary( encryptedData );
			byte[] decValue = c.doFinal( decordedValue );
			String decryptedValue = new String( decValue );
			return decryptedValue;
		} catch( Throwable e ) {
			throw new RuntimeException( e );
		}
	}
	
	private static Key generateKey( String encryptionKey ) throws Exception
	{
		Key key = new SecretKeySpec( keyValue, ALGO );
		return key;
		
	}
}
