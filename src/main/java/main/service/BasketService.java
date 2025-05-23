package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.model.DTO.ProductWithPriceDTO;
import main.model.DTO.ShoppingListDTO;
import main.model.Discount;
import main.model.Supermarket;
import main.model.user.Basket;
import main.model.user.User;
import main.repository.BasketRepository;
import main.repository.SupermarketRepository;
import main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final SupermarketRepository supermarketRepository;
    private final UserRepository userRepository;

    public String addSupermarketToBasket(Long supermarketId, String userEmail) {
        Supermarket supermarket = supermarketRepository.findById(supermarketId).orElse(null);
        User user = userRepository.findUserByEmail(userEmail).orElse(null);

        if (user == null) {
            return "User does not exist";
        }

        if (supermarket != null) {
            Basket userBasket = user.getBasket();
            userBasket.getSupermarkets().add(supermarket);
            basketRepository.save(userBasket);

            return "Supermarket added successfully";
        }

        return "Supermarket with the id " + supermarketId + " does not exist";
    }

    public String removeSupermarketFromBasket(Long supermarketId, String userEmail) {
        Supermarket supermarket = supermarketRepository.findById(supermarketId).orElse(null);
        User user = userRepository.findUserByEmail(userEmail).orElse(null);

        if (user == null) {
            return "User does not exist";
        }

        if (supermarket != null) {
            Basket userBasket = user.getBasket();
            userBasket.getSupermarkets().remove(supermarket);
            basketRepository.save(userBasket);

            return "Supermarket removed successfully";
        }

        return "Supermarket with the id " + supermarketId + " does not exist";
    }

    public List<ShoppingListDTO> transformBasketIntoShoppingLists(String userEmail) {
        User user = userRepository.findUserByEmail(userEmail).orElse(null);
        List<ShoppingListDTO> shoppingLists = new ArrayList<>();

        if (user != null) {
            for (Supermarket supermarket : user.getBasket().getSupermarkets()) {
                String supermarketName = supermarket.getName();

                if (shoppingLists.isEmpty()) {
                    createShoppingList(shoppingLists, supermarket);
                } else {
                    boolean checkIfExist = false;
                    for (ShoppingListDTO shoppingList : shoppingLists) {
                        //verify if a shoppingList with the supermarketName has already been created and if it's true,
                        //we don't create another shoppingList, but add a new productWithPrice to it
                        if (shoppingList.getSupermarket_name().equals(supermarketName)) {

                            shoppingList.getProducts().add(createProductWithPrice(supermarket));
                            checkIfExist = true;
                        }
                    }

                    if (!checkIfExist) {
                        createShoppingList(shoppingLists, supermarket);
                    }

                }
            }
        }

        return shoppingLists;

    }

    private void createShoppingList(List<ShoppingListDTO> shoppingLists, Supermarket supermarket) {
        shoppingLists.add(
                ShoppingListDTO.builder()
                        .shopping_list_name(supermarket.getName() + " shopping list")
                        .products(new ArrayList<>(
                                List.of(createProductWithPrice(supermarket))
                        ))
                        .currency(supermarket.getCurrency())
                        .supermarket_name(supermarket.getName())
                        .build()
        );
    }

    private ProductWithPriceDTO createProductWithPrice(Supermarket supermarket) {
        LocalDate simulateDate = SimulateDate.getDate();
        Discount discount = supermarket.getDiscount();

        boolean hasDiscount = false;
        Double percentageOfDiscount = 0d;
        Double discountPrice = supermarket.getProduct_price();

        if (discount != null) {
            if (DiscountCalculator.checkIfDiscountApplies(simulateDate, discount)) {
                hasDiscount = true;
                percentageOfDiscount = discount.getPercentage_of_discount();
                discountPrice = DiscountCalculator.applyDiscountToProductPrice(discountPrice, percentageOfDiscount);
            }
        }

        return ProductWithPriceDTO.builder()
                .price(supermarket.getProduct_price())
                .product(supermarket.getProduct())
                .discount_price(discountPrice)
                .has_discount(hasDiscount)
                .percentage_of_discount(percentageOfDiscount)
                .build();
    }

}
