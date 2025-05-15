package main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "supermarkets")
public class SupermarketHistory {

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
}
