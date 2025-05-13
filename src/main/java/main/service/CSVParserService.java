package main.service;

import lombok.RequiredArgsConstructor;
import main.respository.DiscountRepository;
import main.respository.ProductRepository;
import main.respository.SupermarketRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CSVParserService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final SupermarketRepository supermarketRepository;


}
