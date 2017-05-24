package eu.pecere.utils.commons.tests;

import java.io.Serializable;

public class Person implements Serializable
{
	private static final long serialVersionUID = -8510242646215515270L;
	
	private String name;
	private String surname;
	private int age;
	private Car car;
	
	public Person()
	{
		super();
	}
	
	public Person( String name, String surname, int age, Car car )
	{
		super();
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.car = car;
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName( String name )
	{
		this.name = name;
	}
	
	/**
	 * @return the surname
	 */
	public String getSurname()
	{
		return surname;
	}
	
	/**
	 * @param surname
	 *            the surname to set
	 */
	public void setSurname( String surname )
	{
		this.surname = surname;
	}
	
	/**
	 * @return the age
	 */
	public int getAge()
	{
		return age;
	}
	
	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge( int age )
	{
		this.age = age;
	}
	
	/**
	 * @return the car
	 */
	public Car getCar()
	{
		return car;
	}
	
	/**
	 * @param car
	 *            the car to set
	 */
	public void setCar( Car car )
	{
		this.car = car;
	}
	
}
