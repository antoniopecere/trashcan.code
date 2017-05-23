package eu.pecere.utils.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.Ostermiller.util.CircularByteBuffer;

/**
 * Utility class to manage the conversion of streams, hiding the usage of the external libraries.<br>
 * <br>
 * Example of use:
 * 
 * <pre>
 * <code>
 * StreamConverter converter = new StreamConveter();
 * 
 * OutputStream outputStream = converter.getOutputStreamToConvert();
 * // statements to write into the output stream...
 * byte[] dataToWrite = ... // statements to produce the data to write.
 * outputStream.write( dataToWrite );
 * 
 * // the input stream now contains the data written into the output stream.
 * 
 * InputStream inputStream = converter.getInputStreamFromConversion();
 * // statements to read data from the input stream.
 * byte[] dataToRead = ...// statements to initialize the array.
 * inputStream.read( dataToRead );
 * </code>
 * </pre>
 * 
 * @author Antonio Pecere: 25 mag 2016
 *
 */
public class StreamConverter
{
	private CircularByteBuffer circularBuffer;
	
	/**
	 * Constructs a new instance of the object to be used to convert an output stream into an input stream.<br>
	 */
	public StreamConverter()
	{
		this.circularBuffer = new CircularByteBuffer( CircularByteBuffer.INFINITE_SIZE );
	}
	
	/**
	 * Retrieve the output stream to write into. This output stream is a part of a circular buffer of bytes used for the
	 * conversion. The bytes written into this output stream are used to feed the input stream of this converter. See
	 * also {@link StreamConverter#getInputStreamFromConversion()}.
	 * 
	 * @return The output stream to write into, used to feed the input stream.
	 * 
	 * @see StreamConverter#getInputStreamFromConversion()
	 */
	public OutputStream getOutputStreamToConvert()
	{
		return this.circularBuffer.getOutputStream();
	}
	
	/**
	 * Retrieve the input stream to read from. This input stream is a part of a circular buffer of bytes used for the
	 * conversion. It is fed by the bytes written into the output stream of this stream converter. See also
	 * {@link StreamConverter#getOutputStreamToConvert()}.
	 * 
	 * @return The input stream to read from, fed by the output stream bytes.
	 * 
	 * @see StreamConverter#getOutputStreamToConvert()
	 */
	public InputStream getInputStreamFromConversion()
	{
		return this.circularBuffer.getInputStream();
	}
	
	/**
	 * Convenience utility method to hide the use of Apache Commons IO library.
	 * 
	 * @param inputStreamToReadForm
	 *            The input stream to read from.
	 * 
	 * @param outputStreamToWriteInto
	 *            The output stream to write into.
	 * 
	 * @throws NullPointerException
	 *             If one of the streams is null.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static void copy( InputStream inputStreamToReadForm, OutputStream outputStreamToWriteInto ) throws IOException
	{
		IOUtils.copy( inputStreamToReadForm, outputStreamToWriteInto );
	}
	
}
