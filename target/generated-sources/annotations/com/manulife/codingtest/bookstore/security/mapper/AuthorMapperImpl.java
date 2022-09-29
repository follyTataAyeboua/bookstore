package com.manulife.codingtest.bookstore.security.mapper;

import com.manulife.codingtest.bookstore.security.domain.Author;
import com.manulife.codingtest.bookstore.security.payload.request.AuthorDto;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-29T18:50:10-0400",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_301 (Oracle Corporation)"
)
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public AuthorDto authorToAuthorDto(Author entity) {
        if ( entity == null ) {
            return null;
        }

        AuthorDto authorDto = new AuthorDto();

        authorDto.setId( entity.getId() );
        authorDto.setUsername( entity.getUsername() );
        authorDto.setFirstname( entity.getFirstname() );
        authorDto.setLastname( entity.getLastname() );
        authorDto.setEmail( entity.getEmail() );

        return authorDto;
    }

    @Override
    public Author authorDTOtoAuthor(AuthorDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author author = new Author();

        author.setId( dto.getId() );
        author.setUsername( dto.getUsername() );
        author.setFirstname( dto.getFirstname() );
        author.setLastname( dto.getLastname() );
        author.setEmail( dto.getEmail() );

        return author;
    }
}
