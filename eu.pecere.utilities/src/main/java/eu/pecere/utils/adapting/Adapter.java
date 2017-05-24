package eu.pecere.utils.adapting;

import java.util.List;

public interface Adapter<SOURCE, TARGET>
{
	TARGET convert( SOURCE source );
	
	List<TARGET> convert( List<SOURCE> sourceList );
	
	SOURCE unconvert( TARGET target );
	
	List<SOURCE> unconvert( List<TARGET> targetList );
}
