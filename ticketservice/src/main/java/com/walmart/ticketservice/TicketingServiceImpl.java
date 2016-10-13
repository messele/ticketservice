
package com.walmart.ticketservice;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.SeatHoldStatus;
import com.walmart.ticketservice.persistance.TicketStore;
import com.walmart.ticketservice.persistance.exception.SeatHoldExpiredException;

/**
 * Concrete Implementation for {@link TicketService}
 *
 */
public final class TicketingServiceImpl implements TicketService {

	private static final Logger logger = LoggerFactory.getLogger(TicketingServiceImpl.class);

	@Autowired
	private TicketStore store;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walmart.ticketingservice.TicketService#numSeatsAvailable()
	 */
	@Override
	public int numSeatsAvailable() {

		logger.info(">> Call started : Getting Num of Seats Available");

		int[] availableSeats = store.getAvailabileSeats(null);

		if (logger.isDebugEnabled()) {
			logger.debug(">> Found avaiable Seats : " + Arrays.toString(availableSeats));
		}

		int count = availableSeats != null ? availableSeats.length : 0;

		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walmart.ticketingservice.TicketService#findAndHoldSeats(int,
	 * java.lang.String)
	 */
	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) throws IllegalArgumentException {

		logger.info(String.format(">> Call started : Find And Hold Seats, input=[numSeats=%s, customerEmail=%s]",
				numSeats, customerEmail));

		// Validate
		if (numSeats < 1) {
			String msg = "Invalid no of Seats requested.  value must be greater than zero";
			logger.debug("Validation Error: " + msg);
			throw new IllegalArgumentException(msg);
		}

		// Find seats
		int[] seats = store.getBestSeats(numSeats);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Seats Found : %s", Arrays.toString(seats)));
		}

		if (seats != null) {

			if (seats.length < numSeats) {
				// not enough seats
				String msg = String.format("Not Enough Seats Found : [numSeatsRequested={}, SeatsFound = {}]", numSeats,
						seats.length);

				logger.error("Validation Error: " + msg);

				throw new IllegalArgumentException(msg);
			}

			// hold seats
			SeatHold hold = store.createSeatHold(seats, customerEmail);

			return hold;

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walmart.ticketingservice.TicketService#reserveSeats(int,
	 * java.lang.String)
	 */
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) throws SeatHoldExpiredException {

		logger.info(String.format(">> Call started : ReserveSeats. input= [SeatHoldId=%s,  customerEmail=%s]",
				seatHoldId, customerEmail));

		// get SeatHold
		try {
			SeatHold hold = store.getSeatHoldById(seatHoldId);

			if (logger.isDebugEnabled()) {

				logger.debug("Found Hold : " + hold);
			}

			if (hold != null) {

				String confirmationCode = store.generateConfirmationCode();

				store.updateSeatHold(hold, SeatHoldStatus.RESERVED, confirmationCode);

				return confirmationCode;
			}

			logger.warn("No hold found for Id: " + seatHoldId);
			
		} catch (SeatHoldExpiredException e) {
			// seatHold has expired
			logger.error("SeatHold found but has Expired: SeatHoldId={}", seatHoldId);
			throw e;
		}
		return null;
	}

	/**
	 * @return the store
	 */
	public TicketStore getStore() {
		return store;
	}

	/**
	 * @param store
	 *            the store to set
	 */
	public void setStore(TicketStore store) {
		this.store = store;
	}

}
