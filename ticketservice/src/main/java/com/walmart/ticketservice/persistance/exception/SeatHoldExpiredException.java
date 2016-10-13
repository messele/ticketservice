package com.walmart.ticketservice.persistance.exception;

import com.walmart.ticketservice.model.SeatHold;

public class SeatHoldExpiredException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private final SeatHold seatHold;
	private final int expiryInSeconds;

	public SeatHoldExpiredException(int expiryInSeconds, SeatHold seatHold) {
		super("SeatHold has Expired");
		this.seatHold = seatHold;
		this.expiryInSeconds = expiryInSeconds;
	}

	/**
	 * @return the seatHold
	 */
	public SeatHold getSeatHold() {
		return seatHold;
	}

	/**
	 * @return the expiryInSeconds
	 */
	public int getExpiryInSeconds() {
		return expiryInSeconds;
	}

}
