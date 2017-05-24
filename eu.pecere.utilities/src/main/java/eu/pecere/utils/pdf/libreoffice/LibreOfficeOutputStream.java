package eu.pecere.utils.pdf.libreoffice;

import java.io.ByteArrayOutputStream;

import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XOutputStream;

/**
 * Class that wraps an output stream into a customized stream. This stream implements the UNO specific interface
 * XOutputStream, to make LibreOffice able to use it.
 * 
 * @author Antonio Pecere
 *
 */
class LibreOfficeOutputStream extends ByteArrayOutputStream implements XOutputStream
{
	public LibreOfficeOutputStream()
	{
		super( 32768 );
	}
	
	// #######################
	// Implement XOutputStream
	// #######################
	
	@Override
	public void writeBytes( byte[] values ) throws NotConnectedException, BufferSizeExceededException, com.sun.star.io.IOException
	{
		try {
			this.write( values );
		} catch( java.io.IOException e ) {
			throw ( new com.sun.star.io.IOException( e.getMessage() ) );
		}
	}
	
	@Override
	public void closeOutput() throws NotConnectedException, BufferSizeExceededException, com.sun.star.io.IOException
	{
		try {
			super.flush();
			super.close();
		} catch( java.io.IOException e ) {
			throw ( new com.sun.star.io.IOException( e.getMessage() ) );
		}
	}
	
	@Override
	public void flush()
	{
		try {
			super.flush();
		} catch( java.io.IOException e ) {
		}
	}
	
}