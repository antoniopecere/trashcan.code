package eu.pecere.utilities.commons;

import org.junit.Test;

import eu.pecere.utils.commons.JsonUtils;

public class JsonUtilsTest
{
	private static final JsonUtils<Person> JSU_Person = new JsonUtils<Person>() {
	};
	
	private static final JsonUtils<Car> JSU_Car = new JsonUtils<Car>() {
	};
	
	@Test
	public void testJsonUtils()
	{
		Car car = new Car();
		car.setBrand( "FIAT" );
		car.setModel( "Panda" );
		car.setYear( 1998 );
		
		Person person = new Person();
		person.setName( "Marco" );
		person.setSurname( "Rossi" );
		person.setAge( 44 );
		person.setCar( car );
		
		String jsonCar = JSU_Car.serializeToJson( car );
		System.out.println( jsonCar );
		
		String jsonPerson = JSU_Person.serializeToJson( person );
		System.out.println( jsonPerson );
	}
	
}
