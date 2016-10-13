package com.walmart.ticketingservice;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.walmart.ticketservice.TicketService;
import com.walmart.ticketservice.persistance.TicketStore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
public final class SpringInjectionTest extends BaseTest {

	@Autowired
	TicketStore store;
	
	@Autowired
	TicketService service;

	
	@Test
	public void InjectionTestForTicketStore() {
	
		assertNotNull("Autowiring of Ticketing Store failed", store);
		assertTrue("No of seats cannot be less or equal to zero", store.getNoOfSeats() > 0);
		assertTrue("SeatHold expiry time cannot be less than zero.", store.getSeatHoldExpiryTime() > 0);
		
	}

	
	@Test
	public void  injectionTestForTicketService() {
		assertNotNull("Autowiring of Ticketing Store failed", service);
		assertTrue("No of seats cannot be less or equal to zero", service.numSeatsAvailable() > 0);
	}
}
