package eu.pecere.utils.pdf.libreoffice;

/**
 * Class containing the enumerations of the Filter Names provided by LibreOffice to be used for load and export
 * documents.<br>
 * A list of the Filter Names is available here:
 * <a href="https://wiki.openoffice.org/wiki/Framework/Article/Filter/FilterList_OOo_3_0">FilterList_OOo_3_0</a>
 * 
 * @author Antonio Pecere
 *
 */
public class LibreOfficeFilterNames
{
	/**
	 * Enumeration to manage the Filter Names provided by LibreOffice to load documents.
	 */
	public enum In
	{
		//@formatter:off
		// Import filters:
		DOC( "MS Word 97" ),
		DOCX( "MS Word 2007 XML" ),
		TXT( "Text" ),
		RTF( "Rich Text Format" ),
		HTML( "HTML" ),
		HTM( "HTML" ),
		XML( "StarOffice XML (Writer)" ),
		ODT( "writer8" ),
		WPS( "writer_WPSSystem_WPS2000_10" ),
		WPD( "WordPerfect" ),
		XLS( "MS Excel 97" ),
		XLSB( "Calc MS Excel 2007 Binary" ),
		XLSX( "Calc MS Excel 2007 XML" ),
		ODS( "calc8" ),
		PPT( "MS PowerPoint 97" ),
		PPTX( "MS PowerPoint 2007 XML" ),
		ODP( "impress8" );
		//@formatter:on
		
		private String filterName;
		
		private In( String filterName )
		{
			this.filterName = filterName;
		}
		
		public String getFilterName()
		{
			return this.filterName;
		}
		
		/**
		 * LibreOffice use the Filter Name mapped by this enumeration, to load a document with specific extension and
		 * format. This method returns the allowed Filter Name that must be used to export a specific file previously
		 * loaded using this input filter name.
		 * 
		 * @return The output Filter Name to be used to export the document loaded with this input Filter Name.
		 */
		public Out getRelatedOutputFilter()
		{
			switch( this ) {
				
				case DOC:
				case DOCX:
				case TXT:
				case RTF:
				case ODT:
				case WPS:
				case WPD:
					return LibreOfficeFilterNames.Out.W_PDF;
				
				case HTML:
				case HTM:
				case XML:
					return LibreOfficeFilterNames.Out.WW_PDF;
				
				case XLS:
				case XLSB:
				case XLSX:
				case ODS:
					return LibreOfficeFilterNames.Out.C_PDF;
				
				case PPT:
				case PPTX:
				case ODP:
					return LibreOfficeFilterNames.Out.I_PDF;
				
				default:
					return null;
			}
		}
		
	}
	
	/**
	 * Enumeration to manage the Filter Names provided by LibreOffice to export documents into pdf format.
	 */
	public enum Out
	{
		//@formatter:off
		// Export filters for PDF:
		W_PDF( "writer_pdf_Export" ),
		WW_PDF( "writer_web_pdf_Export" ),
		C_PDF( "calc_pdf_Export" ),
		I_PDF( "impress_pdf_Export" ),
		D_PDF( "draw_pdf_Export" );
		//@formatter:on
		
		private String filterName;
		
		private Out( String filterName )
		{
			this.filterName = filterName;
		}
		
		public String getFilterName()
		{
			return this.filterName;
		}
		
	}
	
	/**
	 * Utility method to retrieve the correct Filter Name to be used to load a document, starting from the document
	 * extension.
	 * 
	 * @param extension
	 *            The document extension.
	 * 
	 * @return The enumeration instance representing the filter name related to the specified extension.
	 */
	public static In getInputFilterName( String extension )
	{
		return In.valueOf( extension.toUpperCase() );
	}
	
}
