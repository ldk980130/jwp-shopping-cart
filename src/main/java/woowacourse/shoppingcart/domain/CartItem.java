package woowacourse.shoppingcart.domain;

import static lombok.EqualsAndHashCode.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
public class CartItem {

    @Include
    private Long id;
    private final Long productId;
    private final String name;
    private final int price;
    private final String imageUrl;
    private final int quantity;

    public CartItem(Product product, int quantity) {
        this(null, product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), quantity);
    }

    public CartItem(Long id, Product product, int quantity) {
        this(id, product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), quantity);
    }

    public CartItem createWithId(Long id) {
        return new CartItem(id, productId, name, price, imageUrl, quantity);
    }

    public boolean isSameProductId(Long productId) {
        return this.productId.equals(productId);
    }
}
