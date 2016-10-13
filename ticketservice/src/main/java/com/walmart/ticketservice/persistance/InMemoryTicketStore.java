package com.walmart.ticketservice.persistance;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.SeatHoldStatus;
import com.walmart.ticketservice.persistance.exception.SeatHoldExpiredException;

public final class InMemoryTicketStore implements TicketStore {

	/**
	 * Default seat expiration time for seats onHold in Seconds.
	 */
	public final static int DEFAULT_SEAT_HOLD_EXPIRY_TIME = 10;

	/**
	 * Default no of seats that exist in the venue.
	 */
	public final static int DEFAULT_NO_OF_SEATS = 1000;

	/**
	 * Atomic integer that stores the last Seat Hold id
	 */
	public static AtomicInteger LAST_SEAT_HOLD_ID = new AtomicInteger(1);

	/**
	 * Stores SeatHolds
	 */
	public final static Map<Integer, SeatHold> SEAT_HOLDS = new ConcurrentHashMap<>();

	/**
	 * Map that Stores Seats that are reserved or on hold with their
	 * corresponding SeatHoldId.
	 */
	public final static Map<Integer, Integer> UNAVAILABLE_SEATS = new ConcurrentHashMap<>();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * expiration time for seats onHold in Seconds
	 */
	private int seatHoldExpiryTime = DEFAULT_SEAT_HOLD_EXPIRY_TIME;

