package eu.pecere.utils.commons;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

/**
 * Utility class containing methods to manage running processes abstracting from Operating System.
 * 
 * @author Antonio Pecere: 18 mag 2016
 *
 */
public class ProcessUtils
{
	private static final Log log = LogFactory.getLog( ProcessUtils.class );
	
	public static final Boolean IS_WIN = SystemUtils.IS_OS_WINDOWS;
	public static final Boolean IS_NIX = SystemUtils.IS_OS_UNIX;
	
	/**
	 * The Operating System running the JVM.
	 */
	private static final String OS;
	
	/**
	 * The extension of the running process, depending by the OS
	 */
	private static final String EXT;
	
	/**
	 * The command to kill a process by name, depending by the OS.
	 */
	private static final String KILL_BY_NAME;
	
	/*
	 * Static block to initialize constants depending on Operating System.
	 */
	static {
		OS = System.getProperty( "os.name" );
		log.info( "Your OS is: [ " + OS + " ]." );
		
		if( IS_WIN ) {
			EXT = FileExtension.EXE.getMainDotExtension();
			KILL_BY_NAME = "taskkill /F /IM %1$s* /T";
		} else if( IS_NIX ) {
			EXT = FileExtension.BIN.getMainDotExtension();
			KILL_BY_NAME = "killall -9 %1$s";
		} else {
			log.error( "Your OS is not supported!" );
			EXT = null;
			KILL_BY_NAME = null;
		}
	}
	
	public static Process runCommand( String command ) throws IOException
	{
		Runtime runtime = Runtime.getRuntime();
		return runtime.exec( command );
	}
	
	/**
	 * Retrieve the number of the processes currently running with the specified name.
	 * 
	 * @param processName
	 *            The process' name to look for into the currently running processes.
	 * 
	 * @return The number of the processes currently running with the specified name.
	 */
	public static int runningProcesses( String processName )
	{
		int runningProcesses = 0;
		
		List<ProcessInfo> processesList = JProcesses.getProcessList();
		log.info( "Total running processes: " + processesList.size() );
		
		for( final ProcessInfo processInfo : processesList ) {
			String currentProcessName = processInfo.getName().toLowerCase();
			if( currentProcessName.startsWith( processName ) && currentProcessName.endsWith( EXT ) ) {
				log.info( "Found running process: " + currentProcessName );
				runningProcesses++;
			}
		}
		
		log.info( "Total running processes with name [" + processName + "]: " + runningProcesses );
		return runningProcesses;
	}
	
	/**
	 * Retrieve the process' id of the first found currently running process with the specified name.
	 * 
	 * @param processName
	 *            The process' name to look for into the currently running processes.
	 * 
	 * @return The process' id of the first process found running.
	 */
	private static Integer retrieveProcessIdByName( String processName )
	{
		Integer processId = null;
		
		List<ProcessInfo> processesList = JProcesses.getProcessList();
		for( final ProcessInfo processInfo : processesList ) {
			String currentProcessName = processInfo.getName();
			if( currentProcessName.startsWith( processName ) && currentProcessName.endsWith( EXT ) ) {
				processId = Integer.valueOf( processInfo.getPid() );
				log.info( "Found running process: [ " + processName + ", " + processId + "]" );
				break;
			}
		}
		
		return processId;
	}
	
	/**
	 * This method looks for a process with the specified name and tries to kill it only if it's unique.
	 * 
	 * @param processName
	 *            The process' name of the process to be killed.
	 * 
	 * @return <code>true</code> if the attempt to kill the process has success, <code>false</code> otherwise.
	 */
	public static boolean killProcess( String processName )
	{
		boolean success = false;
		
		int runningProcesses = ProcessUtils.runningProcesses( processName );
		
		if( runningProcesses < 1 ) {
			log.error( "There are no processes called [" + processName + "]." );
		} else if( runningProcesses == 1 ) {
			Integer pid = ProcessUtils.retrieveProcessIdByName( processName );
			if( pid != null ) {
				success = ProcessUtils.killProcess( pid );
			} else {
				log.info( "PID not found! Trying to kill process by name!" );
				success = ProcessUtils.killAllProcesses( processName );
			}
		} else {
			log.error( "Too much processes called [" + processName + "] to choose from!" );
		}
		
		return success;
	}
	
	/**
	 * This method looks for all the processes with the specified name and tries to kill alla of them.
	 * 
	 * @param processName
	 *            The process' name of the process to be killed.
	 * 
	 * @return <code>true</code> if the attempt to kill processes has success, <code>false</code> otherwise.
	 */
	public static boolean killAllProcesses( String processName )
	{
		boolean success = false;
		
		log.info( "Trying to kill all processes by name [" + processName + "]!" );
		if( KILL_BY_NAME != null ) {
			try {
				// String command = String.format( "Comando ... blabla %1$s%2$s blabla ...", processName, EXT );
				String command = String.format( KILL_BY_NAME, processName );
				Process process = ProcessUtils.runCommand( command );
				process.waitFor();
				success = true;
			} catch( Exception e ) {
				log.error( "Attempt to kill all processes by name [" + processName + "] has failed!" );
				log.error( e.getMessage(), e );
			}
		} else {
			log.error( "Operation not supported for your OS!" );
		}
		
		return success;
	}
	
	/**
	 * This method tries to kill the process with the specified pid.
	 * 
	 * @param pid
	 *            The process' id of the process to be killed.
	 * 
	 * @return <code>true</code> if the attempt to kill the process has success, <code>false</code> otherwise.
	 */
	public static boolean killProcess( Integer pid )
	{
		return JProcesses.killProcess( pid ).isSuccess();
	}
	
}
