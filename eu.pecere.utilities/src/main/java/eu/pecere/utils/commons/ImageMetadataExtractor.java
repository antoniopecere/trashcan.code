package eu.pecere.utils.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.file.FileMetadataDirectory;

/**
 * @author Antonio Pecere
 */
public class ImageMetadataExtractor
{
	private static Log log = LogFactory.getLog( ImageMetadataExtractor.class );
	
	public enum MetadataType
	{
		LATITUDE, LONGITUDE, ALTITUDE, CREATION_DATETIME, LAST_MODIFICATION_DATETIME
	}
	
	private File file;
	private Metadata originalMetadata;
	private final Map<MetadataType, Object> metadata;
	
	public ImageMetadataExtractor( String filePathname ) throws IOException
	{
		this( new File( filePathname ) );
	}
	
	public ImageMetadataExtractor( File file ) throws IOException
	{
		if( file == null )
			throw new FileNotFoundException( "The file is [null]!" );
		
		if( !file.exists() )
			throw new FileNotFoundException( "File [" + file.getName() + "], does not exists!" );
		
		this.file = file;
		this.metadata = new HashMap<MetadataType, Object>();
		
		try {
			
			this.originalMetadata = ImageMetadataReader.readMetadata( this.file );
			if( this.originalMetadata == null )
				return;
			
			extractCoordinates();
			extractDatetimes();
			
		} catch( ImageProcessingException e ) {
			String errorMessage = "Error while retrieving Metadata from file " + file.getName() + ", Error message: " + e.getMessage();
			log.error( errorMessage, e );
			throw new IOException( errorMessage, e );
		}
	}
	
	public Map<MetadataType, Object> getMetadata()
	{
		return this.metadata;
	}
	
	public Double getLatitude()
	{
		return (Double) this.metadata.get( MetadataType.LATITUDE );
	}
	
	public Double getLongitude()
	{
		return (Double) this.metadata.get( MetadataType.LONGITUDE );
	}
	
	public Double getAltitude()
	{
		return (Double) this.metadata.get( MetadataType.ALTITUDE );
	}
	
	public Date getCreationDatetime()
	{
		return (Date) this.metadata.get( MetadataType.CREATION_DATETIME );
	}
	
	public Date getLastModificationDatetime()
	{
		return (Date) this.metadata.get( MetadataType.LAST_MODIFICATION_DATETIME );
	}
	
	private void extractCoordinates() throws IOException, ImageProcessingException
	{
		List<GpsDirectory> gpsDirs = (List<GpsDirectory>) originalMetadata.getDirectoriesOfType( GpsDirectory.class );
		
		if( gpsDirs == null || gpsDirs.size() == 0 )
			return;
		
		for( GpsDirectory gpsDirectory : gpsDirs ) {
			
			GeoLocation geoLocation = gpsDirectory.getGeoLocation();
			
			if( geoLocation != null && !geoLocation.isZero() ) {
				Double latitude = geoLocation.getLatitude();
				metadata.put( MetadataType.LATITUDE, latitude );
				
				Double longitude = geoLocation.getLongitude();
				metadata.put( MetadataType.LONGITUDE, longitude );
				
				Double altitude = null;
				try {
					altitude = gpsDirectory.getDouble( GpsDirectory.TAG_ALTITUDE );
				} catch( MetadataException e ) {
					// Do nothing! Altitude is already null.
				}
				
				metadata.put( MetadataType.ALTITUDE, altitude );
				
				break;
			}
		}
	}
	
	private void extractDatetimes() throws IOException, ImageProcessingException
	{
		ExifSubIFDDirectory exifDirectory = originalMetadata.getFirstDirectoryOfType( ExifSubIFDDirectory.class );
		if( exifDirectory != null ) {
			Date creationDatetime = exifDirectory.getDate( ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL );
			metadata.put( MetadataType.CREATION_DATETIME, creationDatetime );
		}
		
		FileMetadataDirectory filemetadataDirectory = originalMetadata.getFirstDirectoryOfType( FileMetadataDirectory.class );
		if( filemetadataDirectory != null ) {
			Date lastModificationDatetime = (Date) filemetadataDirectory.getObject( FileMetadataDirectory.TAG_FILE_MODIFIED_DATE );
			metadata.put( MetadataType.LAST_MODIFICATION_DATETIME, lastModificationDatetime );
		}
	}
	
}
