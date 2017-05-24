package eu.pecere.utils.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class containing methods to manage resource files.
 * 
 * @author Antonio Pecere
 *
 */
public class ResourceUtils
{
	private static final Log log = LogFactory.getLog( ResourceUtils.class );
	
	public static final String DEFAULT_FILENAME = "tempStream2file";
	
	/**
	 * Utility method to retrieve a resource file from Classpath. It attempts to retrieve the file as a resource
	 * stream, first from this class, then from the ClassLoader and finally from the ContextClassLoader, and returns the
	 * first stream that is not null.
	 * 
	 * @param resourceFilePathname
	 *            The qualified pathname of the file to be retrieved as a resource stream.
	 * 
	 * @return The stream to the resource file if it's found, <code>null</code> otherwise.
	 */
	public static InputStream getResourceAsStream( String resourceFilePathname )
	{
		InputStream resourceStream = null;
		
		// To avoid the throw of an IOException, the stream is not retrieved with this cross-calling approach:
		// URL resourceUrl = ResourceUtils.getResourceURL( resourceFilePathname );
		// InputStream resourceStream = resourceUrl.openStream();
		// return resourceStream;
		
		boolean isDebugEnabled = log.isDebugEnabled();
		String getResourceAsStream = ".getResourceAsStream(|" + resourceFilePathname + "|)";
		String streamStatus = null;
		
		if( isDebugEnabled ) {
			log.debug( "Look up for resource |" + resourceFilePathname + "|..." );
		}
		
		resourceStream = ResourceUtils.class.getResourceAsStream( resourceFilePathname );
		if( isDebugEnabled ) {
			streamStatus = ( resourceStream == null ? " IS NULL!" : " *** OK! ***" );
			log.debug( "ResourceUtils.class" + getResourceAsStream + streamStatus );
		}
		
		if( resourceStream == null ) {
			resourceStream = ResourceUtils.class.getClassLoader().getResourceAsStream( resourceFilePathname );
			if( isDebugEnabled ) {
				streamStatus = ( resourceStream == null ? " IS NULL!" : " *** OK! ***" );
				log.debug( "ResourceUtils.class.getClassLoader()" + getResourceAsStream + streamStatus );
			}
		}
		
		if( resourceStream == null ) {
			resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resourceFilePathname );
			if( isDebugEnabled ) {
				streamStatus = ( resourceStream == null ? " IS NULL!" : " *** OK! ***" );
				log.debug( "Thread.currentThread().getContextClassLoader()" + getResourceAsStream + streamStatus );
			}
		}
		
		if( isDebugEnabled ) {
			log.debug( "Stream relative to resource |" + resourceFilePathname + "| " + ( resourceStream == null ? "IS NOT " : "" )
					+ "FOUND!" );
		}
		
