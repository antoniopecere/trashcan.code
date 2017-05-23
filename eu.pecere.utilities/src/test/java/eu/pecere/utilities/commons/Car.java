package eu.pecere.utilities.commons;

import java.io.Serializable;

public class Car implements Serializable
{
	private static final long serialVersionUID = -2845336770354436589L;
	
	private String brand;
	private String model;
	private Integer year;
	
	public Car()
	{
		super();
	}
	
	public Car( String brand, String model, Integer year )
	{
		super();
		this.brand = brand;
		this.model = model;
		this.year = year;
	}
	
	/**
	 * @return the brand
	 */
	public String getBrand()
	{
		return brand;
	}
	
	/**
	 * @param brand
	 *            the brand to set
	 */
	public void setBrand( String brand )
	{
		this.brand = brand;
	}
	
	/**
	 * @return the model
	 */
	public String getModel()
	{
		return model;
	}
	
	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel( String model )
	{
		this.model = model;
	}
	
	/**
	 * @return the year
	 */
	public Integer getYear()
	{
		return year;
	}
	
	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear( Integer year )
	{
		this.year = year;
	}
	
}
