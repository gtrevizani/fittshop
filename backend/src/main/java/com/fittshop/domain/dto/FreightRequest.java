package com.fittshop.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public class FreightRequest {
    private String zipCode;
    private BigDecimal subtotal;
    private List<CartItemDto> items;

    public FreightRequest() {}

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }
}
