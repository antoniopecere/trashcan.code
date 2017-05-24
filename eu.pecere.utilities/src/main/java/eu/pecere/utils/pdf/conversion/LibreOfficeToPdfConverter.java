package eu.pecere.utils.pdf.conversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.pecere.utils.pdf.libreoffice.LibreOfficeFilterNames;
import eu.pecere.utils.pdf.libreoffice.LibreOfficeStreamConverter;

/**
 * This class implements a specific converter for documents in the Office formats.
 * It delegate the conversion to an instance of LibreOffice, using the class {@link LibreOfficeStreamConverter}.
 * 
 * @author Antonio Pecere
 * 
 */
class LibreOfficeToPdfConverter extends ToPdfConverter
{
	private static final Log log = LogFactory.getLog( LibreOfficeToPdfConverter.class );
	
	private LibreOfficeFilterNames.In inputFilterName;
	
	private static Integer errorCounter = 0;
	
	protected LibreOfficeToPdfConverter( File inputFile )
	{
		this( inputFile, null );
	}
	
	/**
	 * Constructor using fields. It adds to the default constructor, the field <strong>inputFilterName</strong> related
	 * to the type of the input file containing the document.
	 * 
	 * @param inputFile
	 *            The file containing the document to be converted.
	 * @param inputFilterName
	 *            The FilterName related to the type of the document to be converted.
	 */
	protected LibreOfficeToPdfConverter( File inputFile, LibreOfficeFilterNames.In inputFilterName )
	{
		super( inputFile );
		this.inputFilterName = inputFilterName;
	}
	
	@Override
	protected void convert( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		log.info( "Java conversion from " + this.inputFilterName + " to PDF: Started!" );
		LibreOfficeStreamConverter loStreamConverter = null;
		try {
			// Create LibreOffice converter.
			loStreamConverter = LibreOfficeStreamConverter.getInstance();
			
			// Convert document stream to PDF stream.
			loStreamConverter.convert(
					inputStream,
					outputStream,
					inputFilterName.getFilterName(),
					inputFilterName.getRelatedOutputFilter().getFilterName() );
			
			// Close LibreOffice running instance.
			// loStreamConverter.reset();
		} catch( IOException e ) {
			log.error( e.getMessage(), e );
			
			if( errorCounter++ < 5 ) {
				if( loStreamConverter != null )
					loStreamConverter.reset();
				
				this.convert( inputStream, outputStream );
			} else
				throw e;
		}
		
		log.info( "Java conversion from " + this.inputFilterName + " to PDF: Completed!" );
	}
	
	@Override
	public String toString()
	{
		return "[ name: " + this.getClass().getName() + "( " + this.inputFilterName + " ) ]";
	}
	
}
