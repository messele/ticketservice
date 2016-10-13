package com.walmart.ticketingservice;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.stream.IntStream;

import com.walmart.ticketservice.TicketService;
import com.walmart.ticketservice.TicketingServiceImpl;
import com.walmart.ticketservice.model.BaseModel;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.SeatHoldStatus;
import com.walmart.ticketservice.persistance.TicketStore;

import junit.framework.Assert;

public class TicketingServiceTest extends BaseModel {

	/**
	 * Tests {@link TicketService#numSeatsAvailable()}
	 */
	@Test
	public void testNumOfSeatsAvailable() {

		// Create a mock store.
		TicketStore mockStore = mock(TicketStore.class);

		// mock methods that will be called by getAvailableSeats.
		when(mockStore.getAvailabileSeats(null)).thenReturn(IntStream.range(1, 101).toArray());

		// Inject
		TicketingServiceImpl service = new TicketingServiceImpl();
		service.setStore(mockStore);

		// Verify
		Assert.assertEquals("Mismatch in number of available seats", service.numSeatsAvailable());
	}

	/**
	 * Tests {@link TicketingServiceImpl#findAndHoldSeats(int, String)}
	 */
	@Test
	public void testFindAndHoldSeats() {

		// mock data
		int[] expectedSeats = new int[] { 1, 2, 3, 6, 8, 9, 10, 20, 22, 31 };
		String email = "mock@mock.com";

		SeatHold seatHold = new SeatHold() {
			{
				setSeatHoldId(1);
				setSeatIds(expectedSeats);
				setCreatedDate(new Date());
				setCustomerEmail(email);
			}
		};

		// Create a mock store.
		TicketStore mockStore = mock(TicketStore.class);

		// mock methods that will be called by findAndHoldSeats.
		when(mockStore.getBestSeats(expectedSeats.length)).thenReturn(expectedSeats);
		when(mockStore.createSeatHold(expectedSeats, email)).thenReturn(seatHold);

		// inject
		TicketingServiceImpl service = new TicketingServiceImpl();
		service.setStore(mockStore);

		// Verify
		Assert.assertEquals("Mismatch in SeatHold created: ", seatHold,
				service.findAndHoldSeats(expectedSeats.length, email));
	}

	/**
	 * Tests {@link TicketingServiceImpl#reserveSeats(int, String)}
	 */
	@Test
	public void testReserveSeats() {

		// mock data
		final int seatHoldId = 1;
		final String email = "mail@mail.com";
		final SeatHold seatHold = new SeatHold();
		final String confirmationCode = "mockCode";

		// Create a mock store.
		TicketStore mockStore = mock(TicketStore.class);

		// mock methods that will be called by Reserve Seats.
		when(mockStore.getSeatHoldById(seatHoldId)).thenReturn(seatHold);
		when(mockStore.generateConfirmationCode()).thenReturn(confirmationCode);
		doAnswer(new Answer<SeatHold>() {
			@Override
			public SeatHold answer(InvocationOnMock invocation) throws Throwable {

				seatHold.setReservationConfirmationCode(confirmationCode);
				seatHold.setSeatHoldStatus(SeatHoldStatus.RESERVED);
				return seatHold;
			}
		}).when(mockStore).updateSeatHold(seatHold, SeatHoldStatus.RESERVED, confirmationCode);

		// inject
		TicketingServiceImpl service = new TicketingServiceImpl();
		service.setStore(mockStore);

		// verify
		Assert.assertEquals("Mismatch in confirationCode", confirmationCode, service.reserveSeats(seatHoldId, email));

	}
}
