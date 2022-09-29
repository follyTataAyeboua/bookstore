package com.manulife.codingtest.bookstore.store.mapper;

import com.manulife.codingtest.bookstore.security.domain.Author;
import com.manulife.codingtest.bookstore.security.mapper.AuthorMapper;
import com.manulife.codingtest.bookstore.security.payload.request.AuthorDto;
import com.manulife.codingtest.bookstore.store.domain.Book;
import com.manulife.codingtest.bookstore.store.payload.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    AuthorMapper authorMapper = Mappers.getMapper(AuthorMapper.class);

    @Mapping(source = "author", target = "authorDto", qualifiedByName = "authorToAuthorDto")
    BookDto bookToBookDto(Book entity);

    @Mapping(source = "authorDto", target = "author", qualifiedByName = "authorDtoToAuthor")
    Book bookDtoToBook(BookDto dto);

    @Named("authorToAuthorDto")
    public default AuthorDto authorToAuthorDto(Author author) {
        return authorMapper.authorToAuthorDto(author);
    }

    @Named("authorDtoToAuthor")
    public default Author authorDtoToAuthor(AuthorDto authorDto) {
        return authorMapper.authorDTOtoAuthor(authorDto);
    }
}
