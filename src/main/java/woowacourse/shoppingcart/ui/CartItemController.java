package woowacourse.shoppingcart.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import woowacourse.auth.domain.Customer;
import woowacourse.auth.support.Login;
import woowacourse.shoppingcart.domain.CartItem;
import woowacourse.shoppingcart.dto.CartItemResponse;
import woowacourse.shoppingcart.dto.QuantityRequest;
import woowacourse.shoppingcart.application.CartService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartItemController {

    private final CartService cartService;

    @GetMapping("/api/customers/{customerName}/cartItems")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable final String customerName) {
        return ResponseEntity.ok().body(cartService.findCartsByCustomerName(customerName));
    }

    @PutMapping("/cart/products/{productId}")
    public ResponseEntity<CartItemResponse> addCartItem(
        @Login Customer customer,
        @PathVariable Long productId,
        @RequestBody QuantityRequest quantityRequest) {
        CartItem cartItem = cartService.addItem(customer.getId(), productId, quantityRequest.getQuantity());
        return ResponseEntity.created(makeUri(cartItem.getId()))
            .body(CartItemResponse.from(cartItem));
    }

    private URI makeUri(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{cartId}")
                .buildAndExpand(id)
                .toUri();
    }


    @DeleteMapping("/api/customers/{customerName}/cartItems/{cartId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable final String customerName,
                                         @PathVariable final Long cartId) {
        cartService.deleteCart(customerName, cartId);
        return ResponseEntity.noContent().build();
    }
}
