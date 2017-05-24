package eu.pecere.utils.pdf.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.pecere.utils.commons.ResourceUtils;
import eu.pecere.utils.pdf.conversion.PdfUtils;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class PdfExtractPagesTest extends PdfMasterTestCase
{
	@Test
	public void testExtractPages() throws Exception
	{
		List<Integer> pages = new ArrayList<>();
		pages.add( 1 );
		
		String inputFilePath = thisTestFolderPartialPath + INPUT_FOLDER_NAME + "singlePage.pdf";
		String actualFolderAbsolutePath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME;
		String outputFilePath = actualFolderAbsolutePath + "singlePageExtracted.pdf";
		
		InputStream inputStream = ResourceUtils.getResourceAsStream( inputFilePath );
		File actualFolder = ResourceUtils.getOrCreateResourceFolder( actualFolderAbsolutePath );
		OutputStream outputStream = new FileOutputStream( outputFilePath );
		
		PdfUtils.extractPages( inputStream, outputStream, pages );
		
		outputStream.close();
		assertTrue( true );
	}
	
	@Test
	public void testExtractExcedingPages() throws Exception
	{
		List<Integer> pages = new ArrayList<>();
		pages.add( 45 );
		pages.add( 46 );
		pages.add( 47 );
		pages.add( 48 );
		
		String inputFilePath = thisTestFolderPartialPath + INPUT_FOLDER_NAME + "tutorial.pdf";
		String actualFolderAbsolutePath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME;
		String outputFilePath = actualFolderAbsolutePath + "pagesExtractedFromTutorial.pdf";
		
		InputStream inputStream = ResourceUtils.getResourceAsStream( inputFilePath );
		File actualFolder = ResourceUtils.getOrCreateResourceFolder( actualFolderAbsolutePath );
		OutputStream outputStream = new FileOutputStream( outputFilePath );
		
		PdfUtils.extractPages( inputStream, outputStream, pages );
		
		outputStream.close();
		assertTrue( true );
	}
}
