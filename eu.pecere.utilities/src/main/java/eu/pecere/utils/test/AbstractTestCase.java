package eu.pecere.utils.test;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.pecere.utils.commons.ResourceUtils;

/**
 * 
 * @author Antonio Pecere: 10 giu 2016
 *
 */
public abstract class AbstractTestCase
{
	protected final Log log = LogFactory.getLog( this.getClass() );
	
	protected static final String DEFAULT_TEST_FILES_ROOT_FOLDER_NAME = "testFiles";
	
	protected static final String INPUT_FOLDER_NAME = "input" + File.separator;
	protected static final String ACTUAL_FOLDER_NAME = "actual" + File.separator;
	protected static final String EXPECTED_FOLDER_NAME = "expected" + File.separator;
	
	//@formatter:off
	protected final String thisTestFolderPartialPath =
			this.getThisTestFolderRootName() + File.separator +
			this.getClass().getSimpleName() + File.separator;
	
	protected final File thisTestFolder = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath );
	
	protected final String thisTestFolderAbsolutePath =
			( thisTestFolder != null ? thisTestFolder.getAbsolutePath() : this.getThisTestFolderRootName() )
			+ File.separator;
	//@formatter:on
	
	protected String getThisTestFolderRootName()
	{
		return DEFAULT_TEST_FILES_ROOT_FOLDER_NAME;
	}
	
}
