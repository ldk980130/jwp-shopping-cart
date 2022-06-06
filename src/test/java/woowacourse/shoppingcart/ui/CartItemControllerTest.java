package woowacourse.shoppingcart.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import woowacourse.auth.application.AuthService;
import woowacourse.auth.application.CustomerService;
import woowacourse.auth.dto.customer.CustomerRequest;
import woowacourse.auth.dto.token.TokenRequest;
import woowacourse.shoppingcart.ProductInsertUtil;
import woowacourse.shoppingcart.application.CartService;
import woowacourse.shoppingcart.dto.CartItemResponse;
import woowacourse.shoppingcart.dto.QuantityRequest;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class CartItemControllerTest {

	private Long customerId;
	private Long productId;
	private String token;

	@Autowired
	private CustomerService customerService;
	@Autowired
	private ProductInsertUtil productInsertUtil;
	@Autowired
	private AuthService authService;
	@Autowired
	private CartService cartService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void init() {
		String email = "123@gmail.com";
		String password = "a1234!";
		customerId = customerService.signUp(new CustomerRequest(email, password, "does"))
			.getId();

		productId = productInsertUtil.insert("치킨", 20000, "test.jpg");
		token = authService.login(new TokenRequest(email, password))
			.getAccessToken();
	}

	@DisplayName("장바구니에 상품을 추가한다.")
	@Test
	void addCartItem() throws Exception {
		// given
		QuantityRequest request = new QuantityRequest(13);

		// when
		ResultActions result = mockMvc.perform(put("/cart/products/" + productId)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		CartItemResponse response = new CartItemResponse(
			productId, "치킨", "test.jpg", 20000, 13);
		result.andExpect(status().isCreated())
			.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@DisplayName("로그인 사용자의 장바구니 목록을 조회한다.")
	@Test
	void getItems() throws Exception {
		// given
		cartService.addItem(customerId, productId, 2);
		Long productId2 = productInsertUtil.insert("콜라", 1500, "test.jpg");
		cartService.addItem(customerId, productId2, 3);

		// when
		ResultActions result = mockMvc.perform(get("/cart")
			.header("Authorization", "Bearer " + token));

		// then
		CartItemResponse response1 = new CartItemResponse(
			productId, "치킨", "test.jpg", 20000, 2);
		CartItemResponse response2 = new CartItemResponse(
			productId2, "콜라", "test.jpg", 1500, 3);
		result.andExpect(status().isOk())
			.andExpect(content().json(
				objectMapper.writeValueAsString(List.of(response1, response2))));
	}
}
