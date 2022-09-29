package com.manulife.codingtest.bookstore.security.mapper;

import com.manulife.codingtest.bookstore.security.domain.Author;
import com.manulife.codingtest.bookstore.security.payload.request.AuthorDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    AuthorDto authorToAuthorDto(Author entity);

    Author authorDTOtoAuthor(AuthorDto dto);
}
