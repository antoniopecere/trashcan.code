package eu.pecere.utils.commons.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import eu.pecere.utils.commons.ProcessUtils;

/**
 * 
 * @author Antonio Pecere
 *
 */
public class ProcessUtilsTest extends CommonsMasterTestCase
{
	@Test
	public void testKillProcess() throws IOException, InterruptedException
	{
		long start = ( new Date() ).getTime();
		log.info( "Start testKillProcess(): " + start );
		
		String processName = null;
		if( ProcessUtils.IS_WIN ) {
			processName = "calc";
		} else if( ProcessUtils.IS_NIX ) {
			processName = "vi";
		} else {
			fail( "No test for unknown OS!" );
		}
		
		ProcessUtils.runCommand( processName );
		
		int processesNumber = ProcessUtils.runningProcesses( processName );
		
		assertEquals( 1, processesNumber );
		
		boolean success = ProcessUtils.killProcess( processName );
		log.info( "Response of kill operation: " + success );
		
		if( success ) {
			processesNumber = ProcessUtils.runningProcesses( processName );
			assertEquals( 0, processesNumber );
		}
		
		long stop = ( new Date() ).getTime();
		
		log.info( "Start testKillProcess(): " + start );
		log.info( "Stop testKillProcess(): " + stop );
		log.info( "Difference stop-start testKillProcess(): " + ( stop - start ) );
	}
	
}
