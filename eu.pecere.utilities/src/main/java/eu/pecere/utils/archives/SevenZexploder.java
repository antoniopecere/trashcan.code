/**
 * 
 */
package eu.pecere.utils.archives;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.pecere.utils.commons.ExtensionUtils;

/**
 * This class implement the abstract class {@link ToArchiveExploder} with the 7z Archive Exploder Procedure
 * 
 * @author Antonio Pecere
 *
 */
public class SevenZexploder extends ToArchiveExploder
{
	
	/**
	 * This method receive in input the archive to explode and a boolean variable used to determinate whether to delete
	 * the archive that is exploding
	 * 
	 * @param inputFile
	 * @param delete
	 */
	
	private static final Log log = LogFactory.getLog( SevenZexploder.class );
	
	public SevenZexploder( File inputFile, boolean delete )
	{
		super( inputFile, delete );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msa.utils.archives.ToArchiveExploder#explode(java.io.File, boolean)
	 */
	@Override
	public String explode( File inFile, boolean delete ) throws IOException
	{
		log.info( "Exploder: " + this.getClass() + " Method explode(): START!" );
		
		try {
			String explodedSevenZPath = ExtensionUtils.removeExtension( inFile );
			
			SevenZFile sevenZfile = new SevenZFile( inFile );
			SevenZArchiveEntry sevenZentry;
			
			byte[] buffer = new byte[2048];
			while( ( sevenZentry = sevenZfile.getNextEntry() ) != null ) {
				
				if( sevenZentry.isDirectory() ) {
					continue;
				}
				
				String archiveRootFolderPath = ExtensionUtils.removeExtension( inFile );
				log.info( "Parent Directory: " + archiveRootFolderPath );
				
				File zipRootFolder = new File( archiveRootFolderPath.trim() );
				if( !zipRootFolder.exists() ) {
					zipRootFolder.mkdir();
					log.info( "Parent Directory created: " + archiveRootFolderPath );
				}
				
				String sevenZEntryName = sevenZentry.getName();
				File newFileFromEntry = new File( archiveRootFolderPath + File.separator + sevenZEntryName );
				
				String parentFolderPath = newFileFromEntry.getParent();
				File parentFolder = new File( parentFolderPath );
				parentFolder.mkdirs();
				
				FileOutputStream fos = new FileOutputStream( newFileFromEntry );
				int len;
				
				while( ( len = sevenZfile.read( buffer ) ) > 0 ) {
					fos.write( buffer, 0, len );
				}
				fos.close();
				
				String filterDescription = "accepted archivie extensions";
				FileNameExtensionFilter archiveFilter = new FileNameExtensionFilter( filterDescription, ExtensionUtils.ARCHIVE_EXTENSIONS );
				
				if( archiveFilter.accept( newFileFromEntry ) ) {
					ArchiveUtils.explode( newFileFromEntry, true );
				}
				
			}
			
			sevenZfile.close();
			if( delete ) {
				inFile.delete();
			}
			
			log.info( "Exploder: " + this.getClass() + " Method explode(): STOP!" );
			return explodedSevenZPath;
			
		} catch( IOException e ) {
			String errorMessage = "Error while exploding Zip Archive"
					+ this.getClass().getSimpleName() + ".\r\n"
					+ "Exception: " + e.getClass().getSimpleName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw e;
		}
	}
	
}
