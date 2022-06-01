package woowacourse.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class EncryptedPassword {

	private final String value;

	public boolean isSame(String value) {
		return this.value.equals(value);
	}
}
