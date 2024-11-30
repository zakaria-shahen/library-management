package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.model.BookModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDto toBookDto(BookModel book);

    BookModel toBookModel(BookDto bookDto);

    List<BookDto> toBookDto(List<BookModel> bookList);

}
