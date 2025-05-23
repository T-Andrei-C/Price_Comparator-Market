package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.DTO.ShoppingListDTO;
import main.service.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/baskets")
public class BasketController {

    private final BasketService basketService;

    @PostMapping("/add/supermarket/{supermarketId}/user/email/{email}")
    private ResponseEntity<String> addSupermarketToBasket(@PathVariable Long supermarketId, @PathVariable String email) {
        return ResponseEntity.ok(basketService.addSupermarketToBasket(supermarketId, email));
    }

    @DeleteMapping("/remove/supermarket/{supermarketId}/user/email/{email}")
    private ResponseEntity<String> removeSupermarketFromBasket(@PathVariable Long supermarketId, @PathVariable String email) {
        return ResponseEntity.ok(basketService.removeSupermarketFromBasket(supermarketId, email));
    }

    @GetMapping("/to/shopping_lists/user/{userEmail}")
    private List<ShoppingListDTO> transformBasketIntoShoppingLists(@PathVariable String userEmail) {
        return basketService.transformBasketIntoShoppingLists(userEmail);
    }

}
