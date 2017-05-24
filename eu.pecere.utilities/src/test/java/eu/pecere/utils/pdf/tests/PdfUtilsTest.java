package eu.pecere.utils.pdf.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.itextpdf.text.pdf.PdfReader;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileExtension;
import eu.pecere.utils.commons.FileUtils;
import eu.pecere.utils.commons.ResourceUtils;
import eu.pecere.utils.commons.StreamConverter;
import eu.pecere.utils.pdf.conversion.PdfUtils;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class PdfUtilsTest extends PdfMasterTestCase
{
	@Test
	public void testTiffToPdfConvert() throws Exception
	{
		File inputFile = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + INPUT_FOLDER_NAME + "test.tif" );
		String checkTiffFilePath = thisTestFolderPartialPath + EXPECTED_FOLDER_NAME + "checkTiffConversion.pdf";
		File expectedOutputFile = ResourceUtils.getResourceAsFile( checkTiffFilePath );
		
		File actualOutputFile = PdfUtils.convert( inputFile );
		long actualLength = actualOutputFile.length();
		actualOutputFile.delete();
		
		assertEquals( expectedOutputFile.length(), actualLength );
	}
	
	@Test
	public void testXlsConversion() throws Exception
	{
		String excelPartialName = thisTestFolderPartialPath + INPUT_FOLDER_NAME + "ALBO LEGALI 03.05.16.xlsx";
		InputStream isExcelToConvert = ResourceUtils.getResourceAsStream( excelPartialName );
		
		// Create of the actual folder if it not exists.
		
		@SuppressWarnings( "unused" )
		File actualFolder = ResourceUtils.getOrCreateResourceFolder( thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME );
		String sExcelPdfPath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "ALBO LEGALI 03.05.16.pdf";
		OutputStream osExcelPdf = new FileOutputStream( sExcelPdfPath );
		
		PdfUtils.convert( isExcelToConvert, osExcelPdf, FileExtension.XLSX.getMainExtension() );
		osExcelPdf.close();
		
		File actualOutputFile = new File( sExcelPdfPath );
		long actualLength = actualOutputFile.length();
		actualOutputFile.delete();
		
		String expectedPartialName = thisTestFolderPartialPath + EXPECTED_FOLDER_NAME + "checkXlsxConversion.pdf";
		File expectedOutputFile = ResourceUtils.getResourceAsFile( expectedPartialName );
		
		assertEquals( expectedOutputFile.length(), actualLength );
	}
	
	@Test
	public void testPageSelector() throws Exception
	{
		File inputFilePdf = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + INPUT_FOLDER_NAME + "tutorial.pdf" );
		
		// Create of the actual folder if it not exists.
		@SuppressWarnings( "unused" )
		File actualFolder = ResourceUtils.getOrCreateResourceFolder( thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME );
		String outputFilePathname = this.thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "extractedPages.pdf";
		
		Integer[] selectedPages = { 14, 4, 6, 10, 1, 12, 5, 13 };
		List<Integer> pages = new ArrayList<>( Arrays.asList( selectedPages ) );
		
		File actualOutputFile = PdfUtils.extractPages( inputFilePdf.getAbsolutePath(), outputFilePathname, pages );
		long actualLength = actualOutputFile.length();
		actualOutputFile.delete();
		
		String expectedPartialName = thisTestFolderPartialPath + EXPECTED_FOLDER_NAME + "checkExtractedPages.pdf";
		File expectedOutputFile = ResourceUtils.getResourceAsFile( expectedPartialName );
		
		assertEquals( expectedOutputFile.length(), actualLength );
	}
	
	@Test
	public void testMultipleConversion() throws Exception
	{
		long start = ( new Date() ).getTime();
		log.info( "Start testMultipleConversion(): " + start );
		
		File[] inputFiles = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + INPUT_FOLDER_NAME ).listFiles();
		
		List<Long> actualLengths = new ArrayList<>();
		for( File inputFile : inputFiles ) {
			// Exclude conversion for files already in pdf format.
			if( ExtensionUtils.isPdf( inputFile ) )
				continue;
			
			// Excluding file that originate an output with a variable size
			String excludedFilename = "SCHEDA Danno Lesioni MACROpermanenti -2357338.xlsx";
			if( excludedFilename.equals( inputFile.getName() ) )
				continue;
			
			File outputFile = PdfUtils.convert( inputFile );
			actualLengths.add( outputFile.length() );
			log.debug( "Output file: [" + outputFile.getName() + ", " + outputFile.length() + "]" );
			outputFile.delete();
		}
		
		String expectedFolder = thisTestFolderPartialPath + EXPECTED_FOLDER_NAME + "multiple" + File.separator;
		File[] expectedFiles = ResourceUtils.getResourceAsFile( expectedFolder ).listFiles();
		
		List<Long> expectedLengths = new ArrayList<>();
		for( File file : expectedFiles ) {
			expectedLengths.add( file.length() );
		}
		
		Collections.sort( expectedLengths );
		Collections.sort( actualLengths );
		
		assertEquals( expectedLengths, actualLengths );
		
		long stop = ( new Date() ).getTime();
		
		log.info( "Start testMultipleConversion(): " + start );
		log.info( "Stop testMultipleConversion(): " + stop );
		log.info( "Difference stop-start testMultipleConversion(): " + ( stop - start ) );
	}
	
	@Test
	public void testStackSelectionConversionMerge() throws Exception
	{
		// Stream pdf da cui selezionare alcune pagine.
		InputStream isDocToSelectForm = ResourceUtils.getResourceAsStream( thisTestFolderPartialPath + INPUT_FOLDER_NAME + "tutorial.pdf" );
		
		// Lista di pagine da estrarre.
		Integer[] selectedPages = { 14, 4, 6, 10, 1, 12, 5, 13 };
		List<Integer> pages = new ArrayList<>( Arrays.asList( selectedPages ) );
		
		// Buffer in cui scrivere le pagine estratte.
		StreamConverter scPagesSelection = new StreamConverter();
		OutputStream osSelectedPages = scPagesSelection.getOutputStreamToConvert();
		
		// Estrazione pagine e scrittura nel Buffer.
		PdfUtils.extractPages( isDocToSelectForm, osSelectedPages, pages );
		
		// Buffer da cui leggere le pagine selezionate.
		InputStream isSelectedPages = scPagesSelection.getInputStreamFromConversion();
		
		// Stream della tiff da convertire in pdf.
		File fileTiffToConvert = ResourceUtils.getResourceAsFile( thisTestFolderPartialPath + INPUT_FOLDER_NAME + "test.tif" );
		InputStream isTiffToConvert = new FileInputStream( fileTiffToConvert );
		String tiffExtension = ExtensionUtils.extractExtension( fileTiffToConvert ).getMainExtension();
		
		// Buffer in cui scrivere la conversione della Tiff.
		StreamConverter cbbTiffConversion = new StreamConverter();
		OutputStream osConvertedTiff = cbbTiffConversion.getOutputStreamToConvert();
		
		// Conversione della tiff in pdf.
		PdfUtils.convert( isTiffToConvert, osConvertedTiff, tiffExtension );
		isTiffToConvert.close();
		
		// Buffer da cui leggere la tiff convertita.
		InputStream isConvertedTiff = cbbTiffConversion.getInputStreamFromConversion();
		
		// Creo la lista di file pdf di cui effettuare il merge.
		List<InputStream> isToBeMerged = new ArrayList<>();
		isToBeMerged.add( isSelectedPages );
		isToBeMerged.add( isConvertedTiff );
		
		// Create of the actual folder if it not exists.
		@SuppressWarnings( "unused" )
		File actualFolder = ResourceUtils.getOrCreateResourceFolder( thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME );
		
		// Creo lo stream in cui scrivere il merge.
		String sMergedPdfPath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "merged.pdf";
		OutputStream osMergedPdf = new FileOutputStream( sMergedPdfPath );
		
		// Leggo dal CircularBuffer, scrivo sull'outputMerge.
		PdfUtils.merge( isToBeMerged, osMergedPdf, true );
		
		osMergedPdf.close();
		
		File actualFile = new File( sMergedPdfPath );
		long actualLength = actualFile.length();
		actualFile.delete();
		
		String sExpectedFilePathname = thisTestFolderAbsolutePath + EXPECTED_FOLDER_NAME + "checkMerged.pdf";
		File expectedFile = ResourceUtils.getResourceAsFile( sExpectedFilePathname );
		long expectedLength = expectedFile.length();
		
		assertEquals( expectedLength, actualLength );
	}
	
	@Test
	public void testExtractThumbnail() throws Exception
	{
		String inputFilePath = thisTestFolderAbsolutePath + INPUT_FOLDER_NAME + "tutorial.pdf";
		String outputFilePath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "tutorial.png";
		PdfUtils.extractThumbnail( inputFilePath, outputFilePath );
		
		File actualThumbnail = new File( thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "tutorial.png" );
		File expectedThumbnail = new File( thisTestFolderAbsolutePath + EXPECTED_FOLDER_NAME + "tutorial.png" );
		
		assertEquals( expectedThumbnail.length(), actualThumbnail.length() );
	}
	
	@Test
	public void testAddMetadataToPdf() throws Exception
	{
		log.info( "Method testAddMetadataToPdf(): START" );
		
		String inputFilePath = thisTestFolderAbsolutePath + INPUT_FOLDER_NAME + "tutorial.pdf";
		String outputFilePath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "tutorialWithTags.pdf";
		
		TreeMap<String, String> metadata = new TreeMap<>();
		metadata.put( "Title", "Hello Lilliu, Hello Pecere, Hello Di Nardo" );
		metadata.put( "Subject", "New metadata" );
		metadata.put( "Keywords", "Antonio in Action, PD" );
		metadata.put( "Creator", "Antonio Pecere" );
		metadata.put( "Author", "Pecere Antonio" );
		
		PdfUtils.addMetadataToPdf( inputFilePath, outputFilePath, metadata );
		
		String expectedFilePath = thisTestFolderAbsolutePath + EXPECTED_FOLDER_NAME + "tutorialWithTags.pdf";
		PdfReader reader = new PdfReader( expectedFilePath );
		
		TreeMap<String, String> expectedMetadata = new TreeMap<>( reader.getInfo() );
		for( Map.Entry<String, String> entry : expectedMetadata.entrySet() ) {
			log.info( "Key: " + entry.getKey() + ". Value: " + entry.getValue() );
		}
		
		reader = new PdfReader( outputFilePath );
		
		TreeMap<String, String> actualMetadata = new TreeMap<>( reader.getInfo() );
		for( Map.Entry<String, String> entry : actualMetadata.entrySet() ) {
			log.info( "Key: " + entry.getKey() + ". Value: " + entry.getValue() );
		}
		
		assertTrue( expectedMetadata.keySet().equals( actualMetadata.keySet() ) );
		
		log.info( "Method testAddMetadataToPdf(): STOP" );
	}
	
	@Test
	public void testImgConversion() throws IOException
	{
		String inputFilePath = thisTestFolderAbsolutePath + INPUT_FOLDER_NAME + "carroarmato.bmp";
		String outputFilePath = thisTestFolderAbsolutePath + ACTUAL_FOLDER_NAME + "carroarmato.bmp";
		
		InputStream is = FileUtils.thumbnailator( new FileInputStream( inputFilePath ), "bmp" );
		
		byte[] buffer = new byte[2048];
		OutputStream os = new FileOutputStream( outputFilePath );
		
		int len;
		while( ( len = is.read( buffer ) ) > 0 ) {
			os.write( buffer, 0, len );
		}
		
		is.close();
		os.close();
	}
	
	@Test
	public void testCountPages() throws Exception
	{
		String inputFilePath = thisTestFolderAbsolutePath + INPUT_FOLDER_NAME + "tutorial.pdf";
		int numberOfPages = PdfUtils.extractTotalNumberOfPages( inputFilePath );
		assertEquals( 45, numberOfPages );
	}
	
}
