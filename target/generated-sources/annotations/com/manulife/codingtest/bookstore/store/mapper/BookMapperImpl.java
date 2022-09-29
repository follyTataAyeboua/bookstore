package com.manulife.codingtest.bookstore.store.mapper;

import com.manulife.codingtest.bookstore.store.domain.Book;
import com.manulife.codingtest.bookstore.store.payload.BookDto;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-29T18:50:11-0400",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_301 (Oracle Corporation)"
)
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDto bookToBookDto(Book entity) {
        if ( entity == null ) {
            return null;
        }

        BookDto bookDto = new BookDto();

        bookDto.setAuthorDto( authorToAuthorDto( entity.getAuthor() ) );
        bookDto.setId( entity.getId() );
        bookDto.setTitle( entity.getTitle() );
        bookDto.setDescription( entity.getDescription() );
        bookDto.setPrice( entity.getPrice() );

        return bookDto;
    }

    @Override
    public Book bookDtoToBook(BookDto dto) {
        if ( dto == null ) {
            return null;
        }

        Book book = new Book();

        book.setAuthor( authorDtoToAuthor( dto.getAuthorDto() ) );
        book.setId( dto.getId() );
        book.setTitle( dto.getTitle() );
        book.setDescription( dto.getDescription() );
        book.setPrice( dto.getPrice() );

        return book;
    }
}
