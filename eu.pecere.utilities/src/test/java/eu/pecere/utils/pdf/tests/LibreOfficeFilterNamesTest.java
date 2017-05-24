package eu.pecere.utils.pdf.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.pecere.utils.commons.FileExtension;
import eu.pecere.utils.pdf.libreoffice.LibreOfficeFilterNames;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class LibreOfficeFilterNamesTest extends PdfMasterTestCase
{
	@Test
	public void testFilterNames()
	{
		String extension = FileExtension.DOCX.getMainExtension();
		
		LibreOfficeFilterNames.In enIn = LibreOfficeFilterNames.getInputFilterName( extension );
		LibreOfficeFilterNames.Out enOut = enIn.getRelatedOutputFilter();
		log.info( extension + " -> [ " + enIn + ":" + enIn.getFilterName() + "; " + enOut + ":" + enOut.getFilterName() + " ]" );
		
		assertEquals( extension.toUpperCase(), enIn.name() );
		assertEquals( "MS Word 2007 XML", enIn.getFilterName() );
		assertEquals( "writer_pdf_Export", enOut.getFilterName() );
	}
	
}