		return resourceStream;
	}
	
	/**
	 * Utility method to retrieve a resource file from Classpath. It attempts to retrieve the file as an URL, first from
	 * this class, then from the ClassLoader and finally from the ContextClassLoader, and returns the first URL that is
	 * not null.
	 * 
	 * @param resourceFilePathname
	 *            The qualified pathname of the file to be retrieved as a resource stream.
	 * 
	 * @return The URL pointing to the resource file if it's found, <code>null</code> otherwise.
	 */
	public static URL getResourceURL( String resourceFilePathname )
	{
		URL resourceUrl = null;
		
		boolean isDebugEnabled = log.isDebugEnabled();
		String getResourceAsStream = ".getResource(|" + resourceFilePathname + "|)";
		String streamStatus = null;
		
		if( isDebugEnabled ) {
			log.debug( "Look up for resource |" + resourceFilePathname + "|..." );
		}
		
		resourceUrl = ResourceUtils.class.getResource( resourceFilePathname );
		if( isDebugEnabled ) {
			streamStatus = ( resourceUrl == null ? " IS NULL!" : " *** OK! ***" );
			log.debug( "ResourceUtils.class" + getResourceAsStream + streamStatus );
		}
		
		if( resourceUrl == null ) {
			resourceUrl = ResourceUtils.class.getClassLoader().getResource( resourceFilePathname );
			if( isDebugEnabled ) {
				streamStatus = ( resourceUrl == null ? " IS NULL!" : " *** OK! ***" );
				log.debug( "ResourceUtils.class.getClassLoader()" + getResourceAsStream + streamStatus );
			}
		}
		
		if( resourceUrl == null ) {
			resourceUrl = Thread.currentThread().getContextClassLoader().getResource( resourceFilePathname );
			if( isDebugEnabled ) {
				streamStatus = ( resourceUrl == null ? " IS NULL!" : " *** OK! ***" );
				log.debug( "Thread.currentThread().getContextClassLoader()" + getResourceAsStream + streamStatus );
			}
		}
		
		if( isDebugEnabled ) {
			log.debug( "URL relative to resource |" + resourceFilePathname + "| " + ( resourceUrl == null ? "IS NOT " : "" ) + "FOUND!" );
		}
		
		return resourceUrl;
	}
	
	/**
	 * Utility method to retrieve a resource file from Classpath. It attempts to retrieve the file as an URL, first from
	 * this class, then from the ClassLoader and finally from the ContextClassLoader, and returns the first URL that is
	 * not null.
	 * 
	 * @param resourceFilePathname
	 *            The qualified pathname of the file to be retrieved as a resource stream.
	 * 
	 * @return The URI pointing to the resource file if it's found, <code>null</code> otherwise.
	 */
	public static URI getResourceURI( String resourceFilePathname )
	{
		URL resourceUrl = getResourceURL( resourceFilePathname );
		
		URI resourceUri = null;
		try {
			resourceUri = resourceUrl != null ? resourceUrl.toURI() : null;
		} catch( URISyntaxException e ) {
			log.debug( "URI relative to resource |" + resourceFilePathname + "| " + ( resourceUri == null ? "IS NOT " : "" ) + "FOUND!" );
		}
		
		return resourceUri;
	}
	
	/**
	 * Utility method to retrieve a resource file from Classpath. It attempts to retrieve the file as a resource stream
	 * using the method {@link ResourceUtils#getResourceAsStream(String)}. If the stream is null, it returns null,
	 * otherwise it tries to write a temporary file to be returned. It dispose the deleteOnExit for the temporary file
	 * created.
	 * 
	 * @param resourceFilePathname
	 *            The qualified pathname of the file to be retrieved as a resource stream.
	 * 
	 * @return The temporary file created from the read stream if it's found, <code>null</code> otherwise.
	 * 
	 * @throws IOException
	 *             If detecting of File Type or its creation fail.
	 */
	public static File getResourceAsLocalFileCopy( String resourceFilePathname ) throws IOException
	{
		File resourceFile = getResourceAsFile( resourceFilePathname );
		
		if( resourceFile == null ) {
			return null;
		}
		
		String extension = ExtensionUtils.extractExtension( resourceFile ).getMainExtension();
		extension = extension == null || extension.isEmpty() ? FileExtension.TMP.getMainExtension() : extension;
		String filename = DEFAULT_FILENAME + "_" + ( new Date() ).getTime() + "_" + RandomUtils.nextInt( 0, 9000 ) + 1000 + "_";
		final File tempFile = File.createTempFile( filename, extension );
		tempFile.deleteOnExit();
		
		try( FileInputStream inputStream = new FileInputStream( resourceFile );
				FileOutputStream outputStream = new FileOutputStream( tempFile ) ) {
			IOUtils.copy( inputStream, outputStream );
		}
		
		return tempFile;
	}
	
	/**
	 * Utility method to retrieve a resource file from Classpath. It attempts to retrieve the file by its URL using the
	 * method {@link ResourceUtils#getResourceURL(String)}. If the URL is null, it returns null, otherwise it returns
	 * the file created starting from the URL.
	 * 
	 * @param resourceFilePathname
	 *            The qualified pathname of the file to be retrieved.
	 * 
	 * @return The file created from the specified path if it's found, <code>null</code> otherwise.
	 */
	public static File getResourceAsFile( String resourceFilePathname )
	{
		File resourceFile = new File( resourceFilePathname );
		if( resourceFile.exists() ) {
			return resourceFile;
		}
		
		URI resourceUri = getResourceURI( resourceFilePathname );
		resourceFile = resourceUri == null ? null : Paths.get( resourceUri ).toFile();
		return resourceFile;
	}
	
	public static File getOrCreateResourceFolder( String folderAbsolutePath )
	{
		File folder = getResourceAsFile( folderAbsolutePath );
		if( folder == null ) {
			folder = new File( folderAbsolutePath );
			if( !folder.exists() ) {
				folder.mkdir();
			}
		}
		return folder;
	}
	
}
