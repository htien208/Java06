package com.shop.model;

import java.math.BigDecimal;

public class InvoiceDetail {

    private int detailId;
    private int invoiceId;
    private int productId;
    private String productName; // join từ product
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public InvoiceDetail() {}

    public InvoiceDetail(int productId, String productName,
                         int quantity, BigDecimal unitPrice) {
        this.productId   = productId;
        this.productName = productName;
        this.quantity    = quantity;
        this.unitPrice   = unitPrice;
        this.subtotal    = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }


    public int getDetailId()                    { return detailId; }
    public void setDetailId(int detailId)       { this.detailId = detailId; }

    public int getInvoiceId()                   { return invoiceId; }
    public void setInvoiceId(int invoiceId)     { this.invoiceId = invoiceId; }

    public int getProductId()                   { return productId; }
    public void setProductId(int productId)     { this.productId = productId; }

    public String getProductName()                  { return productName; }
    public void setProductName(String productName)  { this.productName = productName; }

    public int getQuantity()                    { return quantity; }
    public void setQuantity(int quantity)       { this.quantity = quantity; }

    public BigDecimal getUnitPrice()                    { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice)      { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal()                     { return subtotal; }
    public void setSubtotal(BigDecimal subtotal)        { this.subtotal = subtotal; }

    // Tính lại subtotal khi quantity hoặc unitPrice thay đổi
    public void recalcSubtotal() {
        if (unitPrice != null && quantity > 0) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Hiển thị trong JTable
    public String getUnitPriceDisplay() {
        return unitPrice != null ? String.format("%,.0f ₫", unitPrice) : "0 ₫";
    }

    public String getSubtotalDisplay() {
        return subtotal != null ? String.format("%,.0f ₫", subtotal) : "0 ₫";
    }
}