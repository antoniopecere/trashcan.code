package eu.pecere.utils.pdf.conversion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileExtension;
import eu.pecere.utils.pdf.libreoffice.LibreOfficeStreamConverter;

/**
 * This class expose the utility functions of the library for conversion, select and merge documents.
 * 
 * @author Antonio Pecere
 *
 */
public class PdfUtils
{
	public static final String[] PDF_CONVERTIBLE_EXTENSIONS = ConverterFactory.CONVERTER_MANAGED_EXTENSIONS;
	public static final FileExtension[] PDF_CONVERTIBLE_FILE_EXTENSIONS = ConverterFactory.CONVERTER_MANAGED_FILE_EXTENSIONS;
	
	public static boolean isConvertible( File inputFile )
	{
		return PdfUtils.isConvertible( inputFile.getName() );
	}
	
	public static boolean isConvertible( String inputFilePathname )
	{
		FileExtension fileExtToCheck = ExtensionUtils.extractExtension( inputFilePathname );
		for( FileExtension fileExt : ConverterFactory.CONVERTER_MANAGED_FILE_EXTENSIONS ) {
			if( fileExtToCheck == fileExt ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Converts an input stream containing a file of a specified type, into a pdf file to be written into a specified
	 * output stream. It leaves the streams opened.
	 * 
	 * @param inputStream
	 *            The input stream containing the file to be converted.
	 * @param outputStream
	 *            The output stream to write the produced pdf into.
	 * @param inputFileExtension
	 *            The extension of the file contained into the input stream.
	 * 
	 * @throws IOException
	 *             If the conversion fails.
	 */
	public static void convert( InputStream inputStream, OutputStream outputStream, String inputFileExtension ) throws IOException
	{
		ToPdfConverter converter = ConverterFactory.getPdfConverter( inputFileExtension );
		
		if( converter == null ) {
			throw new NullPointerException( "No converter for extension [ " + inputFileExtension + " ]." );
		}
		
		converter.convert( inputStream, outputStream );
	}
	
	/**
	 * Converts a file placed in a specified pathname, producing a pdf file placed into the specified pathname.
	 * 
	 * @param inputFilePathname
	 *            The pathname of the file to be converted.
	 * @param outputFilePathname
	 *            The pathname to write the produced pdf into.
	 * 
	 * @return The object file produced, containing the pdf document.
	 * 
	 * @throws IOException
	 *             If the conversion fails.
	 */
	public static File convert( String inputFilePathname, String outputFilePathname ) throws IOException
	{
		File inputFile = new File( inputFilePathname );
		ToPdfConverter converter = ConverterFactory.getPdfConverter( inputFile );
		
		if( converter == null ) {
			throw new NullPointerException( "No converter for file [ " + inputFile.getName() + " ]." );
		}
		
		return converter.convert( outputFilePathname );
	}
	
	/**
	 * Converts a file producing a pdf file placed into the specified pathname.
	 * 
	 * @param inputFile
	 *            The file to be converted.
	 * @param outputFilePathname
	 *            The pathname to write the produced pdf into.
	 * 
	 * @return The object file produced, containing the pdf document.
	 * 
	 * @throws IOException
	 *             If the conversion fails.
	 */
	public static File convert( File inputFile, String outputFilePathname ) throws IOException
	{
		ToPdfConverter converter = ConverterFactory.getPdfConverter( inputFile );
		
		if( converter == null ) {
			throw new NullPointerException( "No converter for file [ " + inputFile.getName() + " ]." );
		}
		
		return converter.convert( outputFilePathname );
	}
	
	/**
	 * Converts a file producing a pdf file to be written into a specified output stream. It leaves the streams opened.
	 * 
	 * @param inputFile
	 *            The file to be converted.
	 * @param outputStream
	 *            The output stream to write the produced pdf into.
	 * 
	 * @throws IOException
	 *             If the conversion fails.
	 */
	public static void convert( File inputFile, OutputStream outputStream ) throws IOException
	{
		ToPdfConverter converter = ConverterFactory.getPdfConverter( inputFile );
		
		if( converter == null ) {
			throw new NullPointerException( "No converter for file [ " + inputFile.getName() + " ]." );
		}
		
		converter.convert( outputStream );
	}
	
	/**
	 * Converts a file, producing a pdf file into the same directory of the input file, with the same name but the pdf
	 * extension.
	 * 
	 * @param inputFile
	 *            The file to be converted.
	 * 
	 * @return The object file produced, containing the pdf document.
	 * 
	 * @throws IOException
	 *             If the conversion fails.
	 */
	public static File convert( File inputFile ) throws IOException
	{
		ToPdfConverter converter = ConverterFactory.getPdfConverter( inputFile );
		
		if( converter == null ) {
			throw new NullPointerException( "No converter for file [ " + inputFile.getName() + " ]." );
		}
		
		return converter.convert();
	}
	
	/**
	 * Merges multiple pdf into one pdf starting from a list of input streams and producing an output stream. The
	 * outputStream will not be closed.<br>
	 * If the <strong>smartCopy</strong> option is <strong>true</strong>, the merge will use
	 * <strong>PdfSmartCopy</strong> instead of <strong>PdfCopy</strong>.<br>
	 * PdfSmartCopy has the same functionality as PdfCopy, but when resources (such as fonts, images,...) are
	 * encountered, a reference to these resources is saved in a cache, so that they can be reused. This requires more
	 * memory, but reduces the file size of the resulting PDF document.
	 * 
	 * @param inputStreams
	 *            The list of pdf input streams to be merged.
	 * @param outputStream
	 *            The output stream to write the output produced by merged files.
	 * @param smartCopy
	 *            A boolean to chose to use smartCopy option.
	 * 
	 * @throws IOException
	 *             If the PdfReader fails reading from one of the input streams, or if the attempt to copy fails writing
	 *             merged file into the output stream.
	 */
	public static void merge( List<InputStream> inputStreams, OutputStream outputStream, Boolean smartCopy )
			throws IOException
	{
		try {
			Document document = new Document();
			
			PdfCopy copy;
			if( smartCopy )
				copy = new PdfSmartCopy( document, outputStream );
			else
				copy = new PdfCopy( document, outputStream );
			
			document.open();
			for( InputStream is : inputStreams ) {
				PdfReader reader = new PdfReader( is );
				copy.addDocument( reader );
				reader.close();
			}
			document.close();
			
			// outputStream.flush();
		} catch( DocumentException e ) {
			throw new IOException( e.getMessage(), e );
		}
	}
	
	/**
	 * Convenience overload of the method {@link PdfUtils#merge(List, OutputStream, Boolean)}.<br>
	 * It starts from a list of pdf files to be merged, and returns the file produced on the path outputFilePathname.
	 * 
	 * @param files
	 *            The list of the pdf files to be merged.
	 * @param outputStream
	 *            The output stream to write the output produced by merged files.
	 * @param smartCopy
	 *            A boolean to chose to use smartCopy option.
	 * 
	 * @throws IOException
	 *             If the PdfReader fails reading from one of the files, or if the attempt to copy fails writing merged
	 *             file into the output stream.
	 *
	 * @see PdfUtils#merge(List, OutputStream, Boolean)
	 */
	public static void merge( List<File> files, OutputStream outputStream, boolean smartCopy )
			throws IOException
	{
		List<InputStream> inputStreams = new ArrayList<>();
		
		try {
			
			for( File file : files ) {
				inputStreams.add( new FileInputStream( file ) );
			}
			
			PdfUtils.merge( inputStreams, outputStream, true );
			
		} catch( Exception e ) {
			
			throw e;
			
		} finally {
			
			for( InputStream inputStream : inputStreams ) {
				if( inputStream != null )
					inputStream.close();
			}
			
		}
		
	}
	
	public static File merge( List<File> files, String outputFilePathname, boolean smartCopy ) throws IOException
	{
		List<String> filePathnames = new ArrayList<>();
		for( File file : files ) {
			filePathnames.add( file.getAbsolutePath() );
		}
		
		return PdfUtils.merge( filePathnames, outputFilePathname, smartCopy );
	}
	
	/**
	 * Convenience overload of the method {@link PdfUtils#merge(List, OutputStream, Boolean)}.<br>
	 * It starts from a list of pathnames related to the pdf files to be merged, and returns the file produced on the
	 * path outputFilePathname.
	 * 
	 * @param filePathnames
	 *            The list of the pdf file pathnames to be merged.
	 * @param outputFilePathname
	 *            The path to create the stream to write the output produced merging files.
	 * @param smartCopy
	 *            A boolean to chose to use smartCopy option.
	 * 
	 * @return The file produced by merge operation.
	 * 
	 * @throws IOException
	 *             If one of the pathnames is not found or if reading from one of it fails, or if the attempt to produce
	 *             the output file fails.
	 * 
	 * @see PdfUtils#merge(List, OutputStream, Boolean)
	 */
	public static File merge( List<String> filePathnames, String outputFilePathname, Boolean smartCopy ) throws IOException
	{
		File outputFile = new File( outputFilePathname );
		
		List<InputStream> inputStreams = new ArrayList<>();
		
		try( OutputStream outputStream = new FileOutputStream( outputFile ); ) {
			
			for( String filePathname : filePathnames ) {
				inputStreams.add( new FileInputStream( filePathname ) );
			}
			
			PdfUtils.merge( inputStreams, outputStream, smartCopy );
			
		} catch( Exception e ) {
			
			outputFile.delete();
			throw e;
			
		} finally {
			
			for( InputStream inputStream : inputStreams ) {
				if( inputStream != null )
					inputStream.close();
			}
		}
		
		return outputFile;
	}
	
	/**
	 * This method use the class {@link ThumbnailExtractor} to extract the Thumbnail.
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @throws IOException
	 * 
	 * @see {@link eu.pecere.utils.pdf.conversion.ThumbnailExtractor#convertPDFToImage(InputStream, OutputStream)}
	 */
	public static void extractThumbnail( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		ThumbnailExtractor.convertPDFToImage( inputStream, outputStream );
	}
	
	/**
	 * Convenience overload of the method {@link #extractThumbnail(InputStream, OutputStream)}.<br>
	 * The overload method accept in input the input file name path and the output file name path.
	 * 
	 * @param inputFilePathname
	 * @param outputFilePathname
	 * @return
	 * @throws IOException
	 */
	public static File extractThumbnail( String inputFilePathname, String outputFilePathname ) throws IOException
	{
		File inputFile = new File( inputFilePathname );
		File outputFile = PdfUtils.extractThumbnail( inputFile, outputFilePathname );
		return outputFile;
	}
	
	public static File extractThumbnail( File inputFile, String outputFilePathname ) throws IOException
	{
		
		File outputFile = new File( outputFilePathname );
		try(
				InputStream inputStream = new FileInputStream( inputFile );
				OutputStream outputStream = new FileOutputStream( outputFile ); ) {
			
			PdfUtils.extractThumbnail( inputStream, outputStream );
			
		} catch( Exception e ) {
			outputFile.delete();
			throw e;
		}
		
		return outputFile;
	}
	
	/**
	 * Select the specified pages from the input stream and write into the output stream a new pdf file containing the
	 * selected pages. <strong>This method does not close the streams</strong>.<br>
	 * To specify with the right syntax, the sequence of pages to be selected, is mandatory use the same pattern used to
	 * print them. For example to print those pages <strong>(1, 4, 5, 6, 10, 12, 13, 14)</strong> the
	 * <strong>rangeSequence</strong> must be written in that way: <strong>"1, 4-6, 10, 12-14"</strong>.
	 * 
	 * @param inputStream
	 *            The input stream containing the pdf document to read from.
	 * @param outputStream
	 *            The output stream to write the selected pages into.
	 * @param rangeSequence
	 *            The range sequence of pages to be selected.
	 * 
	 * @throws IOException
	 *             If the PdfReader fails reading from input stream, or if the PdfSmartCopy fails writing into output
	 *             stream.
	 */
	public static void extractPages( InputStream inputStream, OutputStream outputStream, String rangeSequence ) throws IOException
	{
		try {
			PdfReader reader = new PdfReader( inputStream );
			reader.selectPages( rangeSequence );
			
			int n = reader.getNumberOfPages();
			Document document = new Document();
			
			PdfSmartCopy copy = new PdfSmartCopy( document, outputStream );
			
			document.open();
			for( int i = 0; i < n; ) {
				copy.addPage( copy.getImportedPage( reader, ++i ) );
			}
			document.close();
		} catch( DocumentException e ) {
			throw new IOException( e.getMessage(), e );
		}
	}
	
	/**
	 * Convenience overload of the method {@link PdfUtils#extractPages(InputStream, OutputStream, String)}.<br>
	 * This overload accept a list of integers containing the page numbers of the pages to be selected.
	 * The list will be converted into a string with the right syntax before invoking the main method.
	 * For example this list of page numbers <strong>{ 14, 4, 6, 10, 1, 12, 5, 13 }</strong> will be ordered and
	 * converted into this <strong>rangeSequence</strong>: <strong>"1, 4-6, 10, 12-14"</strong>.
	 * 
	 * @param inputStream
	 *            The input stream containing the pdf document to read from.
	 * @param outputStream
	 *            The output stream to write the selected pages into.
	 * @param pages
	 *            A list of integers containing the numbers of the pages to be selected.
	 * 
	 * @throws IOException
	 *             If the attempt to read from the input stream fails, or if the attempt to write into the output stream
	 *             fails.
	 * 
	 * @see PdfUtils#extractPages(InputStream, OutputStream, String)
	 */
	public static void extractPages( InputStream inputStream, OutputStream outputStream, List<Integer> pages ) throws IOException
	{
		String rangeSequence = PdfUtils.rangeSequence( pages );
		PdfUtils.extractPages( inputStream, outputStream, rangeSequence );
	}
	
	/**
	 * Convenience overload of the method {@link PdfUtils#extractPages(InputStream, OutputStream, String)}.<br>
	 * This overload selects the specified pages from the file placed at the input pathname and writes to the output
	 * pathname, a new pdf file containing the selected pages. Then the streams will be closed and the produced file is
	 * returned.<br>
	 * To specify with the right syntax, the sequence of pages to be selected, is mandatory use the same pattern used to
	 * print them. For example to print those pages <strong>(1, 4, 5, 6, 10, 12, 13, 14)</strong> the
	 * <strong>rangeSequence</strong> must be written in that way: <strong>"1, 4-6, 10, 12-14"</strong>.
	 * 
	 * @param inputFilePathname
	 *            The pathname of the pdf file to read from.
	 * @param outputFilePathname
	 *            The pathname of the pdf file to be created with the selected pages.
	 * @param rangeSequence
	 *            The range sequence of pages to be selected.
	 * 
	 * @return The created pdf file containing the selected pages.
	 * 
	 * @throws IOException
	 *             If a file is not found at the specified path or if the attempt to read it fails, or if the attempt to
	 *             produce the output file fails.
	 * 
	 * @see PdfUtils#extractPages(InputStream, OutputStream, String)
	 */
	public static File extractPages( String inputFilePathname, String outputFilePathname, String rangeSequence ) throws IOException
	{
		InputStream inputStream = new FileInputStream( inputFilePathname );
		File outputFile = new File( outputFilePathname );
		OutputStream outputStream = new FileOutputStream( outputFile );
		
		PdfUtils.extractPages( inputStream, outputStream, rangeSequence );
		outputStream.close();
		inputStream.close();
		
		return outputFile;
	}
	
	/**
	 * Convenience overload of the method {@link PdfUtils#extractPages(String, String, String)}.<br>
	 * This overload accept a list of integers containing the page numbers of the pages to be selected.
	 * The list will be converted into a string with the right syntax before invoking the main method.
	 * For example this list of page numbers <strong>{ 14, 4, 6, 10, 1, 12, 5, 13 }</strong> will be ordered and
	 * converted into this <strong>rangeSequence</strong>: <strong>"1, 4-6, 10, 12-14"</strong>.
	 * 
	 * @param inputFilePathname
	 *            The pathname of the pdf file to read from.
	 * @param outputFilePathname
	 *            The pathname of the pdf file to be created with the selected pages.
	 * @param pages
	 *            A list of integers containing the numbers of the pages to be selected.
	 * 
	 * @return The created pdf file containing the selected pages.
	 * 
	 * @throws IOException
	 *             If the input file is not found at the specified path or if the attempt to read it fails, or if the
	 *             attempt to produce the output file fails.
	 * 
	 * @see PdfUtils#extractPages(String, String, String)
	 */
	public static File extractPages( String inputFilePathname, String outputFilePathname, List<Integer> pages )
			throws IOException
	{
		String rangeSequence = PdfUtils.rangeSequence( pages );
		return PdfUtils.extractPages( inputFilePathname, outputFilePathname, rangeSequence );
	}
	
	/**
	 * Convenience overload of the method {@link PdfUtils#extractTotalNumberOfPages(InputStream)}.<br>
	 * Utility method to extract the total number of pages from a PDF document.
	 * 
	 * @param inputFilePathname
	 *            The absolute path to the PDF document to extract the total number of pages from.
	 * 
	 * @return The total number of pages of the specified document.
	 * 
	 * @throws IOException
	 *             If the extraction fails for any reason.
	 * 
	 * @see PdfUtils#extractTotalNumberOfPages(InputStream)
	 */
	public static Integer extractTotalNumberOfPages( String inputFilePathname ) throws IOException
	{
		File inputFile = new File( inputFilePathname );
		return PdfUtils.extractTotalNumberOfPages( inputFile );
	}
	
	/**
	 * Convenience overload of the method {@link PdfUtils#extractTotalNumberOfPages(InputStream)}.<br>
	 * Utility method to extract the total number of pages from a PDF document.
	 * 
	 * @param inputFile
	 *            The object file pointing to the PDF document to extract the total number of pages from.
	 * 
	 * @return The total number of pages of the specified document.
	 * 
	 * @throws IOException
	 *             If the extraction fails for any reason.
	 * 
	 * @see PdfUtils#extractTotalNumberOfPages(InputStream)
	 */
	public static Integer extractTotalNumberOfPages( File inputFile ) throws IOException
	{
		try( InputStream inputStream = new FileInputStream( inputFile ) ) {
			return PdfUtils.extractTotalNumberOfPages( inputStream );
		} catch( IOException e ) {
			throw e;
		}
	}
	
	/**
	 * Utility method to extract the total number of pages from a PDF document.
	 * 
	 * @param inputStream
	 *            The stream related to the PDF document to extract the total number of pages from.
	 * 
	 * @return The total number of pages of the specified document.
	 * 
	 * @throws IOException
	 *             If the extraction fails for any reason.
	 * 
	 */
	public static Integer extractTotalNumberOfPages( InputStream inputStream ) throws IOException
	{
		Integer numberOfPages = null;
		
		PdfReader reader = null;
		try {
			RandomAccessSource ras = new RandomAccessSourceFactory().createSource( inputStream );
			RandomAccessFileOrArray rafa = new RandomAccessFileOrArray( ras );
			reader = new PdfReader( rafa, new byte[0] );
			numberOfPages = reader.getNumberOfPages();
		} catch( IOException e ) {
			throw e;
		} finally {
			if( reader != null )
				reader.close();
		}
		
		return numberOfPages;
	}
	
	/**
	 * Utility public method to add metadata to an existing PDF file, this method will write the new PDF with metadata
	 * in the outoutFilePathname and will return it.
	 * 
	 * @param inputFilePathname
	 * @param outputFilePathname
	 * @param metadata
	 * @return
	 * @throws Exception
	 */
	public static File addMetadataToPdf( String inputFilePathname, String outputFilePathname, TreeMap<String, String> metadata )
			throws Exception
	{
		InputStream inputStream = new FileInputStream( inputFilePathname );
		File outputFile = new File( outputFilePathname );
		OutputStream outputStream = new FileOutputStream( outputFile );
		
		PdfUtils.addMetadataToPdf( inputStream, outputStream, metadata );
		
		outputStream.close();
		inputStream.close();
		
		return outputFile;
	}
	
	/**
	 * Convenience overload method of {@link #addMetadataToPdf(String, String, Map)}
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @param metadata
	 * @throws Exception
	 */
	public static void addMetadataToPdf( InputStream inputStream, OutputStream outputStream, TreeMap<String, String> metadata )
			throws Exception
	{
		PdfManipulator.addMetadataToPdf( inputStream, outputStream, metadata );
	}
	
	/**
	 * 
	 * @param htmlBody
	 * @param outputStream
	 * @throws IOException
	 */
	public static void convertHtmlToPdf( String htmlBody, OutputStream outputStream ) throws IOException
	{
		Document document = null;
		InputStream inputStream = null;
		try {
			document = new Document();
			PdfWriter writer = PdfWriter.getInstance( document, outputStream );
			document.open();
			
			inputStream = new ByteArrayInputStream( htmlBody.getBytes() );
			XMLWorkerHelper.getInstance().parseXHtml( writer, document, inputStream );
			
		} catch( DocumentException e ) {
			throw new IOException( e.getMessage(), e );
		} finally {
			if( inputStream != null )
				inputStream.close();
			if( document != null )
				document.close();
		}
	}
	
	/**
	 * Utility method to convert a List of page numbers into a string with the correct patter to selection of the pages
	 * into a pdf file. For example this list of page numbers <strong>{ 14, 4, 6, 10, 1, 12, 5, 13 }</strong> will be
	 * ordered and converted into this string: <strong>"1, 4-6, 10, 12-14"</strong>.
	 * 
	 * @param pages
	 *            A list of integers containing the numbers of the pages to be selected.
	 * 
	 * @return The string composed with the right syntax to select pages from a pdf file.
	 */
	private static String rangeSequence( List<Integer> pages )
	{
		if( pages.size() == 1 )
			return pages.get( 0 ).toString();
		
		// Es.: pages: { 14, 4, 6, 10, 1, 12, 5, 13 }
		Collections.sort( pages );
		
		List<String> ranges = new ArrayList<>();
		
		Integer rangeStart = null;
		Integer rangeStop = null;
		
		// Es.: pages: { 1, 4, 5, 6, 10, 12, 13, 14 }
		for( int i = 0; i < pages.size(); i++ ) {
			// page: //1 //4 //5 //6 //10 //12 //13 //14
			Integer page = pages.get( i );
			
			if( rangeStart == null || rangeStop == null ) {
				rangeStart = page; // 1
				rangeStop = page; // 1
				continue;
			} else {
				// test: //4==2 //5==5 //6==6 //10==7 //12==11 //13==13 //14==14
				if( page == rangeStop + 1 ) {
					rangeStop = page; // rangeStop: //5 //6 //13 //14
					if( i == pages.size() - 1 ) {
						// add: //"12-14"
						ranges.add( rangeStart + "-" + rangeStop );
					}
				} else {
					// test: / 1==1 //4==6 //10==10
					if( rangeStart.equals( rangeStop ) ) {
						// add: //"1" //"10"
						ranges.add( rangeStop.toString() );
					} else {
						// add: //"4-6"
						ranges.add( rangeStart + "-" + rangeStop );
					}
					
					rangeStart = page; // rangeStart: //4 //10 //12
					rangeStop = page; // rangeStop: //4 //10 //12
					if( i == pages.size() - 1 ) {
						// add: //"20"
						ranges.add( rangeStop.toString() );
					}
				}
			}
		}
		
		// Es.: 1, 4-6, 10, 12-14
		return ranges.toString().replace( "[", "" ).replace( "]", "" );
	}
	
	public static void tryEventuallyReset() throws IOException
	{
		LibreOfficeStreamConverter losc = LibreOfficeStreamConverter.getInstance();
		losc.reset();
	}
}
