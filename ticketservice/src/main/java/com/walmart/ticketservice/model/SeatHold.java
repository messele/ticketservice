package com.walmart.ticketservice.model;

import java.util.Date;

public class SeatHold extends BaseModel {

	private int seatHoldId;

	private int[] seatIds;

	private String customerEmail;

	private SeatHoldStatus seatHoldStatus;

	private Date createdDate;

	private Date lastModifiedDate;

	private String reservationConfirmationCode;

	/**
	 * @return the seatHoldId
	 */
	public int getSeatHoldId() {
		return seatHoldId;
	}

	/**
	 * @param seatHoldId
	 *            the seatHoldId to set
	 */
	public void setSeatHoldId(int seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	/**
	 * @return the seatIds
	 */
	public int[] getSeatIds() {
		return seatIds;
	}

	/**
	 * @param seatIds
	 *            the seatIds to set
	 */
	public void setSeatIds(int[] seatIds) {
		this.seatIds = seatIds;
	}

	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail
	 *            the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	/**
	 * @return the seatHoldStatus
	 */
	public SeatHoldStatus getSeatHoldStatus() {
		return seatHoldStatus;
	}

	/**
	 * @param seatHoldStatus
	 *            the seatHoldStatus to set
	 */
	public void setSeatHoldStatus(SeatHoldStatus seatHoldStatus) {
		this.seatHoldStatus = seatHoldStatus;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 *            the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the reservationConfirmationCode
	 */
	public String getReservationConfirmationCode() {
		return reservationConfirmationCode;
	}

	/**
	 * @param reservationConfirmationCode
	 *            the reservationConfirmationCode to set
	 */
	public void setReservationConfirmationCode(String reservationConfirmationCode) {
		this.reservationConfirmationCode = reservationConfirmationCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SeatHold) {
			return this.getSeatHoldId() == SeatHold.class.cast(obj).getSeatHoldId();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.seatHoldId;
	}
}
