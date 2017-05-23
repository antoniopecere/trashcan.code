package eu.pecere.utils.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Antonio Pecere: 10 giu 2016
 *
 */
public class FileUtils
{
	public static Boolean isEmpty( File file )
	{
		if( file.isDirectory() ) {
			return file.list().length == 0;
		} else {
			return null;
		}
	}
	
	public static boolean move( String fileToMovePath, String targetDirectoryPath )
	{
		File fileToMove = new File( fileToMovePath );
		return FileUtils.move( fileToMove, targetDirectoryPath );
	}
	
	public static boolean move( File fileToMove, String targetDirectoryPath )
	{
		File targetDirectory = new File( targetDirectoryPath );
		
		if( !targetDirectory.exists() )
			targetDirectory.mkdirs();
		
		return FileUtils.move( fileToMove, targetDirectory );
	}
	
	public static boolean move( File fileToMove, File targetDirectory )
	{
		try {
			Path source = fileToMove.toPath();
			Path target = targetDirectory.toPath().resolve( source.getFileName() );
			Files.move( source, target, StandardCopyOption.REPLACE_EXISTING );
		} catch( IOException e ) {
			return false;
		}
		return true;
	}
	
	public static boolean copy( File from, File to )
	{
		try( OutputStream out = new FileOutputStream( to ) ) {
			Files.copy( from.toPath(), out );
		} catch( Throwable e ) {
			return false;
		}
		return true;
	}
	
	public static boolean rename( File fileToRename, String newFilename )
	{
		try {
			FileUtils.renameAndGet( fileToRename, newFilename );
		} catch( IOException e ) {
			return false;
		}
		return true;
	}
	
	public static File renameAndGet( File fileToRename, String newFilename ) throws IOException
	{
		Path source = fileToRename.toPath();
		Path target = source.resolveSibling( newFilename );
		target = Files.move( source, target, StandardCopyOption.REPLACE_EXISTING );
		File renamedFile = target.toFile();
		
		if( !renamedFile.exists() ) {
			String errorMessage = "Rename af file [" + fileToRename.getName() + "] has failed! "
					+ "Target file [" + newFilename + "] has been not created!";
			throw new IOException( errorMessage );
		}
		
		return renamedFile;
	}
	
	public static List<File> listFiles( String folderPathname )
	{
		return FileUtils.listFiles( new File( folderPathname ) );
	}
	
	public static List<File> listFiles( File folder )
	{
		//@formatter:off
		Collection<File> unfilteredFileCollection =
			org.apache.commons.io.FileUtils.listFiles(
				folder,
				TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE );
		//@formatter:on
		
		return new ArrayList<>( unfilteredFileCollection );
	}
	
	public static List<File> unfilteredListFiles( String folderPathname, boolean doRecursion )
	{
		return FileUtils.listFiles( folderPathname, doRecursion, (FileExtension[]) null );
	}
	
	public static List<File> unfilteredListFiles( File folder, boolean doRecursion )
	{
		return FileUtils.listFiles( folder, doRecursion, (FileExtension[]) null );
	}
	
	public static List<File> listFiles( String folderPathname, boolean doRecursion, FileExtension... fileExtensions )
	{
		return FileUtils.listFiles( new File( folderPathname ), doRecursion, fileExtensions );
	}
	
	public static List<File> listFiles( File folder, boolean doRecursion, FileExtension... fileExtensions )
	{
		String[] extensions = ExtensionUtils.getExtensionsForFileFilter( fileExtensions );
		return FileUtils.listFiles( folder, doRecursion, extensions );
	}
	
	private static List<File> listFiles( File folder, boolean doRecursion, String... extensions )
	{
		//@formatter:off
		Collection<File> filteredFileCollection =
			org.apache.commons.io.FileUtils.listFiles( folder, extensions, doRecursion );
		//@formatter:on
		
		return new ArrayList<>( filteredFileCollection );
	}
	
	private static void deleteFiles( String folderPathname, String... extensions )
	{
		if( extensions == null || extensions.length == 0 )
			return;
		
		File folder = new File( folderPathname );
		
		if( !folder.exists() || !folder.isDirectory() )
			return;
		
		List<File> filesToDelete = FileUtils.listFiles( folder, true, extensions );
		for( File file : filesToDelete ) {
			file.delete();
		}
	}
	
	public static void deleteFiles( String folderPathname, FileExtension... fileExtensions )
	{
		String[] extensionsForFilter = ExtensionUtils.getExtensionsForFileFilter( fileExtensions );
		FileUtils.deleteFiles( folderPathname, extensionsForFilter );
	}
	
	public static boolean deleteDirectory( String folderPathname )
	{
		return deleteDirectory( new File( folderPathname ) );
	}
	
	public static boolean deleteDirectory( File folder )
	{
		try {
			org.apache.commons.io.FileUtils.deleteDirectory( folder );
			return true;
		} catch( Exception e ) {
			return false;
		}
	}
	
	public static String commonFolderPathname( String[] paths, String fileSeparator )
	{
		String commonPath = "";
		String[][] folders = new String[paths.length][];
		for( int i = 0; i < paths.length; i++ ) {
			if( File.separator.equals( fileSeparator ) ) {
				folders[i] = StringUtils.split( paths[i], fileSeparator );
			} else {
				folders[i] = paths[i].split( fileSeparator );
			}
		}
		for( int j = 0; j < folders[0].length; j++ ) {
			String thisFolder = folders[0][j];
			boolean allMatched = true;
			for( int i = 1; i < folders.length && allMatched; i++ ) {
				if( folders[i].length < j ) {
					allMatched = false;
					break;
				}
				
				allMatched &= folders[i][j].equals( thisFolder ); // equivalent to a = a & b;
			}
			if( allMatched ) {
				commonPath += thisFolder + fileSeparator;
			} else {
				break;
			}
		}
		return commonPath;
	}
	
	public static String commonFolderPathname( List<String> pathList, String fileSeparator )
	{
		String[] paths = pathList.toArray( new String[0] );
		return commonFolderPathname( paths, fileSeparator );
	}
	
	public static String commonFolderPathname( File[] files, String fileSeparator )
	{
		List<String> pathList = new ArrayList<>();
		
		for( File file : files ) {
			pathList.add( file.getAbsolutePath() );
		}
		
		return commonFolderPathname( pathList, fileSeparator );
	}
	
	public static List<String> fileNameOrderedList( File folder )
	{
		List<File> fileList = FileUtils.listFiles( folder );
		List<String> nameList = new ArrayList<>();
		
		for( File file : fileList ) {
			nameList.add( file.getName() );
		}
		
		Collections.sort( nameList );
		return nameList;
	}
	
	public static List<String> fileNameOrderedList( String folderPathname )
	{
		File folder = new File( folderPathname );
		return fileNameOrderedList( folder );
	}
	
	/**
	 * Create temporary file into user temp folder.
	 * 
	 * @return The temporary file created, or <b>null</b> if creation has been failed.
	 * 
	 */
	public static File createTempFile()
	{
		try {
			return Files.createTempFile( null, null ).toFile();
		} catch( Exception e ) {
			return null;
		}
	}
	
}
