package eu.pecere.utils.pdf.libreoffice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;

/**
 * Class containing some utility snippet methods used by the classes in the package.
 * 
 * @author Antonio Pecere
 *
 */
class LibreOfficeUtils
{
	/**
	 * Convenience method to create an input stream customized to be used by LibreOffice.<br>
	 * It copies the bytesoriginal input stream into the custom object.
	 * 
	 * @param inputStream
	 *            The input stream to read from, to be wrapped.
	 * 
	 * @return The input stream customized to be used by LibreOffice.
	 * 
	 * @throws IOException
	 *             If reading from the input stream fails.
	 */
	static LibreOfficeInputStream newLibreOfficeInputStream( InputStream inputStream ) throws IOException
	{
		InputStream inputFile = new BufferedInputStream( inputStream );
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] byteBuffer = new byte[4096];
		int byteBufferLength = 0;
		while( ( byteBufferLength = inputFile.read( byteBuffer ) ) > 0 ) {
			bytes.write( byteBuffer, 0, byteBufferLength );
		}
		inputFile.close();
		
		LibreOfficeInputStream loInputStream = new LibreOfficeInputStream( bytes.toByteArray() );
		return loInputStream;
	}
	
	/**
	 * Convenience method to load a document from an input stream customized for LibreOffice.
	 * 
	 * @param xComponentLoader
	 *            The loader related to the LibreOffice instance to be used for the load.
	 * @param loInputStream
	 *            The input stream customized for LibreOffice
	 * @param inputFilterName
	 *            The filterName related to the extension to be used for the load.
	 * 
	 * @return The document loaded from the input stream.
	 * 
	 * @throws com.sun.star.io.IOException
	 *             If the load of the document fails.
	 */
	static XComponent loadDocument(
			XComponentLoader xComponentLoader, LibreOfficeInputStream loInputStream,
			String inputFilterName ) throws com.sun.star.io.IOException
	{
		PropertyValue[] loadProperties = new PropertyValue[3];
		loadProperties[0] = LibreOfficeUtils.newPropertyValue( "InputStream", loInputStream );
		loadProperties[1] = LibreOfficeUtils.newPropertyValue( "FilterName", inputFilterName );
		loadProperties[2] = LibreOfficeUtils.newPropertyValue( "Hidden", new Boolean( true ) );
		
		XComponent document = xComponentLoader.loadComponentFromURL( "private:stream", "_blank", 0, loadProperties );
		return document;
	}
	
	/**
	 * Convenience method to export a document into the output stream customized for LibreOffice.
	 * 
	 * @param document
	 *            The document to be exported.
	 * @param loOutputStream
	 *            The output stream customized for LibreOffice
	 * @param outputFilterName
	 *            The filterName related to the extension to be used for the export.
	 * 
	 * @throws com.sun.star.io.IOException
	 *             If the export of the document fails.
	 */
	static void exportDocument( XComponent document, LibreOfficeOutputStream loOutputStream, String outputFilterName )
			throws com.sun.star.io.IOException
	{
		PropertyValue[] conversionProperties = new PropertyValue[2];
		conversionProperties[0] = LibreOfficeUtils.newPropertyValue( "OutputStream", loOutputStream );
		conversionProperties[1] = LibreOfficeUtils.newPropertyValue( "FilterName", outputFilterName );
		
		XStorable xStorable = UnoRuntime.queryInterface( XStorable.class, document );
		xStorable.storeToURL( "private:stream", conversionProperties );
	}
	
	/**
	 * Convenience method to safely close a document into LibreOffice's context.
	 * 
	 * @param xDocument
	 *            The document to be closed.
	 * 
	 * @throws CloseVetoException
	 *             If the close of the document fails.
	 */
	static void closeDocument( XComponent xDocument ) throws CloseVetoException
	{
		// Retrieve a service object representing the document previously opened by URL.
		// Check supported functionality of the document (model or controller).
		XModel xModel = UnoRuntime.queryInterface( XModel.class, xDocument );
		
		if( xModel != null ) {
			// Retrieve a service object allowed to close the previously opened document represented by the model
			// It is a full featured office document. Try to use close mechanism instead of a hard dispose().
			// But maybe such service is not available on this model.
			XCloseable xCloseable = UnoRuntime.queryInterface( XCloseable.class, xModel );
			
			// Try to last close the resource if possible,
			// otherwise retrieve a service object to break cyclic references and close the resource.
			if( xCloseable != null ) {
				try {
					// Use close(boolean DeliverOwnership)
					// The boolean parameter DeliverOwnership tells objects vetoing the close process that they may
					// assume ownership if they object the closure by throwing a CloseVetoException
					// Here we give up ownership. To be on the safe side, catch possible veto exception anyway.
					xCloseable.close( true );
				} catch( CloseVetoException e ) {
				}
			} else {
				// If close is not supported by this model - try to dispose it.
				// But if the model disagree with a reset request for the modify state
				// we shouldn't do so. Otherwise some strange things can happen.
				XComponent xDisposeable = UnoRuntime.queryInterface( XComponent.class, xModel );
				
				if( xDisposeable != null ) {
					xDisposeable.dispose();
				}
			}
		}
		
		xDocument = null;
	}
	
	/**
	 * Convenience method to compose a PropertyValue pair (Key, Value) used by LibreOffice to load or export a document.
	 * 
	 * @param name
	 *            The key to retrieve the value.
	 * @param value
	 *            The object to be retrieved by LibreOffice using the key.
	 * 
	 * @return The PropertyValue object to be used by LibreOffice.
	 */
	static PropertyValue newPropertyValue( String name, Object value )
	{
		PropertyValue property = new PropertyValue();
		property.Name = name;
		property.Value = value;
		return property;
	}
	
	/**
	 * Convenience method to compose the options to launch the instance of LibreOffice.<br>
	 * For a list of the options available, go here:<br>
	 * <a href="https://wiki.openoffice.org/wiki/Framework/Article/Command_Line_Arguments">https://wiki.openoffice.org/
	 * wiki/Framework/Article/Command_Line_Arguments</a><br>
	 * or here:<br>
	 * <a href="https://help.libreoffice.org/Common/Starting_the_Software_With_Parameters/it">https://help.libreoffice.
	 * org/Common/Starting_the_Software_With_Parameters/it</a>
	 * 
	 * @return an String array containing the chosen options.
	 */
	static String[] getLaunchOptions()
	{
		// Retrieve current default options:
		String[] options = Bootstrap.getDefaultOptions();
		// Option 1:--nologo
		// Option 2:--nodefault
		// Option 3:--norestore
		// Option 4:--nolockcheck
		
		// Adding options:
		List<String> optionList = new ArrayList<>( Arrays.asList( options ) );
		// Option 5:--headless
		optionList.add( "--headless" );
		// Option 6:--nofirststartwizard
		optionList.add( "--nofirststartwizard" );
		// Option 7:--accept="socket,port=8100;urp;"
		optionList.add( "--accept=\"socket,port=8100;urp;\"" );
		
		options = optionList.toArray( new String[0] );
		return options;
	}
	
}
