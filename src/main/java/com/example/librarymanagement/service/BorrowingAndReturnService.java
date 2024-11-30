package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BorrowingDto;
import com.example.librarymanagement.exception.NotFoundResourceException;
import com.example.librarymanagement.exception.UnavailableBookCopiesException;
import com.example.librarymanagement.exception.UserBorrowedBookBeforeWithoutReturnIt;
import com.example.librarymanagement.mapper.BorrowingMapper;
import com.example.librarymanagement.model.BookModel;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.BorrowingAndReturnRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class BorrowingAndReturnService {

    private final BorrowingAndReturnRepository borrowingAndReturnRepository;
    private final BookRepository bookRepository;

    @Transactional
    public BorrowingDto borrowing(long bookId, long userId, BorrowingDto borrowingDto) {
        final var model = BorrowingMapper.INSTANCE.toBorrowingModel(borrowingDto);
        model.setBookId(bookId);
        model.setUserId(userId);


        final var copies = bookRepository.findById(model.getBookId())
                .map(BookModel::getCopies)
                .orElseThrow(NotFoundResourceException::new);
        final var reservedCopies = borrowingAndReturnRepository.findCopiesCopiesByBookId(model.getBookId());
        if (reservedCopies >= copies) {
            throw new UnavailableBookCopiesException();
        }

        var isHaveBook = borrowingAndReturnRepository.findBorrowingByBookIdAndUserId(model.getBookId(), model.getUserId());
        if (isHaveBook > 0) {
             throw new UserBorrowedBookBeforeWithoutReturnIt();
        }

        return BorrowingMapper.INSTANCE.toBorrowingDto(borrowingAndReturnRepository.create(model));
    }

    public void returns(long bookId, long userId) {
        int isDeleted = borrowingAndReturnRepository.returns(bookId, userId);

        if (isDeleted == 0) {
            throw new NotFoundResourceException();
        }
    }
}
