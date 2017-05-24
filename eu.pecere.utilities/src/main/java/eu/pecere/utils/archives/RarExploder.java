/**
 * 
 */
package eu.pecere.utils.archives;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import eu.pecere.utils.commons.ExtensionUtils;

/**
 * This class implement the abstract class {@link ToArchiveExploder} with the Rar Archive Exploder Procedure
 * 
 * @author Antonio Pecere
 *
 */
public class RarExploder extends ToArchiveExploder
{
	
	private static final Log log = LogFactory.getLog( RarExploder.class );
	
	/**
	 * This method receive in input the archive to explode and a boolean variable used to determinate whether to delete
	 * the archive that is exploding
	 * 
	 * @param inputFile
	 * @param delete
	 * 
	 */
	public RarExploder( File inputFile, boolean delete )
	{
		super( inputFile, delete );
	}
	
	/**
	 * @throws RarException
	 * @throws IOException
	 * @see eu.pecere.utils.archives.ToArchiveExploder#explode(java.io.File, boolean)
	 */
	@Override
	public String explode( File archiveFile, boolean delete ) throws IOException
	{
		log.info( "Exploder: " + this.getClass() + " Method explode(): START!" );
		
		try {
			String explodedRarPath = ExtensionUtils.removeExtension( archiveFile );
			
			Archive archive = new Archive( archiveFile );
			FileHeader fileHeader = archive.nextFileHeader();
			
			while( fileHeader != null ) {
				
				if( fileHeader.isDirectory() ) {
					fileHeader = archive.nextFileHeader();
					continue;
				}
				
				String archiveRootFolderPath = ExtensionUtils.removeExtension( archiveFile );
				log.info( "Parent Directory: " + archiveRootFolderPath );
				
				File zipRootFolder = new File( archiveRootFolderPath.trim() );
				if( !zipRootFolder.exists() ) {
					zipRootFolder.mkdir();
					log.info( "Parent Directory created: " + archiveRootFolderPath );
				}
				
				String headerName = fileHeader.getFileNameString();
				File newFileFromHeader = new File( archiveRootFolderPath + File.separator + headerName );
				
				String parentFolderName = newFileFromHeader.getParent();
				File parentDir = new File( parentFolderName );
				parentDir.mkdirs();
				
				OutputStream fos = new FileOutputStream( newFileFromHeader );
				
				archive.extractFile( fileHeader, fos );
				
				fos.close();
				
				String filterDescription = "accepted archivie extensions";
				FileNameExtensionFilter archiveFilter = new FileNameExtensionFilter( filterDescription, ExtensionUtils.ARCHIVE_EXTENSIONS );
				
				if( archiveFilter.accept( newFileFromHeader ) ) {
					ArchiveUtils.explode( newFileFromHeader, true );
				}
				
				fileHeader = archive.nextFileHeader();
			}
			archive.close();
			if( delete ) {
				archiveFile.delete();
			}
			
			log.info( "Exploder: " + this.getClass() + " Method explode(): STOP!" );
			return explodedRarPath;
			
		} catch( RarException e ) {
			String errorMessage = "Error while exploding Rar Archive"
					+ this.getClass().getSimpleName() + ".\r\n"
					+ "Exception: " + e.getClass().getSimpleName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw new IOException( e );
		} catch( Exception e ) {
			String errorMessage = "Error while exploding Rar Archive"
					+ this.getClass().getSimpleName() + ".\r\n"
					+ "Exception: " + e.getClass().getSimpleName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw e;
		}
	}
}
