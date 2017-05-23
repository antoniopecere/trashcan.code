package eu.pecere.utils.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FileExtension
{
	// --------------- //
	// @formatter:off  //
	
	// Email message:
	MSG( "msg"),
	EML( "eml" ),
	
	// Images:
	JPG( "jpg", "jpeg" ),
	PNG( "png" ),
	BMP( "bmp" ),
	GIF( "gif" ),
	TIF( "tif", "tiff" ),
	
	// Office documents
	PDF( "pdf" ),
	DOC( "doc" ),
	DOCX( "docx" ),
	RTF( "rtf" ),
	ODT( "odt" ),
	WPS( "wps" ),
	WPD( "wpd" ),
	XLS( "xls" ),
	XLSB( "xlsb" ),
	XLSX( "xlsx" ),
	ODS( "ods" ),
	PPT( "ppt" ),
	PPTX( "pptx" ),
	ODP( "odp" ),
	TXT( "txt" ),
	CSV( "csv" ),
	VCF( "vcf" ),
	
	// Signed files:
	P7M( "p7m" ),
	P7S( "p7s" ),
	
	// Archives:
	ZIP( "zip" ),
	TAR( "tar" ),
	RAR( "rar" ),
	SZIP( "7z" ),
	ADZ( "adz" ),
	
	// Mark Up and web
	XML( "xml" ),
	XSD( "xsd" ),
	XSLT( "xslt" ),
	HTML( "html", "htm" ),
	CSS( "css" ),
	JS( "js" ),
	
	// System and executable:
	EXE( "exe" ),
	BIN( "bin" ),
	BAT( "bat" ),
	SH( "sh" ),
	TMP( "tmp" ),
	INI( "ini" ),
	
	// Various:
	PROPERTIES( "properties" ),
	FAKE( "_xxx_" );
	
	// @formatter:on   //
	// --------------- //
	
	private static final String DOT = ".";
	
	public static final FileExtension[] EMAIL_FILE_EXTENSIONS = { MSG, EML };
	public static final FileExtension[] IMAGE_FILE_EXTENSIONS = { JPG, PNG, BMP, GIF, TIF };
	public static final FileExtension[] ARCHIVE_FILE_EXTENSIONS = { ZIP, TAR, RAR, SZIP, ADZ };
	public static final FileExtension[] PDF_FILE_EXTENSIONS = { PDF };
	public static final FileExtension[] FAKE_FILE_EXTENSIONS = { FAKE };
	public static final FileExtension[] SIGNED_FILE_EXTENSIONS = { P7M, P7S };
	
	private String[] extensions;
	
	private FileExtension( String... extensions )
	{
		this.extensions = extensions;
	}
	
	public String[] getExtensions()
	{
		return this.extensions;
	}
	
	public String getMainExtension()
	{
		return this.extensions[0];
	}
	
	public String getMainDotExtension()
	{
		return FileExtension.DOT + this.getMainExtension();
	}
	
	public String[] getExtensionsForFileFilter()
	{
		List<String> filters = new ArrayList<>();
		for( String extension : this.extensions ) {
			filters.add( extension.toLowerCase() );
			filters.add( extension.toUpperCase() );
		}
		
		return filters.toArray( new String[0] );
	}
	
	@Override
	public String toString()
	{
		String[] filters = this.getExtensionsForFileFilter();
		List<String> filterList = Arrays.asList( filters );
		return this.name() + filterList.toString();
	}
	
	public static FileExtension getFileExtension( String sExtension )
	{
		FileExtension matchingFileExtension = null;
		
		outer_loop: for( FileExtension fileExtension : FileExtension.values() ) {
			String[] extensions = fileExtension.getExtensions();
			
			inner_loop: for( String extension : extensions ) {
				if( extension.equalsIgnoreCase( sExtension ) ) {
					matchingFileExtension = fileExtension;
					break outer_loop;
				}
			}
		}
		
		return matchingFileExtension;
	}
}
