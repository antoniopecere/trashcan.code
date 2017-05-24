package eu.pecere.utils.archives;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileExtension;

/**
 * Class that contains the method factory to instantiate the needed converter, hiding the implementation of the relative
 * strategy.
 * 
 * @author Antonio Pecere
 *
 */
public class ExploderFactory
{
	private static final Log log = LogFactory.getLog( ExploderFactory.class );
	
	/**
	 * 
	 * @param inputFile
	 * @param delete
	 * @return
	 */
	public static ToArchiveExploder getArchiveExploder( File inputFile, boolean delete )
	{
		String inputFileExtension = ExtensionUtils.extractExtension( inputFile ).getMainExtension();
		return ExploderFactory.getArchiveExploder( inputFileExtension, inputFile, delete );
	}
	
	/**
	 * Factory method to create an instance of a specific converter related to the archive type to be exploder. The
	 * right converter is chosen by the extension.
	 * 
	 * @param inputFileExtension
	 *            The extension used to chose the converter to be used for the conversion.
	 * @param inputFile
	 *            The possible file to be converted.
	 * 
	 * @return The converter related to the chosen extension.
	 */
	public static ToArchiveExploder getArchiveExploder( String inputFileExtension, File inputFile, boolean delete )
	{
		log.info( "Input file extension: " + inputFileExtension );
		FileExtension fileExtension = FileExtension.getFileExtension( inputFileExtension );
		
		log.info( "The input file is" + ( inputFile == null ? " " : " not " ) + "null!" );
		
		ToArchiveExploder exploder;
		
		switch( fileExtension ) {
			case SZIP:
				exploder = new SevenZexploder( inputFile, delete );
				break;
			case ZIP:
			case ADZ:
				exploder = new ZipExploder( inputFile, delete );
				break;
			case RAR:
				exploder = new RarExploder( inputFile, delete );
				break;
			case TAR:
				exploder = new TarExploder( inputFile, delete );
				break;
			
			default:
				exploder = null;
				break;
		}
		
		log.info( "The exploder to be returned is: " + exploder );
		return exploder;
	}
	
}
