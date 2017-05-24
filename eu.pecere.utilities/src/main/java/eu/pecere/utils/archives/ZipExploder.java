package eu.pecere.utils.archives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.pecere.utils.commons.ExtensionUtils;

/**
 * This class implement the abstract class {@link ToArchiveExploder} with the Zip Archive Exploder Procedure
 * 
 * @author Antonio Pecere
 *
 */
public class ZipExploder extends ToArchiveExploder
{
	/**
	 * This method receive in input the archive to explode and a boolean variable used to determinate whether to delete
	 * the archive that is exploding
	 * 
	 * @param inputFile
	 * @param delete
	 * 
	 * @exception FileNotFoundException,
	 *                IOException
	 */
	private static final Log log = LogFactory.getLog( ZipExploder.class );
	
	protected ZipExploder( File inputFile, boolean delete )
	{
		super( inputFile, delete );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msa.utils.archives.ToArchiveExploder#explode(java.io.File, boolean)
	 */
	@Override
	protected String explode( File zipFile, boolean delete ) throws FileNotFoundException, IOException
	{
		log.info( "Method explode( ZipFile, boolean ): START!" );
		
		try( ZipFile zipfile = new ZipFile( zipFile ) ) {
			// DO NOTHING!!
		} catch( ZipException e ) {
			String errorMessage = "Error while exploding Zip Archive, the current file would seem don't be a real Zip Archive or don't be a valid Zip Archive "
					+ this.getClass().getSimpleName() + ".\r\n"
					+ "Exception: " + e.getClass().getSimpleName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw e;
		}
		
		try {
			String zipRootFolderPath = ExtensionUtils.removeExtension( zipFile );
			
			ZipInputStream zis = new ZipInputStream( new FileInputStream( zipFile ) );
			ZipEntry entry;
			
			byte[] buffer = new byte[2048];
			while( ( entry = zis.getNextEntry() ) != null ) {
				
				if( entry.isDirectory() ) {
					continue;
				}
				
				log.info( "Parent Directory: " + zipRootFolderPath );
				
				File zipRootFolder = new File( zipRootFolderPath.trim() );
				if( !zipRootFolder.exists() ) {
					zipRootFolder.mkdir();
					log.info( "Parent Directory created: " + zipRootFolderPath );
				}
				
				String zipEntryName = entry.getName();
				
				// create all non exists folders else you will hit FileNotFoundException for compressed folder
				File newFileFromZipEntry = new File( zipRootFolder + File.separator + zipEntryName );
				
				String parentFolderName = newFileFromZipEntry.getParent();
				File parentDir = new File( parentFolderName );
				if( !parentDir.exists() )
					parentDir.mkdirs();
				
				OutputStream fos = new FileOutputStream( newFileFromZipEntry );
				
				int len;
				while( ( len = zis.read( buffer ) ) > 0 ) {
					fos.write( buffer, 0, len );
				}
				fos.close();
				
				String filterDescription = "accepted archivie extensions";
				FileNameExtensionFilter archiveFilter = new FileNameExtensionFilter( filterDescription, ExtensionUtils.ARCHIVE_EXTENSIONS );
				
				if( archiveFilter.accept( newFileFromZipEntry ) ) {
					ArchiveUtils.explode( newFileFromZipEntry, true );
				}
				
				zis.closeEntry();
			}
			
			zis.close();
			if( delete ) {
				zipFile.delete();
			}
			log.info( "Method explode( ZipFile, boolean ): STOP!" );
			return zipRootFolderPath;
		} catch( IOException e ) {
			String errorMessage = "Error while exploding Zip Archive"
					+ this.getClass().getSimpleName() + ".\r\n"
					+ "Exception: " + e.getClass().getSimpleName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw e;
		}
	}
	
}
