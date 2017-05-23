/**
 * 
 */
package eu.pecere.utils.commons;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.util.Store;

/**
 * @author Martin Lilliu
 * @date 21 giu 2016
 * 
 *       This utility class is used to extract content from PKCS7 file and save the original file.
 *
 */
public class P7Mextractor
{
	private static final Log log = LogFactory.getLog( P7Mextractor.class );
	
	/**
	 * This method will receive in input the Input File from which to extract content and the output file where to write
	 * the extracted content.
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 * 
	 */
	@SuppressWarnings( "unchecked" )
	public static void p7mExtractor( File input, File output ) throws IOException
	{
		log.info( "Method p7mExtractor: START!" );
		byte[] buffer = new byte[(int) input.length()];
		
		// Retrieve the inputStream from the file passed with method signature;
		try {
			DataInputStream dataIs = new DataInputStream( new FileInputStream( input ) );
			dataIs.readFully( buffer );
			dataIs.close();
			
			CMSSignedData signature = new CMSSignedData( buffer );
			Store<?> certificateStore = signature.getAttributeCertificates();
			SignerInformationStore signerInfosStore = signature.getSignerInfos();
			
			Collection<SignerInformation> signersCollection = signerInfosStore.getSigners();
			Iterator<SignerInformation> iterator = signersCollection.iterator();
			
			// The following code will extract the content of the input file
			// and will write the original file in the output file.
			byte[] data = null;
			OutputStream fileOutPutStream = new FileOutputStream( output );
			
			while( iterator.hasNext() ) {
				SignerInformation signerInfos = iterator.next();
				Collection<?> certificateCollection = certificateStore.getMatches( signerInfos.getSID() );
				Iterator<?> certificateIterator = certificateCollection.iterator();
				
				if( certificateIterator.hasNext() ) {
					// X509Certificate certificate = (X509Certificate) certificateIterator.next();
					certificateIterator.next();
				}
				
				CMSProcessable signedContent = signature.getSignedContent();
				data = (byte[]) signedContent.getContent();
				fileOutPutStream.write( data );
			}
			
			fileOutPutStream.close();
			log.info( "Method p7mExtractor: STOP!" );
		} catch( CMSException e ) {
			log.error( e.getMessage(), e );
			throw new IOException( e );
		}
	}
}
