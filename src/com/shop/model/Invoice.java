package com.shop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private int invoiceId;
    private int userId;
    private Integer customerId;     // NULL được — khách lẻ
    private String customerName;    // tên khách (join từ customer)
    private BigDecimal total;
    private String note;
    private LocalDateTime createdAt;

    private List<InvoiceDetail> details = new ArrayList<>();

    public Invoice() {}

    public int getInvoiceId()                       { return invoiceId; }
    public void setInvoiceId(int invoiceId)         { this.invoiceId = invoiceId; }

    public int getUserId()                          { return userId; }
    public void setUserId(int userId)               { this.userId = userId; }

    public Integer getCustomerId()                  { return customerId; }
    public void setCustomerId(Integer customerId)   { this.customerId = customerId; }

    public String getCustomerName()                 { return customerName; }
    public void setCustomerName(String customerName){ this.customerName = customerName; }

    public BigDecimal getTotal()                    { return total; }
    public void setTotal(BigDecimal total)          { this.total = total; }

    public String getNote()                         { return note; }
    public void setNote(String note)                { this.note = note; }

    public LocalDateTime getCreatedAt()                     { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)       { this.createdAt = createdAt; }

    public List<InvoiceDetail> getDetails()                 { return details; }
    public void setDetails(List<InvoiceDetail> details)     { this.details = details; }

    // Hiển thị tổng tiền có định dạng
    public String getTotalDisplay() {
        if (total == null) return "0 ₫";
        return String.format("%,.0f ₫", total);
    }

    // Hiển thị ngày tạo
    public String getCreatedAtDisplay() {
        if (createdAt == null) return "";
        return String.format("%02d/%02d/%04d %02d:%02d",
                createdAt.getDayOfMonth(), createdAt.getMonthValue(),
                createdAt.getYear(), createdAt.getHour(), createdAt.getMinute());
    }
}