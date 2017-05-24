package eu.pecere.utils.adapting;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.BeanUtils;

import eu.pecere.utils.exceptions.NotImplementedException;

public class DefaultAdapter<SOURCE, TARGET> extends AbstractAdapter<SOURCE, TARGET>
{
	@SuppressWarnings( "unchecked" )
	private TARGET getTargetInstance()
	{
		ParameterizedType superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
		Type type = superClass.getActualTypeArguments()[1];
		
		Class<TARGET> instanceType;
		if( type instanceof ParameterizedType ) {
			instanceType = (Class<TARGET>) ( (ParameterizedType) type ).getRawType();
		} else {
			instanceType = (Class<TARGET>) type;
		}
		
		try {
			return instanceType.newInstance();
		} catch( Exception e ) {
			throw new AdapterException( e );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private SOURCE getSourceInstance()
	{
		ParameterizedType superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
		Type type = superClass.getActualTypeArguments()[0];
		
		Class<SOURCE> instanceType;
		if( type instanceof ParameterizedType ) {
			instanceType = (Class<SOURCE>) ( (ParameterizedType) type ).getRawType();
		} else {
			instanceType = (Class<SOURCE>) type;
		}
		
		try {
			return instanceType.newInstance();
		} catch( Exception e ) {
			throw new AdapterException( e );
		}
	}
	
	@Override
	protected TARGET convertImpl( SOURCE source ) throws Throwable
	{
		TARGET target = this.getTargetInstance();
		
		if( target == null )
			throw new NotImplementedException( "Method not implemented." );
		
		// Commons BeanUtils arguments are inverted respect Spring BeanUtils...
		BeanUtils.copyProperties( target, source );
		
		return target;
	}
	
	@Override
	protected SOURCE unconvertImpl( TARGET target ) throws Throwable
	{
		SOURCE source = this.getSourceInstance();
		
		if( source == null )
			throw new NotImplementedException( "Method not implemented." );
		
		// Commons BeanUtils arguments are inverted respect Spring BeanUtils...
		BeanUtils.copyProperties( source, target );
		
		return source;
	}
	
}
