package eu.pecere.utils.archives;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class that implements the behavior of a generic pdf converter that must be able to convert from different
 * file formats to a pdf document. Inheriting classes must implement the abstract method
 * {@link ToArchiveExploder#convert(InputStream, OutputStream)} that is invoked by other methods.
 * 
 * @author Antonio Pecere
 *
 */
public abstract class ToArchiveExploder
{
	private static final Log log = LogFactory.getLog( ToArchiveExploder.class );
	
	/**
	 * The file to be converted into pdf format.
	 */
	private File inputFile;
	/**
	 * Boolean variable to determinate whether to delete the archive that is exploding
	 */
	private boolean delete;
	
	/**
	 * Abstract constructor to make mandatory the implementation in the inheriting classes.
	 * The inputFile could be null.
	 * 
	 * @param inputFile
	 *            The file to be converted into pdf format.
	 * @param delete
	 */
	protected ToArchiveExploder( File inputFile, boolean delete )
	{
		super();
		this.inputFile = inputFile;
		this.delete = delete;
	}
	
	/**
	 * @return The output folder path.
	 * 
	 * @throws IOException
	 *             If the conversion fails.
	 */
	String explode() throws IOException
	{
		File archiveFile = this.inputFile;
		boolean delete = this.delete;
		String extractedFolder = this.explode( archiveFile, delete );
		return extractedFolder;
	}
	
	/**
	 * Abstract method that call the exploder received from the Exploder Factory {@link ExploderFactory} .
	 * 
	 * @param archiveFile
	 * @param delete
	 * @return
	 * @throws IOException
	 */
	abstract String explode( File archiveFile, boolean delete ) throws IOException;
	
}
