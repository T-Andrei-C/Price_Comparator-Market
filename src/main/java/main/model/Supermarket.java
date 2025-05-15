package main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "supermarkets")
public class Supermarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String supermarket_name;
    private String product_name;
    private String category;
    private String brand;
    private Double quantity;
    private String unit;
    private LocalDate publish_date;
    private Double product_price;
    private String currency;

    @OneToMany(mappedBy = "supermarket")
    private Set<Discount> discounts;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Supermarket that = (Supermarket) o;
        return Objects.equals(supermarket_name, that.supermarket_name) && Objects.equals(product_name, that.product_name) && Objects.equals(category, that.category) && Objects.equals(brand, that.brand) && Objects.equals(quantity, that.quantity) && Objects.equals(unit, that.unit) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supermarket_name, product_name, category, brand, quantity, unit, currency);
    }
}
