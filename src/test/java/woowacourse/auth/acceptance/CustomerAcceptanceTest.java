package woowacourse.auth.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.ori.acceptancetest.SpringBootAcceptanceTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("회원 관련 기능")
@SpringBootAcceptanceTest
public class CustomerAcceptanceTest {

	private final String email = "123@gmail.com";
	private final String password = "a1234!";
	private final String nickname = "does";

	@DisplayName("회원가입을 한다.")
	@Test
	void signUpSuccess() {
		// when
		ExtractableResponse<Response> response = RestUtils.signUp(email, password, nickname);

		String email = response.jsonPath().getString("email");
		String nickname = response.jsonPath().getString("nickname");

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(email).isEqualTo("123@gmail.com"),
			() -> assertThat(nickname).isEqualTo("does")
		);
	}

	@DisplayName("토큰이 없을 때 회원 탈퇴를 할 수 없다.")
	@Test
	void signOutNotLogin() {
		// given
		RestUtils.signUp(email, password, nickname);

		// when
		ExtractableResponse<Response> response = RestUtils.signOut("");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

	@DisplayName("회원 탈퇴를 진행한다.")
	@Test
	void signOutSuccess() {
		// given
		RestUtils.signUp(email, password, nickname);
		ExtractableResponse<Response> loginResponse = RestUtils.login(email, password);
		String token = loginResponse.jsonPath().getString("accessToken");

		// when
		ExtractableResponse<Response> response = RestUtils.signOut(token);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("회원 정보를 수정한다.")
	@Test
	void updateCustomer() {
		// given
		RestUtils.signUp(email, password, nickname);
		ExtractableResponse<Response> loginResponse = RestUtils.login(email, password);
		String token = loginResponse.jsonPath().getString("accessToken");

		// when
		ExtractableResponse<Response> response = RestUtils.update(
			token, "thor", password, "b1234!"
		);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getString("nickname")).isEqualTo("thor")
		);
	}

	@DisplayName("회원 정보을 조회한다.")
	@Test
	void findCustomer() {
		// given
		RestUtils.signUp(email, password, nickname);
		ExtractableResponse<Response> loginResponse = RestUtils.login(email, password);
		String token = loginResponse.jsonPath().getString("accessToken");

		// when
		ExtractableResponse<Response> response = RestUtils.getCustomer(token);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getString("nickname")).isEqualTo("does"),
			() -> assertThat(response.jsonPath().getString("email")).isEqualTo(email)
		);
	}
}
