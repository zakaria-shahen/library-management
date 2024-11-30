package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.BorrowingDto;
import com.example.librarymanagement.model.BorrowingModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BorrowingMapper {

    BorrowingMapper INSTANCE = Mappers.getMapper(BorrowingMapper.class);

    BorrowingModel toBorrowingModel(BorrowingDto borrowingDto);

    BorrowingDto toBorrowingDto(BorrowingModel borrowingModel);
}
