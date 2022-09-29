package com.manulife.codingtest.bookstore.store.repository;

import com.manulife.codingtest.bookstore.store.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Boolean existsByTitleAndAuthor_Username(String title, String username);

    Page<Book> findAll(Pageable pageable);

}
