/**
 * 
 */
package com.walmart.ticketingservice.persistance;

import java.util.Arrays;

import org.junit.Test;

import com.walmart.ticketingservice.BaseTest;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.SeatHoldStatus;
import com.walmart.ticketservice.persistance.InMemoryTicketStore;
import com.walmart.ticketservice.persistance.exception.SeatHoldExpiredException;

import junit.framework.Assert;

/**
 * Tests for {@link InMemoryTicketStore}
 *
 */
public final class InMemoryTicketStoreTests  extends BaseTest {

	InMemoryTicketStore ticketingStore = new InMemoryTicketStore();
	
	


	/**
	 * Happy Path Test method for
	 * {@link com.walmart.ticketservice.persistance.InMemoryTicketStore#getAvailabileSeats(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetAvailableSeatsHappyPath() {

		cleanUpStore();
		testGetAvailabileSeats(1000, 200);
		testGetAvailabileSeats(1000, 10);
	}

	/**
	 * Edge Cases for
	 * {@link com.walmart.ticketservice.persistance.InMemoryTicketStore#getAvailabileSeats(java.lang.Integer)}
	 */
	@Test
	public void testGetAvailableSeatsEdgeCase() {

		cleanUpStore();
		testGetAvailabileSeats(1000, null);
		testGetAvailabileSeats(1000, 0);
		testGetAvailabileSeats(1000, 1);
	}

	/**
	 * Tests Seat hold.
	 */
	@Test
	public void testCreateSeatHold() {

		String customerEmail = "foo@foo.com";
		int noOfSeatsNeeded = 10;
		int noOfSeats = 1000;

		int[] seatIds = getSeatIds(noOfSeats, noOfSeatsNeeded);

		Assert.assertNotNull("seatId's is null", seatIds);
		SeatHold seatHold = ticketingStore.createSeatHold(seatIds, customerEmail);

		// Test integrity of seatHold created
		Assert.assertNotNull("SeatHold creation Failed.", seatHold);
		Assert.assertNotNull("SeatHold seatHoldId is Null", seatHold.getSeatHoldId());
		Assert.assertTrue("SeatHold seatIds mismatch", Arrays.equals(seatIds, seatHold.getSeatIds()));
		Assert.assertEquals("SeatHold email mismatch", customerEmail, seatHold.getCustomerEmail());
		Assert.assertEquals("SeatHold seatId's mismatch", seatIds, seatHold.getSeatIds());
		Assert.assertEquals("SeatHold status Mismatch", SeatHoldStatus.ONHOLD, seatHold.getSeatHoldStatus());
		Assert.assertNotNull("SeatHold createdDate is null.", seatHold.getCreatedDate());
		Assert.assertNull("SeatHold lastModifiedDate should be null", seatHold.getLastModifiedDate());
		Assert.assertNull("SeatHold Confirmation Code should be null.", seatHold.getReservationConfirmationCode());

		// Test Seats are saved as hold
		SeatHold savedSeatHold = InMemoryTicketStore.SEAT_HOLDS.get(seatHold.getSeatHoldId());

		Assert.assertNotNull("Seat Hold has not been saved. ", savedSeatHold);

		// Test if seats are no unavaialable
		for (int seatId : seatIds) {
			Integer seatHoldId = InMemoryTicketStore.UNAVAILABLE_SEATS.get(seatId);

			Assert.assertNotNull("Seat Not put in Unavaialble bucket", seatHoldId);

			Assert.assertEquals("seatHoldId not matched with seatId", seatHoldId,
					Integer.valueOf(seatHold.getSeatHoldId()));

		}
	}

