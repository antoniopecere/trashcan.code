package eu.pecere.utils.archives;

import java.io.File;
import java.io.IOException;

/**
 * This class expose the utility functions of the library for explode archives.
 * 
 * @author Antonio Pecere
 *
 */
public class ArchiveUtils
{
	
	public static String explode( File inputFile, boolean delete ) throws IOException
	{
		ToArchiveExploder exploder = ExploderFactory.getArchiveExploder( inputFile, delete );
		return exploder.explode();
	}
	
}
