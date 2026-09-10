package io.freelance.kjm.react.controllers;

import io.freelance.kjm.react.dto.OptionsResponse;
import io.freelance.kjm.react.facts.CurrencyExchange;
import io.freelance.kjm.react.facts.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller returning curated hardware catalog items and currency conversion benchmarks.
 */
@RestController
@RequestMapping("/api/options")
public class OptionsController {

    private static final List<Product> PRODUCTS = Arrays.asList(
            new Product("macbook_pro_14", "Apple MacBook Pro 14\"", "Laptop", "Apple", 1599.0),
            new Product("dell_xps_15", "Dell XPS 15", "Laptop", "Dell", 1299.0),
            new Product("lenovo_thinkpad_x1", "Lenovo ThinkPad X1 Carbon", "Laptop", "Lenovo", 1449.0),
            new Product("asus_rog_g14", "ASUS ROG Zephyrus G14", "Gaming Laptop", "ASUS", 1599.0),
            new Product("ipad_pro_m4", "Apple iPad Pro M4", "Tablet", "Apple", 999.0),
            new Product("galaxy_tab_s9", "Samsung Galaxy Tab S9", "Tablet", "Samsung", 799.0),
            new Product("hp_spectre_x360", "HP Spectre x360", "2-in-1 Laptop", "HP", 1399.0)
    );

    private static final List<CurrencyExchange> CURRENCIES = Arrays.asList(
            new CurrencyExchange("EUR", "Euro", "€", 0.85),
            new CurrencyExchange("GBP", "British Pound", "£", 0.77),
            new CurrencyExchange("JPY", "Japanese Yen", "¥", 150.0),
            new CurrencyExchange("CAD", "Canadian Dollar", "C$", 1.36),
            new CurrencyExchange("AUD", "Australian Dollar", "A$", 1.52),
            new CurrencyExchange("CHF", "Swiss Franc", "Fr", 0.88),
            new CurrencyExchange("INR", "Indian Rupee", "₹", 83.5)
    );

    @GetMapping
    public ResponseEntity<OptionsResponse> getOptions() {
        return ResponseEntity.ok(new OptionsResponse(PRODUCTS, CURRENCIES));
    }
}
