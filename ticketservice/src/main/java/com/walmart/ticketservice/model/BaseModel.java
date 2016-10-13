package com.walmart.ticketservice.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Base class for all  model objects
 * 
 * overrides the {@link BaseModel#toString()} for pretty logging purpose.
 *
 */
public abstract class BaseModel {

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
