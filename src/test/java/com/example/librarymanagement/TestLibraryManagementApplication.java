package com.example.librarymanagement;

import org.springframework.boot.SpringApplication;

public class TestLibraryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(LibraryManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
