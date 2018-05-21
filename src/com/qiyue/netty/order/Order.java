package com.qiyue.netty.order;

import java.beans.Customizer;

import io.netty.channel.AddressedEnvelope;

public class Order {
	
	private long orderNumber;
	
	private Customer customer;
	
	private Address billTo;
	
	private Shipping shipping;
	
	private Address shipTo;
	
	private float total;

	/**
	 * @return the orderNumber
	 */
	public long getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the billTo
	 */
	public Address getBillTo() {
		return billTo;
	}

	/**
	 * @param billTo the billTo to set
	 */
	public void setBillTo(Address billTo) {
		this.billTo = billTo;
	}

	/**
	 * @return the shipping
	 */
	public Shipping getShipping() {
		return shipping;
	}

	/**
	 * @param shipping the shipping to set
	 */
	public void setShipping(Shipping shipping) {
		this.shipping = shipping;
	}

	/**
	 * @return the shipTo
	 */
	public Address getShipTo() {
		return shipTo;
	}

	/**
	 * @param shipTo the shipTo to set
	 */
	public void setShipTo(Address shipTo) {
		this.shipTo = shipTo;
	}

	/**
	 * @return the total
	 */
	public float getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(float total) {
		this.total = total;
	}
	
	
	

}
