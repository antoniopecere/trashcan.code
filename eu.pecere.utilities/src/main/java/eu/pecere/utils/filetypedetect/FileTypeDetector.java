package eu.pecere.utils.filetypedetect;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class FileTypeDetector
{
	private static final Log log = LogFactory.getLog( FileTypeDetector.class );
	
	private static final Tika TIKA = new Tika();
	private static final MimeTypes TIKA_MIME_REPOSITORY = TikaConfig.getDefaultConfig().getMimeRepository();
	
	private String mediaType;
	private String mimeType;
	private String extension;
	
	/**
	 * Create a new FileTypeDetector with the InputStream specified. Then uses the inputStream to detect the properties
	 * of its content, retrieving media and mime type and the extension of the underlying file.
	 * 
	 * @param inputStream
	 *            The InputStream to detect properties from.
	 * 
	 * @throws IOException
	 *             If the detection from the stream fails.
	 */
	public FileTypeDetector( InputStream inputStream ) throws IOException
	{
		this.mediaType = TIKA.detect( TikaInputStream.get( inputStream ) );
		log.info( "Detected MediaType: " + this.mediaType );
		
		MimeType mimeTypeObject;
		try {
			mimeTypeObject = TIKA_MIME_REPOSITORY.forName( this.mediaType );
		} catch( MimeTypeException e ) {
			log.error( e.getMessage(), e );
			throw new IOException( e );
		}
		
		this.mimeType = mimeTypeObject.toString();
		log.info( "Detected MimeType: " + this.mimeType );
		
		String dotExtension = mimeTypeObject.getExtension().toLowerCase();
		this.extension = dotExtension.isEmpty() ? "" : dotExtension.substring( dotExtension.indexOf( "." ) + 1 );
		log.info( "Detected Extension: " + this.extension );
	}
	
	/**
	 * The media type detected from the InputStream, retrieved during construction.
	 * 
	 * @return The media type detected from the InputStream.
	 */
	public String getMediaType()
	{
		return this.mediaType;
	}
	
	/**
	 * The mime type detected from the InputStream, retrieved during construction.
	 * 
	 * @return The mime type detected from the InputStream.
	 */
	public String getMimeType()
	{
		return this.mimeType;
	}
	
	/**
	 * The eventual extension used by the file underlying the InputStream, retrieved during construction.
	 * 
	 * @return The eventual extension used by the file underlying the InputStream.
	 */
	public String getExtension()
	{
		return this.extension;
	}
	
	/**
	 * The eventual extension used by the file underlying the InputStream, retrieved during construction.
	 * 
	 * @return The eventual extension used by the file underlying the InputStream.
	 */
	public String getDotExtension()
	{
		return this.extension.isEmpty() ? "" : "." + this.extension;
	}
	
}
