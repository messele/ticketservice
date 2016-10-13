package com.walmart.ticketservice.model.ui;

import com.walmart.ticketservice.model.BaseModel;

public class ReserveSeatsInput extends BaseModel{
	
	private Integer seatHoldId;
	private String email;

	/**
	 * @return the seatHoldId
	 */
	public Integer getSeatHoldId() {
		return seatHoldId;
	}

	/**
	 * @param seatHoldId the seatHoldId to set
	 */
	public void setSeatHoldId(Integer seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	
	
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
	

}
