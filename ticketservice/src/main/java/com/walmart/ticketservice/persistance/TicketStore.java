package com.walmart.ticketservice.persistance;

import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.SeatHoldStatus;
import com.walmart.ticketservice.persistance.exception.SeatHoldExpiredException;

/**
 * Provides Methods for Curd Operations on Seats
 */
public interface TicketStore  {

	/**
	 * Returns the total no of seats in the venue
	 * 
	 * @return
	 */
	public int getNoOfSeats();

	/**
	 * Gets the seat hold expiry time in seconds.
	 * 
	 * @return
	 */
	public int getSeatHoldExpiryTime();

	/**
	 * * Gets the number of available seats up to the given size.
	 * 
	 * N.B Available seats are seats which have not been reserved Or Seats which
	 * are on hold but have passed their expiry.
	 * 
	 * 
	 * @param maxSize
	 *            if null it will return the list of all available seats.
	 * @return
	 */
	public int[] getAvailabileSeats(Integer maxSize);

	/**
	 * Gets the Best Seats from the list of available Seats.
	 * 
	 * @param noOfSeats
	 * @return
	 */
	public int[] getBestSeats(int noOfSeats);

	/**
	 * Gets the SeatHold information by Id.
	 * 
	 * @param seatHoldId
	 * @return
	 */
	public SeatHold getSeatHoldById(int seatHoldId)  throws SeatHoldExpiredException;

	/**
	 * Creates a seatHold with the given SeatId and customer Email.
	 * 
	 * @param seatId
	 * @param customerEmail
	 * @return
	 */
	public SeatHold createSeatHold(int[] seatIds, String customerEmail);

	/**
	 * Modifies the seatHold Status
	 * 
	 * @param seathold
	 */
	public void updateSeatHold(SeatHold seathold, SeatHoldStatus newSeatHoldStatus, String confirmationCode);

	/**
	 * Generates the Confirmation Code.
	 * 
	 * @return
	 */
	public String generateConfirmationCode();

}
