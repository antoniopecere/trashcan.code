package eu.pecere.utils.pdf.conversion;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import eu.pecere.utils.commons.FileExtension;

/**
 * This class expose the public method to convert a PDF into a JPG image resized as Thumbail.
 * 
 * @author Antonio Pecere
 *
 */
class ThumbnailExtractor
{
	private static final int IMG_WIDTH = 600;
	private static final int IMG_HEIGHT = 600;
	
	/**
	 * Convenience method to convert PDF into JPG
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @throws IOException
	 */
	public static void convertPDFToImage( InputStream inputStream, OutputStream outputStream ) throws IOException
	{
		PDDocument document = PDDocument.load( inputStream );
		PDFRenderer pdfRenderer = new PDFRenderer( document );
		BufferedImage bufferedThumbnailImg = pdfRenderer.renderImageWithDPI( 0, 600, ImageType.GRAY );
		// Resize the buffered image before write it
		bufferedThumbnailImg = resizeImage( bufferedThumbnailImg );
		// write the resized image
		ImageIOUtil.writeImage( bufferedThumbnailImg, FileExtension.PNG.getMainExtension(), outputStream );
		document.close();
	}
	
	/**
	 * Convenience method to resize the passed buffered image
	 * 
	 * @param bufferedThumbnailImg
	 */
	private static BufferedImage resizeImage( BufferedImage originalBufferedImage )
	{
		BufferedImage resizedBufferedImage = new BufferedImage( IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_BYTE_GRAY );
		Graphics2D g = resizedBufferedImage.createGraphics();
		g.drawImage( originalBufferedImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null );
		g.dispose();
		
		g.setComposite( AlphaComposite.Src );
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		return resizedBufferedImage;
	}
}
