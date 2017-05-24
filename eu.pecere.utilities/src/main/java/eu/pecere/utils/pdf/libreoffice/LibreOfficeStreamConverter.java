package eu.pecere.utils.pdf.libreoffice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.CloseVetoException;

import eu.pecere.utils.commons.ProcessUtils;

/**
 * Class that represents the conversion service that invokes LibreOffice to convert documents through UNO library.
 * It is a singleton and uses the customized streams to load and write documents.
 * 
 * @author Antonio Pecere
 *
 */
public class LibreOfficeStreamConverter
{
	private static final Log log = LogFactory.getLog( LibreOfficeStreamConverter.class );
	
	private static final String LIBRE_OFFICE_PROCESS_NAME = "soffice";
	
	/**
	 * The singleton instance of the LibreOfficeStreamConverter.
	 */
	private static LibreOfficeStreamConverter service;
	
	private XDesktop xDesktop;
	
	private XComponentLoader xComponentLoader;
	
	/**
	 * Private constructor to create the instance of the service to convert documents.
	 * It launch an instance of LibreOffice making its context available to conversions.
	 * 
	 * @throws IOException
	 *             If the boot of the LibreOffice instance fails.
	 */
	private LibreOfficeStreamConverter() throws IOException
	{
		int countAttempts = 0;
		do {
			try {
				countAttempts++;
				this.init();
			} catch( Throwable e ) {
				
				log.warn( e.getMessage(), e );
				this.reset();
				
				if( countAttempts >= 5 ) {
					throw new IOException( e.getMessage(), e );
				}
			}
		} while( ( this.xDesktop == null || this.xComponentLoader == null ) && countAttempts < 5 );
	}
	
	private void init() throws com.sun.star.uno.Exception, BootstrapException
	{
		// Retrieve the options to be used to launch the LibreOffice instance
		String[] options = LibreOfficeUtils.getLaunchOptions();
		
		// Connect to LibreOffice server.
		XComponentContext xComponentContext = Bootstrap.bootstrap( options );
		
		// Retrieve the LibreOffice service manager.
		XMultiComponentFactory xMultiComponentFactory = xComponentContext.getServiceManager();
		log.info( "... the service is '" + ( xMultiComponentFactory != null ? "available" : "not available" ) + "'! " );
		
		// Strat-up an instance of Libre Office.
		Object desktopService = xMultiComponentFactory.createInstanceWithContext( "com.sun.star.frame.Desktop", xComponentContext );
		
		// Retrieve the service object implementing the interface XDesktop.
		this.xDesktop = UnoRuntime.queryInterface( XDesktop.class, desktopService );
		
		// Retrieve the service object implementing the interface XComponentLoader.
		this.xComponentLoader = UnoRuntime.queryInterface( XComponentLoader.class, xDesktop );
	}
	
	/**
	 * Utility method to get the singleton instance of the LibreOfficeStreamConverter.
	 * 
	 * @return The singleton instance of the LibreOfficeStreamConverter.
	 * 
	 * @throws IOException
	 *             If the boot of the LibreOffice instance fails.
	 */
	public static LibreOfficeStreamConverter getInstance() throws IOException
	{
		// Lazy instantiation using double locking mechanism.
		if( service == null ) {
			synchronized( LibreOfficeStreamConverter.class ) {
				if( service == null ) {
					// Kills all previous eventually running instances.
					ProcessUtils.killAllProcesses( LIBRE_OFFICE_PROCESS_NAME );
					service = new LibreOfficeStreamConverter();
				}
			}
		}
		
		return service;
	}
	
	/**
	 * Method to reset the service, closing the LibreOffice running instance. If the attempt to safely close the
	 * instance fails, it tries to directly kill all the soffice processes.
	 */
	public void reset()
	{
		this.xComponentLoader = null;
		
		boolean isSafelyClosed = false;
		try {
			if( this.xDesktop != null ) {
				isSafelyClosed = this.xDesktop.terminate();
			}
			
		} catch( DisposedException e ) {
			log.warn( e.getMessage(), e );
		}
		
		this.xDesktop = null;
		service = null;
		
		if( !isSafelyClosed ) {
			ProcessUtils.killAllProcesses( LIBRE_OFFICE_PROCESS_NAME );
		}
	}
	
	/**
	 * Convenience overload of the method
	 * {@link LibreOfficeStreamConverter#convert(LibreOfficeInputStream, LibreOfficeOutputStream, String, String)}.
	 * <br>
	 * It converts the generic InputStream and OutputStream into the related customized streams, and then calls the main
	 * method.
	 * 
	 * @param inputStream
	 *            The input stream to load from.
	 * @param outputStream
	 *            The output stream to write into.
	 * @param inputFilterName
	 *            The filter name to be used to load the document from the input stream.
	 * @param outputFilterName
	 *            The filter name to be used to write the converted document into the output stream.
	 * 
	 * @throws IOException
	 *             If open the document from the input stream, export the conversion to the output stream or close the
	 *             opened document, fails.
	 * 
	 * @see LibreOfficeStreamConverter#convert(LibreOfficeInputStream, LibreOfficeOutputStream, String, String)
	 */
	public void convert( InputStream inputStream, OutputStream outputStream,
			String inputFilterName, String outputFilterName ) throws IOException
	{
		// Create LibreOfficeInputStream
		LibreOfficeInputStream loInputStream = LibreOfficeUtils.newLibreOfficeInputStream( inputStream );
		
		// Create LibreOfficeOutputStream
		LibreOfficeOutputStream loOutputStream = new LibreOfficeOutputStream();
		
		// Convert document to PDF
		this.convert( loInputStream, loOutputStream, inputFilterName, outputFilterName );
		
		// Save LibreOfficeOutputStream
		outputStream.write( loOutputStream.toByteArray() );
	}
	
	/**
	 * Method that converts a file loaded by the customized input stream using the input filter name, and write the
	 * product of the conversion into the customized output stream using the output filter name.
	 * 
	 * @param loInputStream
	 *            The customized input stream to load from.
	 * @param loOutputStream
	 *            The customized output stream to write into.
	 * @param inputFilterName
	 *            The filter name to be used to load the document from the customized input stream.
	 * @param outputFilterName
	 *            The filter name to be used to write the converted document into the customized output stream.
	 * 
	 * @throws IOException
	 *             If open the document from the input stream, export the conversion to the output stream or close the
	 *             opened document, fails.
	 */
	public void convert( LibreOfficeInputStream loInputStream, LibreOfficeOutputStream loOutputStream,
			String inputFilterName, String outputFilterName ) throws IOException
	{
		XComponent xDocument = null;
		
		int countAttempts = 0;
		do {
			try {
				countAttempts++;
				xDocument = LibreOfficeUtils.loadDocument( this.xComponentLoader, loInputStream, inputFilterName );
			} catch( Exception e ) {
				service.reset();
				if( countAttempts >= 5 )
					throw new IOException( e.getMessage(), e );
			}
		} while( xDocument == null && countAttempts < 5 );
		
		try {
			LibreOfficeUtils.exportDocument( xDocument, loOutputStream, outputFilterName );
			LibreOfficeUtils.closeDocument( xDocument );
		} catch( CloseVetoException | com.sun.star.io.IOException e ) {
			log.error( e.getMessage(), e );
			throw new IOException( e.getMessage(), e );
		}
	}
	
}
