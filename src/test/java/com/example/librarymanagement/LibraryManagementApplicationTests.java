package com.example.librarymanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class LibraryManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
