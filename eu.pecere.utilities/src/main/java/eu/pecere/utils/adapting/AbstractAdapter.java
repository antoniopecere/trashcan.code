package eu.pecere.utils.adapting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractAdapter<SOURCE, TARGET> implements Adapter<SOURCE, TARGET>
{
	private final Log log = LogFactory.getLog( this.getClass() );
	
	@Override
	public List<TARGET> convert( List<SOURCE> sourceList )
	{
		if( sourceList == null )
			return null;
		
		List<TARGET> targetList = new ArrayList<>();
		if( sourceList.size() > 0 )
			for( SOURCE source : sourceList ) {
				TARGET target = this.convert( source );
				targetList.add( target );
			}
		
		return targetList;
	}
	
	@Override
	public List<SOURCE> unconvert( List<TARGET> targetList )
	{
		if( targetList == null )
			return null;
		
		List<SOURCE> sourceList = new ArrayList<>();
		if( targetList.size() > 0 )
			for( TARGET target : targetList ) {
				SOURCE source = this.unconvert( target );
				sourceList.add( source );
			}
		
		return sourceList;
	}
	
	@Override
	public TARGET convert( SOURCE source )
	{
		if( source == null )
			return null;
		
		TARGET target = null;
		try {
			target = this.convertImpl( source );
		} catch( Throwable e ) {
			String errorMessage = "ERROR DURING CONVERSION FROM CLASS: " + source.getClass().getName();
			log.error( errorMessage, e );
			throw new AdapterException( errorMessage, e );
		}
		
		return target;
	}
	
	@Override
	public SOURCE unconvert( TARGET target )
	{
		if( target == null )
			return null;
		
		SOURCE source = null;
		try {
			source = this.unconvertImpl( target );
		} catch( Throwable e ) {
			String errorMessage = "ERROR DURING CONVERSION FROM CLASS: " + target.getClass().getName();
			log.error( errorMessage, e );
			throw new AdapterException( errorMessage, e );
		}
		
		return source;
	}
	
	protected abstract TARGET convertImpl( SOURCE source ) throws Throwable;
	
	protected abstract SOURCE unconvertImpl( TARGET target ) throws Throwable;
}
