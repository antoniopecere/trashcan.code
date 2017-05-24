package eu.pecere.utils.pdf.conversion;

import static eu.pecere.utils.commons.FileExtension.*;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.pecere.utils.commons.ExtensionUtils;
import eu.pecere.utils.commons.FileExtension;
import eu.pecere.utils.pdf.libreoffice.LibreOfficeFilterNames.In;

/**
 * Class that contains the method factory to instantiate the needed converter, hiding the implementation of the relative
 * strategy.
 * 
 * @author Antonio Pecere
 *
 */
class ConverterFactory
{
	private static final Log log = LogFactory.getLog( ConverterFactory.class );
	
	/**
	 * Convenience overload of the method {@link ConverterFactory#getPdfConverter(String, File)}.<br>
	 * It's equivalent to call <code>ConverterFactory.getPdfConverter( inputFileExtension, null );</code><br>
	 * In that case the <strong>inputFile</strong> is <strong>null</strong>, and the returned converter is initialized
	 * to be used to convert directly from an input stream to an output stream, and the conversion methods that expect
	 * the existence of the input file, will fail launching a NullPointerException.
	 * 
	 * @param inputFileExtension
	 *            The extension used to chose the converter to be used for the conversion.
	 * 
	 * @return The converter related to the chosen extension.
	 * 
	 * @see ConverterFactory#getPdfConverter(String, File)
	 */
	public static ToPdfConverter getPdfConverter( String inputFileExtension )
	{
		return ConverterFactory.getPdfConverter( inputFileExtension, null );
	}
	
	/**
	 * Convenience overload of the method {@link ConverterFactory#getPdfConverter(String, File)}.<br>
	 * The extension of the input file is retrieved by the file itself using org.apache.commons.io.FilenameUtils.
	 * 
	 * @param inputFile
	 *            The file to be converted.
	 * 
	 * @return The converter related to the extension retrieved from the input file.
	 * 
	 * @see ConverterFactory#getPdfConverter(String, File)
	 * @see FilenameUtils#getExtension(String)
	 */
	public static ToPdfConverter getPdfConverter( File inputFile )
	{
		String inputFileExtension = ExtensionUtils.extractExtension( inputFile ).getMainExtension();
		return ConverterFactory.getPdfConverter( inputFileExtension, inputFile );
	}
	
	/**
	 * Factory method to create an instance of a specific converter related to the file type to be converted. The right
	 * converter is chosen by the extension. The <strong>inputFile</strong> can be <strong>null</strong>, but in that
	 * case the returned converter is initialized to be used to convert directly from an input stream to an output
	 * stream, and the conversion methods that expect the existence of the input file, will fail launching a
	 * NullPointerException.
	 * 
	 * @param inputFileExtension
	 *            The extension used to chose the converter to be used for the conversion.
	 * @param inputFile
	 *            The possible file to be converted.
	 * 
	 * @return The converter related to the chosen extension.
	 */
	public static ToPdfConverter getPdfConverter( String inputFileExtension, File inputFile )
	{
		log.info( "Input file extension: " + inputFileExtension );
		FileExtension fileExtension = FileExtension.getFileExtension( inputFileExtension );
		
		log.info( "The input file is" + ( inputFile == null ? " " : " not " ) + "null!" );
		
		ToPdfConverter converter;
		
		switch( fileExtension ) {
			case TIF:
				converter = new TiffToPdfConverter( inputFile );
				break;
			case PNG:
				converter = new PngToPdfConverter( inputFile );
				break;
			case GIF:
				converter = new GifToPdfConverter( inputFile );
				break;
			case BMP:
				converter = new BmpToPdfConverter( inputFile );
				break;
			case JPG:
				converter = new JpgToPdfConverter( inputFile );
				break;
			case DOC:
				converter = new LibreOfficeToPdfConverter( inputFile, In.DOC );
				break;
			case DOCX:
				converter = new LibreOfficeToPdfConverter( inputFile, In.DOCX );
				break;
			case ODT:
				converter = new LibreOfficeToPdfConverter( inputFile, In.ODT );
				break;
			case PPT:
				converter = new LibreOfficeToPdfConverter( inputFile, In.PPT );
				break;
			case PPTX:
				converter = new LibreOfficeToPdfConverter( inputFile, In.PPTX );
				break;
			case XLS:
				converter = new LibreOfficeToPdfConverter( inputFile, In.XLS );
				break;
			case XLSB:
				converter = new LibreOfficeToPdfConverter( inputFile, In.XLSB );
				break;
			case XLSX:
				converter = new LibreOfficeToPdfConverter( inputFile, In.XLSX );
				break;
			case ODS:
				converter = new LibreOfficeToPdfConverter( inputFile, In.ODS );
				break;
			case XML:
			case CSV:
			case VCF:
			case INI:
			case TXT:
				converter = new TxtToPdfConverter( inputFile );
				break;
			
			default:
				converter = null;
				break;
		}
		
		log.info( "The converter to be returned is: [ " + converter + " ]." );
		return converter;
	}
	
	protected static final FileExtension[] CONVERTER_MANAGED_FILE_EXTENSIONS = new FileExtension[] {
			TIF, PNG, GIF, BMP, JPG, DOC, DOCX, ODT, PPT, PPTX, XLS, XLSB, XLSX, ODS, XML, CSV, VCF, INI, TXT
	};
	
	protected static final String[] CONVERTER_MANAGED_EXTENSIONS = ExtensionUtils
			.getExtensionsForFileFilter( CONVERTER_MANAGED_FILE_EXTENSIONS );
}
