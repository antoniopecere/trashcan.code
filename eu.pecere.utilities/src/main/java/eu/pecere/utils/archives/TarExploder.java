/**
 * 
 */
package eu.pecere.utils.archives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.pecere.utils.commons.ExtensionUtils;

/**
 * This class implement the abstract class {@link ToArchiveExploder} with the Tar Archive Exploder Procedure
 * 
 * @author Antonio Pecere
 *
 */
public class TarExploder extends ToArchiveExploder
{
	/**
	 * This method receive in input the archive to explode and a boolean variable used to determinate whether to delete
	 * the archive that is exploding
	 * 
	 * @param inputFile
	 * @param delete
	 */
	private static final Log log = LogFactory.getLog( TarExploder.class );
	
	public TarExploder( File inputFile, boolean delete )
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
			String explodedTarPath = ExtensionUtils.removeExtension( inFile );
			
			InputStream inputStream = new FileInputStream( inFile );
			TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream( inputStream );
			ArchiveEntry tarEntry = tarArchiveInputStream.getNextEntry();
			
			byte[] buffer = new byte[2048];
			while( tarEntry != null ) {
				
				if( tarEntry.isDirectory() ) {
					tarEntry = tarArchiveInputStream.getNextEntry();
					continue;
				}
				
				String archiveRootFolderPath = ExtensionUtils.removeExtension( inFile );
				log.info( "Parent Directory: " + archiveRootFolderPath );
				
				File archiveRootFolder = new File( archiveRootFolderPath.trim() );
				if( !archiveRootFolder.exists() ) {
					archiveRootFolder.mkdir();
					log.info( "Parent Directory created: " + archiveRootFolderPath );
				}
				
				String tarEntryName = tarEntry.getName();
				File newFileFromEntry = new File( archiveRootFolderPath + File.separator + tarEntryName );
				
				String parentFolder = newFileFromEntry.getParent();
				File parentDir = new File( parentFolder );
				parentDir.mkdirs();
				
				FileOutputStream fos = new FileOutputStream( newFileFromEntry );
				
				int len;
				
				while( ( len = tarArchiveInputStream.read( buffer ) ) > 0 ) {
					fos.write( buffer, 0, len );
				}
				fos.close();
				
				String filterDescription = "accepted archivie extensions";
				FileNameExtensionFilter archiveFilter = new FileNameExtensionFilter( filterDescription, ExtensionUtils.ARCHIVE_EXTENSIONS );
				
				if( archiveFilter.accept( newFileFromEntry ) ) {
					ArchiveUtils.explode( newFileFromEntry, true );
				}
				
				tarEntry = tarArchiveInputStream.getNextEntry();
				
			}
			
			tarArchiveInputStream.close();
			if( delete ) {
				inFile.delete();
			}
			
			log.info( "Exploder: " + this.getClass() + " Method explode(): STOP!" );
			return explodedTarPath;
			
		} catch( IOException e ) {
			String errorMessage = "Error while exploding Tar Archive"
					+ this.getClass().getSimpleName() + ".\r\n"
					+ "Exception: " + e.getClass().getSimpleName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw e;
		}
	}
	
}
