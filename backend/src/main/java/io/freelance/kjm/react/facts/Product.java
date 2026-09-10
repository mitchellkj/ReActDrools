package io.freelance.kjm.react.facts;

import java.io.Serializable;
import java.util.Objects;

/**
 * Drools Fact representing a curated hardware catalog item with benchmark baseline pricing.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String category;
    private String brand;
    private Double benchmarkUsdPrice;

    public Product() {
    }

    public Product(String id, String name, String category, String brand, Double benchmarkUsdPrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.benchmarkUsdPrice = benchmarkUsdPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getBenchmarkUsdPrice() {
        return benchmarkUsdPrice;
    }

    public void setBenchmarkUsdPrice(Double benchmarkUsdPrice) {
        this.benchmarkUsdPrice = benchmarkUsdPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
               Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", benchmarkUsdPrice=" + benchmarkUsdPrice +
                '}';
    }
}
