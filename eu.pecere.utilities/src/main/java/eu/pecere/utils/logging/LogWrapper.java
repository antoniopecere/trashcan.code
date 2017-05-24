package eu.pecere.utils.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogWrapper implements Log
{
	protected static final String row = "##############################################################";
	
	private Log log;
	
	public static LogWrapper build( Class clazz )
	{
		return new LogWrapper( LogFactory.getLog( clazz ) );
	}
	
	private void methodLog()
	{
		log.info( row );
		String className = Thread.currentThread().getStackTrace()[3].getClassName();
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		String methodInvocation = className + "." + methodName + "();";
		log.info( "##### Invocato metodo: " + methodInvocation );
		log.info( row );
	}
	
	private LogWrapper( Log log )
	{
		this.log = log;
	}
	
	@Override
	public void debug( Object arg0 )
	{
		methodLog();
		log.debug( arg0 );
	}
	
	@Override
	public void debug( Object arg0, Throwable arg1 )
	{
		methodLog();
		log.debug( arg0, arg1 );
	}
	
	@Override
	public void error( Object arg0 )
	{
		methodLog();
		log.error( arg0 );
	}
	
	@Override
	public void error( Object arg0, Throwable arg1 )
	{
		methodLog();
		log.error( arg0, arg1 );
	}
	
	@Override
	public void fatal( Object arg0 )
	{
		methodLog();
		log.fatal( arg0 );
	}
	
	@Override
	public void fatal( Object arg0, Throwable arg1 )
	{
		methodLog();
		log.fatal( arg0, arg1 );
	}
	
	@Override
	public void info( Object arg0 )
	{
		methodLog();
		log.info( arg0 );
	}
	
	@Override
	public void info( Object arg0, Throwable arg1 )
	{
		methodLog();
		log.info( arg0, arg1 );
	}
	
	@Override
	public boolean isDebugEnabled()
	{
		methodLog();
		return log.isDebugEnabled();
	}
	
	@Override
	public boolean isErrorEnabled()
	{
		methodLog();
		return log.isErrorEnabled();
	}
	
	@Override
	public boolean isFatalEnabled()
	{
		methodLog();
		return log.isFatalEnabled();
	}
	
	@Override
	public boolean isInfoEnabled()
	{
		methodLog();
		return log.isInfoEnabled();
	}
	
	@Override
	public boolean isTraceEnabled()
	{
		methodLog();
		return log.isTraceEnabled();
	}
	
	@Override
	public boolean isWarnEnabled()
	{
		methodLog();
		return log.isWarnEnabled();
	}
	
	@Override
	public void trace( Object arg0 )
	{
		methodLog();
		log.trace( arg0 );
	}
	
	@Override
	public void trace( Object arg0, Throwable arg1 )
	{
		methodLog();
		log.trace( arg0, arg1 );
	}
	
	@Override
	public void warn( Object arg0 )
	{
		methodLog();
		log.warn( arg0 );
	}
	
	@Override
	public void warn( Object arg0, Throwable arg1 )
	{
		methodLog();
		log.warn( arg0, arg1 );
	}
	
}
