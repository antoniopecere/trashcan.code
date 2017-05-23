package eu.pecere.utils.commons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class ExtensionUtils
{
	public static final String[] EMAIL_EXTENSIONS = ExtensionUtils.getExtensionsForFileFilter( FileExtension.EMAIL_FILE_EXTENSIONS );
	public static final String[] IMAGE_EXTENSIONS = ExtensionUtils.getExtensionsForFileFilter( FileExtension.IMAGE_FILE_EXTENSIONS );
	public static final String[] ARCHIVE_EXTENSIONS = ExtensionUtils.getExtensionsForFileFilter( FileExtension.ARCHIVE_FILE_EXTENSIONS );
	public static final String[] PDF_EXTENSIONS = ExtensionUtils.getExtensionsForFileFilter( FileExtension.PDF_FILE_EXTENSIONS );
	public static final String[] FAKE_EXTENSIONS = ExtensionUtils.getExtensionsForFileFilter( FileExtension.FAKE_FILE_EXTENSIONS );
	public static final String[] SIGNED_EXTENSIONS = ExtensionUtils.getExtensionsForFileFilter( FileExtension.SIGNED_FILE_EXTENSIONS );
	
	public static boolean isEmail( File file )
	{
		return ExtensionUtils.isEmail( file.getName() );
	}
	
	public static boolean isEmail( String filename )
	{
		FileExtension fileExtension = ExtensionUtils.extractExtension( filename );
		if( fileExtension == null )
			return false;
		
		switch( fileExtension ) {
			case MSG:
			case EML:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isImage( File file )
	{
		return ExtensionUtils.isImage( file.getName() );
	}
	
	public static boolean isImage( String filename )
	{
		FileExtension fileExtension = ExtensionUtils.extractExtension( filename );
		if( fileExtension == null )
			return false;
		
		switch( fileExtension ) {
			case JPG:
			case PNG:
			case BMP:
			case GIF:
			case TIF:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isArchive( File file )
	{
		return ExtensionUtils.isArchive( file.getName() );
	}
	
	public static boolean isArchive( String filename )
	{
		FileExtension fileExtension = ExtensionUtils.extractExtension( filename );
		if( fileExtension == null )
			return false;
		
		switch( fileExtension ) {
			case ZIP:
			case TAR:
			case RAR:
			case SZIP:
			case ADZ:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isPdf( File file )
	{
		return ExtensionUtils.isPdf( file.getName() );
	}
	
	public static boolean isPdf( String filename )
	{
		FileExtension fileExtension = ExtensionUtils.extractExtension( filename );
		if( fileExtension == null )
			return false;
		
		switch( fileExtension ) {
			case PDF:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isFake( File file )
	{
		return ExtensionUtils.isFake( file.getName() );
	}
	
	public static boolean isFake( String filename )
	{
		FileExtension fileExtension = ExtensionUtils.extractExtension( filename );
		if( fileExtension == null )
			return false;
		
		switch( fileExtension ) {
			case FAKE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isSigned( File file )
	{
		return ExtensionUtils.isSigned( file.getName() );
	}
	
	public static boolean isSigned( String filename )
	{
		FileExtension fileExtension = ExtensionUtils.extractExtension( filename );
		if( fileExtension == null )
			return false;
		
		switch( fileExtension ) {
			case P7M:
			case P7S:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isOneOf( File file, FileExtension... fileExtensions )
	{
		FileExtension fileExtensionToCheck = ExtensionUtils.extractExtension( file );
		
		for( FileExtension fileExtension : fileExtensions ) {
			if( fileExtension == fileExtensionToCheck )
				return true;
		}
		
		return false;
	}
	
	/**
	 * Utility wrapper method for {@link FilenameUtils#getExtension(String)}, it retrieve the extension in lower case
	 * from the file specified, hiding the use of Apache Commons IO library.
	 * 
	 * @param file
	 *            The file to retreive extension from.
	 * 
	 * @return The extension retrieved.
	 */
	public static FileExtension extractExtension( File file )
	{
		String extension = FilenameUtils.getExtension( file.getAbsolutePath() );
		return FileExtension.getFileExtension( extension );
	}
	
	/**
	 * Utility wrapper method for {@link FilenameUtils#getExtension(String)}, it retrieve the extension in lower case
	 * from the path specified, hiding the use of Apache Commons IO library.
	 * 
	 * @param filePathname
	 *            The path to retreive extension from.
	 * 
	 * @return The extension retrieved.
	 */
	public static FileExtension extractExtension( String filePathname )
	{
		String extension = FilenameUtils.getExtension( filePathname );
		return FileExtension.getFileExtension( extension );
	}
	
	/**
	 * Utility wrapper method for {@link FilenameUtils#removeExtension(String)}, it removes the extension from the
	 * file's path specified, hiding the use of Apache Commons IO library.
	 * 
	 * @param file
	 *            The file to remove extension from.
	 * 
	 * @return The path of the file specified, without its extension.
	 */
	public static String removeExtension( File file )
	{
		return FilenameUtils.removeExtension( file.getAbsolutePath() );
	}
	
	/**
	 * Utility wrapper method for {@link FilenameUtils#removeExtension(String)}, it removes the extension from the
	 * path specified, hiding the use of Apache Commons IO library.
	 * 
	 * @param filePathname
	 *            The path to remove extension from.
	 * 
	 * @return The path specified, without its extension.
	 */
	public static String removeExtension( String filePathname )
	{
		return FilenameUtils.removeExtension( filePathname );
	}
	
	public static String replaceExtension( String filePathname, String newExtension )
	{
		FileExtension newFileExtension = FileExtension.getFileExtension( newExtension );
		return ExtensionUtils.replaceExtension( filePathname, newFileExtension );
	}
	
	public static String replaceExtension( String filePathname, FileExtension newFileExtension )
	{
		return ExtensionUtils.removeExtension( filePathname ) + newFileExtension.getMainDotExtension();
	}
	
	public static File renameToNewExtension( File file, String newExtension ) throws IOException
	{
		FileExtension newFileExtension = FileExtension.getFileExtension( newExtension );
		return ExtensionUtils.renameToNewExtension( file, newFileExtension );
	}
	
	public static File renameToNewExtension( File fileToRename, FileExtension newFileExtension ) throws IOException
	{
		String filename = fileToRename.getName();
		String newFilename = ExtensionUtils.replaceExtension( filename, newFileExtension );
		File renamedFile = FileUtils.renameAndGet( fileToRename, newFilename );
		return renamedFile;
	}
	
	public static String[] getExtensionsForFileFilter( FileExtension... fileExtensions )
	{
		if( fileExtensions == null || fileExtensions.length == 0 )
			return null;
		
		if( fileExtensions.length == 1 )
			return fileExtensions[0].getExtensionsForFileFilter();
		
		List<String> extensions = new ArrayList<>();
		for( FileExtension fileExtension : fileExtensions ) {
			extensions.addAll( Arrays.asList( fileExtension.getExtensionsForFileFilter() ) );
		}
		
		return extensions.toArray( new String[0] );
	}
}
