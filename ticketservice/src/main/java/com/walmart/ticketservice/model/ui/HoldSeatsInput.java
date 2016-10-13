package com.walmart.ticketservice.model.ui;

import com.walmart.ticketservice.model.BaseModel;

public class HoldSeatsInput  extends BaseModel{
	
	private String email;
	private Integer noOfSeatsNeeded;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the noOfSeatsNeeded
	 */
	public Integer getNoOfSeatsNeeded() {
		return noOfSeatsNeeded;
	}
	/**
	 * @param noOfSeatsNeeded the noOfSeatsNeeded to set
	 */
	public void setNoOfSeatsNeeded(Integer noOfSeatsNeeded) {
		this.noOfSeatsNeeded = noOfSeatsNeeded;
	}
	
}
