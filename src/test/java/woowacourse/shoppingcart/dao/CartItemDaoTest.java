package woowacourse.shoppingcart.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;

import woowacourse.auth.dao.CustomerDao;
import woowacourse.auth.domain.Customer;
import woowacourse.shoppingcart.ProductInsertUtil;
import woowacourse.shoppingcart.domain.CartItem;
import woowacourse.shoppingcart.domain.Product;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CartItemDaoTest {

    private Long customerId;
    private Product product;

    private final CartItemDao cartItemDao;
    private final ProductInsertUtil productInsertUtil;
    private final CustomerDao customerDao;

    public CartItemDaoTest(DataSource dataSource) {
        cartItemDao = new CartItemDao(dataSource, new ProductDao(dataSource));
        productInsertUtil = new ProductInsertUtil(dataSource);
        customerDao = new CustomerDao(dataSource);
    }

    @BeforeEach
    void init() {
        customerId = customerDao.save(Customer.builder()
            .nickname("does")
            .email("asd@gmail.com")
            .password("1asd!")
            .build())
            .getId();
        Long productId = productInsertUtil.insert("banana", 1_000, "woowa1.com");
        product = new Product(productId, "banana", 1_000, "woowa1.com");
    }

    @DisplayName("카트에 아이템을 담으면, 담긴 카트 아이디를 반환한다.")
    @Test
    void addCartItem() {
        // given
        CartItem cartItem = new CartItem(product, 2);

        // when
        CartItem saved = cartItemDao.save(customerId, cartItem);

        // then
        assertThat(saved.getId()).isNotNull();
    }

    @DisplayName("customer id로 cartItem을 조회한다.")
    @Test
    void getItems() {
        // given
        cartItemDao.save(customerId, new CartItem(product, 3));

        Long productId2 = productInsertUtil.insert("apple", 1_000, "woowa1.com");
        Product product2 = new Product(productId2, "apple", 1000, "woowa1.com");
        cartItemDao.save(customerId, new CartItem(product2, 2));

        // when
        List<CartItem> items = cartItemDao.findByCustomerId(customerId);

        // then
        assertThat(items.size()).isEqualTo(2);
        assertThat(items)
            .map(CartItem::getName)
            .containsOnly("banana", "apple");
    }
}
