package com.walmart.ticketservice.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.walmart.ticketservice.model.ui.HoldSeatsInput;
import com.walmart.ticketservice.model.ui.ReserveSeatsInput;

public class FormValidator implements Validator {

	
	@Override
	public boolean supports(Class<?> arg0) {

		return HoldSeatsInput.class.equals(arg0) || ReserveSeatsInput.class.equals(arg0);
	}

	@Override
	public void validate(Object target, Errors errors) {

		if (target != null && HoldSeatsInput.class.isInstance(target)) {
			
			HoldSeatsInput input = HoldSeatsInput.class.cast(target);
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Required Field email cannot be empty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "noOfSeatsNeeded", "", "Required Field noOfSeats cannot be empty");

			if(input.getNoOfSeatsNeeded() == null || input.getNoOfSeatsNeeded() < 1) {
				
				errors.reject("noOfSeatsNeeded", "Invalid Seats needed : value should be greater than 0");
			}
		}

	}

}
