package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:test",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
class ShareItAppTest {

	@Test
	void mainContextTest() {
		// Тест проверяет, что контекст Spring успешно загружается
		assertDoesNotThrow(() -> ShareItApp.main(new String[]{}));
	}
}