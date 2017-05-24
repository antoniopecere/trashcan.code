package eu.pecere.utils.adapting;

public class TestDefaultAdapter
{
	public static class Prova1
	{
		private String value;
		
		public Prova1()
		{
			
		}
		
		public String getValue()
		{
			return value;
		}
		
		public void setValue( String value )
		{
			this.value = value;
		}
		
		@Override
		public String toString()
		{
			return this.value;
		}
	}
	
	public static class Prova2
	{
		private String value;
		
		public Prova2()
		{
			
		}
		
		public String getValue()
		{
			return value;
		}
		
		public void setValue( String value )
		{
			this.value = value;
		}
		
		@Override
		public String toString()
		{
			return this.value;
		}
	}
	
	public static void main( String[] args )
	{
		DefaultAdapter<Prova1, Prova2> pd = new DefaultAdapter<Prova1, Prova2>() {
		};
		
		Prova1 p1 = new TestDefaultAdapter.Prova1();
		p1.setValue( "ciao pippo" );
		System.out.println( "P1: " + p1 );
		
		Prova2 p2 = pd.convert( p1 );
		System.out.println( "P2: " + p2 );
		
		System.out.println();
		
		p2.setValue( "ciao pluto" );
		System.out.println( "P2: " + p2 );
		
		Prova1 p1un = pd.unconvert( p2 );
		System.out.println( "P1Un: " + p1un );
	}
	
}