	/**
	 * no of seats that exist in the venue.
	 */
	private int noOfSeats = DEFAULT_NO_OF_SEATS;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walmart.ticketingservice.persistance.TicketingStore#
	 * getAvailabileSeats()
	 */
	@Override
	public int[] getAvailabileSeats(Integer maxSize) {

		if (logger.isDebugEnabled()) {
			logger.debug("Getting available Seats...");
			logger.debug("MaxSize = " + maxSize);
		}

		IntStream result = IntStream.range(1, noOfSeats + 1).filter((int seatId) -> {

			if (UNAVAILABLE_SEATS.containsKey(seatId)) {

				// Seat Id has been placed on hold or reserved.
				Integer seatHoldId = UNAVAILABLE_SEATS.get(seatId);

				SeatHold hold = seatHoldId != null ? SEAT_HOLDS.get(seatHoldId) : null;

				if (hold == null || (hold.getSeatHoldStatus() == SeatHoldStatus.ONHOLD && hasExpired(hold))) {

					removeExpiredSeatHolds(seatId, hold);
					return true;
				}

				// seat is reserved or under an active hold.
				return false;
			}
			return true;

		}).sorted();

		if (logger.isDebugEnabled()) {

			logger.debug("Found unfiltered Seats : " + Arrays.toString(result.toArray()));
		}

		if (maxSize != null) {
			result = result.limit(maxSize);
		}

		if (logger.isDebugEnabled()) {

			logger.debug("Found Seats : " + Arrays.toString(result.toArray()));
		}
		return result.toArray();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.walmart.ticketingservice.persistance.TicketingStore#getBestSeats(int)
	 */
	@Override
	public int[] getBestSeats(int noOfSeats) {

		int[] availableSeats = getAvailabileSeats(noOfSeats);

		// Since array is in sorted order it is returned as-is.
		return availableSeats != null ? availableSeats : null;
	}

	@Override
	public SeatHold getSeatHoldById(int seatHoldId) throws SeatHoldExpiredException {

		logger.debug(">> Getting seatHold by Id");
		SeatHold seatHold = SEAT_HOLDS.get(seatHoldId);

		if (hasExpired(seatHold)) {

			logger.warn("seathold has expired ane will be removed. [Details: {}]", seatHold);
			if (logger.isDebugEnabled()) {
				logger.debug("removing seathold : " + seatHold.getSeatHoldId());
			}

			removeExpiredSeatHolds(null, seatHold);

			throw new SeatHoldExpiredException(this.getSeatHoldExpiryTime(), seatHold);
		}

		return seatHold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.walmart.ticketingservice.persistance.TicketingStore#createSeatHold(
	 * int, java.lang.String)
	 */
	@Override
	public SeatHold createSeatHold(int[] seatIds, String customerEmail) {

		// Create SeatHold object
		int seatHoldId = LAST_SEAT_HOLD_ID.getAndIncrement();

		SeatHold seatHold = new SeatHold();
		seatHold.setSeatHoldId(seatHoldId);
		seatHold.setSeatIds(seatIds);
		seatHold.setCustomerEmail(customerEmail);
		seatHold.setCreatedDate(new Date());

		// By default seatHold status should be put to on hold.
		seatHold.setSeatHoldStatus(SeatHoldStatus.ONHOLD);

		synchronized (this.getClass()) {
			// store SeatHold
			SEAT_HOLDS.put(seatHoldId, seatHold);

			// add seats to unavaialble bucket
			for (int seatId : seatIds) {
				UNAVAILABLE_SEATS.put(seatId, seatHoldId);
			}
		}

		return seatHold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walmart.ticketingservice.persistance.TicketingStore#
	 * changeSeatHoldStatus(com.walmart.ticketingservice.model.SeatHold,
	 * com.walmart.ticketingservice.model.SeatHoldStatus)
	 */
	@Override
	public void updateSeatHold(SeatHold seathold, SeatHoldStatus newSeatHoldStatus, String confirmationCode) {

		if (seathold != null) {
			seathold.setSeatHoldStatus(newSeatHoldStatus);
			seathold.setReservationConfirmationCode(confirmationCode);
			seathold.setLastModifiedDate(new Date());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walmart.ticketingservice.persistance.TicketingStore#
	 * generateConfirmationCode()
	 */
	@Override
	public String generateConfirmationCode() {

		// creates a unique confirmationCode
		return UUID.randomUUID().toString();
	}

	/**
	 * Helper Method to determine if SeatHold has expired or not.
	 * 
	 * @param hold
	 * @return
	 */
	public boolean hasExpired(SeatHold hold) {
		
		return hold != null && (hold.getCreatedDate().getTime() + (seatHoldExpiryTime * 1000)) < new Date().getTime();
	}

	/**
	 * Removes Expired SeatHolds.
	 * 
	 * @param seatId
	 * @param hold
	 */
	public void removeExpiredSeatHolds(Integer seatId, SeatHold hold) {

		logger.info("Removing Expired SeatHolds");
		synchronized (this.getClass()) {
			if (hold != null) {

				// Expunge seat Hold
				SEAT_HOLDS.remove(hold.getSeatHoldId());

				if (logger.isDebugEnabled()) {
					logger.debug("Removed Expired SeatHold  : " + hold.getSeatHoldId());
				}

				for (int seat : hold.getSeatIds()) {
					UNAVAILABLE_SEATS.remove(seat);
					if (logger.isDebugEnabled()) {
						logger.debug("Removed Expired seat placed on hold : " + seat);
					}
				}
			}

			else {
				if (logger.isDebugEnabled()) {
					logger.debug("removing seatId from unavailable seats. : " + seatId);
				}
				if (seatId != null && UNAVAILABLE_SEATS.get(seatId) != null) {
					// expunge from UNAVAIALBLE SEATS only
					UNAVAILABLE_SEATS.remove(seatId);

					if (logger.isDebugEnabled()) {
						logger.debug("removed from unavaialable Seats ; seatId:" + seatId);
					}
				}
			}
		}
	}

	/**
	 * @return the seatHoldExpiryTime
	 */
	public int getSeatHoldExpiryTime() {
		return seatHoldExpiryTime;
	}

	/**
	 * @param seatHoldExpiryTime
	 *            the seatHoldExpiryTime to set
	 */
	public void setSeatHoldExpiryTime(int seatHoldExpiryTime) {
		this.seatHoldExpiryTime = seatHoldExpiryTime;
	}

	/**
	 * @return the noOfSeats
	 */
	public int getNoOfSeats() {
		return noOfSeats;
	}

	/**
	 * @param noOfSeats
	 *            the noOfSeats to set
	 */
	public void setNoOfSeats(int noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

}
