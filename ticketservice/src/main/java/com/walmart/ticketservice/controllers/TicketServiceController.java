package com.walmart.ticketservice.controllers;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ViewResolver;

import com.walmart.ticketservice.TicketService;
import com.walmart.ticketservice.model.SeatHold;
import com.walmart.ticketservice.model.ui.HoldSeatsInput;
import com.walmart.ticketservice.model.ui.ReserveSeatsInput;
import com.walmart.ticketservice.persistance.exception.SeatHoldExpiredException;
import com.walmart.ticketservice.validators.FormValidator;

/**
 * 
 * Controller class that uses Spring {@link ViewResolver} to get specific views.
 * 
 * <pre>
 * Design Considerations: 
 * 			- the Landing page (defined in  <WEB-INF>/views/landing.jsp)  defines the core html with fragments that include a header, footer and content.
 * 			- each webmethod injects the specific page content fragment into the landing page.
 * </pre>
 *
 */
@Controller
public class TicketServiceController {

	/**
	 * CONSANTS
	 */
	private static final String PAGE_NAME_LANDING = "landing";
	private static final String PAGE_NAME_HOLD_SEATS = "holdSeats";
	private static final String PAGE_NAME_HOME = "home";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	TicketService service;

	@Autowired
	FormValidator formValidator;

	/**
	 * Defines the home page.
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(path = { "/", "/home", "/reserve" }, method = RequestMethod.GET)
	public String home(Model model) {

		logger.debug(">> in Home");

		HoldSeatsInput holdSeatInput = null;

		if (model.asMap().containsKey("holdSeatsInput")) {
			holdSeatInput = (HoldSeatsInput) model.asMap().get("holdSeatsInput");
		}
		model.addAttribute("holdSeatsInput", holdSeatInput != null ? holdSeatInput : new HoldSeatsInput());

		model.addAttribute("noOfSeatsNeeded", holdSeatInput != null ? holdSeatInput.getNoOfSeatsNeeded() : null);

		model.addAttribute("email", holdSeatInput != null ? holdSeatInput.getEmail() : null);

		return goToPage(model, PAGE_NAME_HOME);
	}

	/**
	 * Landing for SeatHold post Request..
	 * 
	 * @param model
	 * @param input
	 * @param result
	 * @return
	 */
	@RequestMapping(path = { "/", "/home" }, method = RequestMethod.POST)
	public String holdSeats(Model model, @ModelAttribute("holdSeatsInput") @Validated HoldSeatsInput input,
			BindingResult result) {

		logger.debug(">> In holdSeats [input: {}]", input);

		if (result.hasErrors()) {

			return goToPage(model, PAGE_NAME_HOME);
		}

		SeatHold seatHold = null;
		String error = null;
		try {
			seatHold = service.findAndHoldSeats(input.getNoOfSeatsNeeded(), input.getEmail());
		} catch (IllegalArgumentException il) {
			error = il.getMessage();
			model.addAttribute("error", il.getMessage());
			logger.error("Validation Error :" + il.getMessage());
			return goToPage(model, PAGE_NAME_HOME);
		}

		if (seatHold == null) {
			error = "Not Enough Seats Available.";

		}

		if (error != null) {
			model.addAttribute("error", error);
			return goToPage(model, PAGE_NAME_HOME);
		}

		model.addAttribute("seatHoldId", seatHold.getSeatHoldId());
		model.addAttribute("seats", Arrays.toString(seatHold.getSeatIds()));
		model.addAttribute("email", seatHold.getCustomerEmail());

		ReserveSeatsInput reserveSeatsInput = null;

		if (model.asMap().containsKey("reserveSeatsInput")) {
			reserveSeatsInput = (ReserveSeatsInput) model.asMap().get("reserveSeatsInput");
		} else {
			reserveSeatsInput = new ReserveSeatsInput();
		}
		reserveSeatsInput.setSeatHoldId(seatHold.getSeatHoldId());
		reserveSeatsInput.setEmail(seatHold.getCustomerEmail());
		model.addAttribute("reserveSeatsInput", reserveSeatsInput);

		return goToPage(model, PAGE_NAME_HOLD_SEATS);
	}

	/**
	 * Landing point for POST request on reserve.
	 * 
	 * @param model
	 * @param input
	 * @param result
	 * @return
	 */
	@RequestMapping(path = { "/reserve" }, method = RequestMethod.POST)
	public String reserve(Model model, @ModelAttribute("reserveSeatsInput") @Validated ReserveSeatsInput input,
			BindingResult result) {

		logger.info(">> In Reserve Seats...");
		if (input != null && input.getSeatHoldId() != null) {

			try {
				String confirmationCode = service.reserveSeats(input.getSeatHoldId(), input.getEmail());
				model.addAttribute("confirmationCode", confirmationCode);
				return goToPage(model, "confirmation");
			} catch (SeatHoldExpiredException e) {

				model.addAttribute("error", "Reservation Failed: Your Seat Hold has expired!!");
			} catch (IllegalArgumentException il) {
				model.addAttribute("error", il.getMessage());
			}
		} else {
			model.addAttribute("error",
					"Unknown Error Occurred preventing Seats to be put on hold. Please contact System Admin.");
		}

		return goToPage(model, PAGE_NAME_HOME);

	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(formValidator);
	}

	/**
	 * Helper class to navigate to a specific page fragment.
	 * 
	 * @param model
	 * @param pageFragmentName
	 * @return
	 */
	private String goToPage(Model model, String pageFragmentName) {
		model.addAttribute("content", pageFragmentName);
		model.addAttribute("seatsAvailable", service.numSeatsAvailable());
		return PAGE_NAME_LANDING;
	}

}