	/**
	 * Tests Reserve Seats
	 * 
	 * @throws SeatHoldExpiredException
	 */
	@Test
	public void testReserveSeats() throws SeatHoldExpiredException {

		String customerEmail = "foo@foo.com";
		int noOfSeats = 1000;
		int noOfSeatsNeeded = 100;

		int[] seatIds = getSeatIds(noOfSeats, noOfSeatsNeeded);

		Assert.assertNotNull("seatId's is null", seatIds);
		SeatHold seatHold = ticketingStore.createSeatHold(seatIds, customerEmail);

		Assert.assertNotNull("SeatHold is null.", seatHold);

		String confirmationCode = ticketingStore.generateConfirmationCode();
		Assert.assertNotNull("ConfirmationCode is null", confirmationCode);

		ticketingStore.updateSeatHold(seatHold, SeatHoldStatus.RESERVED, confirmationCode);

		SeatHold savedSeatHold = ticketingStore.getSeatHoldById(seatHold.getSeatHoldId());

		Assert.assertNotNull("No Saved SeatHold", savedSeatHold);
		Assert.assertEquals("SeatHold status mismatch", SeatHoldStatus.RESERVED, savedSeatHold.getSeatHoldStatus());
		Assert.assertEquals("SeatHold confirmationCode mismatch", confirmationCode,
				savedSeatHold.getReservationConfirmationCode());

	}

	/**
	 * Tests the working of seat Expiry.
	 */
	@Test
	public void expiredSeatHoldTest() {

		String customerEmail = "foo@foo.com";
		int noOfSeatsNeeded = 10;
		int noOfSeats = 1000;

		int[] seatIds = getSeatIds(noOfSeats, noOfSeatsNeeded);

		// set Store so the SeatHolds Expire immediately after creation.
		int originalSeatHoldExpiryTime = ticketingStore.getSeatHoldExpiryTime();
		ticketingStore.setSeatHoldExpiryTime(0);

		Assert.assertNotNull("seatId's is null", seatIds);
		SeatHold seatHold = ticketingStore.createSeatHold(seatIds, customerEmail);

		// seat hold would have expired by now

		try {
			Thread.sleep(1000);
			ticketingStore.getSeatHoldById(seatHold.getSeatHoldId());

			// Exception should be thrown before this line.
			Assert.fail("SeatHold should have expired");
		} catch (SeatHoldExpiredException se) {

			Assert.assertNotNull("SeatHold object in Exception is null", se.getSeatHold());
			Assert.assertEquals("SeatHold expiry Time mismatch", 0, se.getExpiryInSeconds());
		} catch (InterruptedException e) {
			// do nothing
		}

		Assert.assertTrue("SeatHold has not been expunged",
				!InMemoryTicketStore.SEAT_HOLDS.containsKey(seatHold.getSeatHoldId()));
		ticketingStore.setSeatHoldExpiryTime(originalSeatHoldExpiryTime);

	}

	/*
	 * HELPER METHODs
	 */

	/**
	 * Cleans Up the ticketing store.
	 */
	private void cleanUpStore() {
		InMemoryTicketStore.SEAT_HOLDS.clear();
		InMemoryTicketStore.UNAVAILABLE_SEATS.clear();
	}

	/**
	 * Helper method to test
	 * {@link com.walmart.ticketservice.persistance.InMemoryTicketStore#getAvailabileSeats(java.lang.Integer)}
	 * 
	 * @param noOfSeats
	 * @param noOfSeatsNeeded
	 * @return
	 */
	private int[] getSeatIds(int noOfSeats, int noOfSeatsNeeded) {
		cleanUpStore();
		ticketingStore.setNoOfSeats(noOfSeats);
		int[] seatIds = ticketingStore.getAvailabileSeats(noOfSeatsNeeded);
		return seatIds;
	}

	/**
	 * Test helper to test various seatNo and needed seats combination.
	 * 
	 * @param noOfSeats
	 * @param noOfSeatsNeeded
	 */
	private void testGetAvailabileSeats(int noOfSeats, Integer noOfSeatsNeeded) {

		cleanUpStore();
		InMemoryTicketStore store = new InMemoryTicketStore();
		int[] availableSeats = store.getAvailabileSeats(noOfSeatsNeeded);

		Assert.assertNotNull("Test Failed: Available Seats is null!!", availableSeats);
		Assert.assertEquals("Test Failed : Avaialbe seats does not match expected value!!",
				noOfSeatsNeeded == null ? Integer.valueOf(noOfSeats) : noOfSeatsNeeded,
				availableSeats != null ? Integer.valueOf(availableSeats.length) : null);

	}

}
