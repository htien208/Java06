package com.shop.model;

import java.math.BigDecimal;

public class Product {

    private int productId;
    private String name;
    private String brand;
    private String size;
    private String color;
    private BigDecimal price;
    private int stock;
    private String status; // "ACTIVE" hoặc "INACTIVE"

    public Product() {}

    public Product(int productId, String name, String brand,
                   String size, String color, BigDecimal price,
                   int stock, String status) {
        this.productId = productId;
        this.name      = name;
        this.brand     = brand;
        this.size      = size;
        this.color     = color;
        this.price     = price;
        this.stock     = stock;
        this.status    = status;
    }

    public int getProductId()                  { return productId; }
    public void setProductId(int productId)    { this.productId = productId; }

    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }

    public String getBrand()                   { return brand; }
    public void setBrand(String brand)         { this.brand = brand; }

    public String getSize()                    { return size; }
    public void setSize(String size)           { this.size = size; }

    public String getColor()                   { return color; }
    public void setColor(String color)         { this.color = color; }

    public BigDecimal getPrice()               { return price; }
    public void setPrice(BigDecimal price)     { this.price = price; }

    public int getStock()                      { return stock; }
    public void setStock(int stock)            { this.stock = stock; }

    public String getStatus()                  { return status; }
    public void setStatus(String status)       { this.status = status; }

    // Hiển thị status tiếng Việt — dùng trong UI
    public String getStatusDisplay() {
        return "ACTIVE".equalsIgnoreCase(status) ? "Đang bán" : "Ngừng bán";
    }

    // Hiển thị giá có định dạng — dùng trong JTable
    public String getPriceDisplay() {
        if (price == null) return "0 ₫";
        return String.format("%,.0f ₫", price);
    }

    @Override
    public String toString() {
        return name + " - " + brand;
    }
}