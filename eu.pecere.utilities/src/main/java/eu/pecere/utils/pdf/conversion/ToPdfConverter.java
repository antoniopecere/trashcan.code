package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileExtension;

/**
 * Abstract class that implements the behavior of a generic pdf converter that must be able to convert from different
 * file formats to a pdf document. Inheriting classes must implement the abstract method
 * {@link ToPdfConverter#convert(InputStream, OutputStream)} that is invoked by other methods.
 * 
 * @author Antonio Pecere
 *
 */
abstract class ToPdfConverter
{
	private static final Log log = LogFactory.getLog( ToPdfConverter.class );
	
	/**
	 * The file to be converted into pdf format.
	 */
	private File inputFile;
	
	/**
	 * Abstract constructor to make mandatory the implementation in the inheriting classes.
	 * The inputFile could be null. In that case the converter is initialized to make a direct conversion from an
	 * InputStream invoking directly the {@link ToPdfConverter#convert(InputStream, OutputStream)} method.
	 * The invoke of the other overloads will throw a {@link NullPointerException}, because the inputFile is null.
	 * 
	 * @param inputFile
	 *            The file to be converted into pdf format.
	 */
	protected ToPdfConverter( File inputFile )
	{
		super();
		this.inputFile = inputFile;
	}
	
	/**
	 * Convenience overload of the method {@link ToPdfConverter#convert(String)}.<br>
	 * It produces the output file into the same folder of the input file. If inputFile is null it throws a
	 * {@link NullPointerException} because the converter is initialized to convert directly an InputStream.
	 * Use instead {@link ToPdfConverter#convert(InputStream, OutputStream)}.
	 * 
	 * @return The file produced in output.
	 * 
	 * @throws IOException
	 *             If the conversion fails.
	 * 
	 * @see ToPdfConverter#convert(String)
	 */
	File convert() throws IOException
	{
		String outputFilePathname = this.getDefaultOutputFilePathname();
		return this.convert( outputFilePathname );
	}
	
	/**
	 * Convenience overload of the method {@link ToPdfConverter#convert(OutputStream)}.<br>
	 * It writes the output file into the specified path, closes the outputStream, and returns the file written.
	 * If inputFile is null it throws a {@link NullPointerException} because the converter is initialized to convert
	 * directly an InputStream. Use instead {@link ToPdfConverter#convert(InputStream, OutputStream)}.
	 * 
	 * @param outputFilePathname
	 *            The absolute path of the pdf file to be produced in output.
	 * 
	 * @return The file produced in output.
	 * 
	 * @throws IOException
	 *             If conversion fails or if the OutputStream.close() method fails.
	 * @throws FileNotFoundException
	 *             If the outputFile does not exists, or the outputStream creation fails.
	 * 
	 * @see ToPdfConverter#convert(OutputStream)
	 */
	File convert( String outputFilePathname ) throws IOException
	{
		File outputFile = new File( outputFilePathname );
		
		try( OutputStream outputStream = new FileOutputStream( outputFile ); ) {
			this.convert( outputStream );
		} catch( Exception e ) {
			log.error( e.getMessage(), e );
			outputFile.delete();
			throw e;
		}
		
		return outputFile;
	}
	
	/**
	 * Convenience overload of the method {@link ToPdfConverter#convert(InputStream,OutputStream)}.<br>
	 * It writes the pdf output file into the specified stream, but does not close the outputStream.
	 * If inputFile is null it throws a {@link NullPointerException} because the converter is initialized to convert
	 * directly an InputStream. Use instead {@link ToPdfConverter#convert(InputStream, OutputStream)}.
	 * 
	 * @param outputStream
	 *            The stream used to write the pdf file produced in output.
	 * 
	 * @throws IOException
	 *             If conversion fails.
	 * @throws FileNotFoundException
	 *             If the inputFile does not exists.
	 * 
	 * @see ToPdfConverter#convert(InputStream,OutputStream)
	 */
	void convert( OutputStream outputStream ) throws IOException
	{
		try( InputStream inputStream = new FileInputStream( this.getInputFile() ); ) {
			this.convert( inputStream, outputStream );
		} catch( Exception e ) {
			log.error( e.getMessage(), e );
			throw e;
		}
	}
	
	/**
	 * The abstract method to be implemented by the inheriting classes. This method has to contain the business logic of
	 * the conversion chosen by the specific implementation. It's intended to convert a generic file into a pdf format
	 * file, using the inputStream to read and the outputStream to write.<br>
	 * It writes the output file onto the specified OutputStream. It must leave the streams opened.
	 * 
	 * @param inputStream
	 *            The stream used to read the input to be converted into a pdf file.
	 * @param outputStream
	 *            The stream used to write the pdf file produced in output.
	 * 
	 * @throws IOException
	 *             If the access to the streams fails.
	 */
	abstract void convert( InputStream inputStream, OutputStream outputStream ) throws IOException;
	
	/**
	 * Utility method to read the field inputFile.<br>
	 * If inputFile is null it throws a {@link NullPointerException} because it means that the converter was initialized
	 * to convert directly an InputStream. Use instead {@link ToPdfConverter#convert(InputStream, OutputStream)}.
	 * 
	 * @return The file in the field inputFile.
	 * 
	 * @throws NullPointerException
	 *             if the inputFile is null.
	 */
	private File getInputFile()
	{
		if( this.inputFile == null )
			throw new NullPointerException( "There is no inputFile to read from!" );
		
		return this.inputFile;
	}
	
	/**
	 * Utility method to compose the output file pathname, starting from the input file pathname.
	 * 
	 * @return The absolute pathname of the output file with ".pdf" extension.
	 */
	private String getDefaultOutputFilePathname()
	{
		return ExtensionUtils.replaceExtension( this.getInputFile().getAbsolutePath(), FileExtension.PDF );
	}
	
	/**
	 * Utility method used by the inheriting converters with the responsibility to convert images from their usual
	 * formats to pdf file. It refers and use objects from iTextPdf library.
	 * 
	 * @param image
	 *            The Image object to be converted.
	 * @param outputStream
	 *            The OutputStream used to write on.
	 * 
	 * @throws DocumentException
	 *             If iTextPdf fails to create the document to be written.
	 */
	protected static void convertAndAddImage( Image image, OutputStream outputStream ) throws DocumentException
	{
		Document destinationDoc = new Document( PageSize.A4, 0, 0, 0, 0 );
		
		@SuppressWarnings( "unused" )
		PdfWriter writer = PdfWriter.getInstance( destinationDoc, outputStream );
		
		destinationDoc.open();
		
		Rectangle pageSize = destinationDoc.getPageSize();
		if( image.getWidth() > image.getHeight() )
			pageSize = pageSize.rotate();
		
		image.setAlignment( Image.ALIGN_CENTER );
		image.scaleToFit( pageSize );
		destinationDoc.setPageSize( pageSize );
		destinationDoc.newPage();
		destinationDoc.add( image );
		
		destinationDoc.close();
		// writer.close();
	}
	
	@Override
	public String toString()
	{
		return "[ name: " + this.getClass().getName() + " ]";
	}
	
}
