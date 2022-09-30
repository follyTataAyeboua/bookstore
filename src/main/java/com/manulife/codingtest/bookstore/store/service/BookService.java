package com.manulife.codingtest.bookstore.store.service;

import com.manulife.codingtest.bookstore.security.domain.Author;
import com.manulife.codingtest.bookstore.security.service.AuthorService;
import com.manulife.codingtest.bookstore.store.domain.Book;
import com.manulife.codingtest.bookstore.store.mapper.BookMapper;
import com.manulife.codingtest.bookstore.store.payload.BookDto;
import com.manulife.codingtest.bookstore.store.repository.BookRepository;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorService authorService;

    @PersistenceContext
    private EntityManager entityManager;

    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    public long getCount() {
        return bookRepository.count();
    }

    public List<BookDto> getAll(int page, int size) {
        if (page == 0 && size == 0) {
            return bookRepository.findAll().stream().map(bookMapper::bookToBookDto).collect(Collectors.toList());
        } else {
            page = Math.max(page, 1);
            int min = (page - 1) * size;
            int max = page * size;
            Pageable sortedByTitle = PageRequest.of(min, max, Sort.by("title"));
            return bookRepository.findAll(sortedByTitle).stream().map(bookMapper::bookToBookDto).collect(Collectors.toList());
        }
    }

    public List<BookDto> getAllByTitleCriteria(List<Predicate<Book>> predicates) {
        List<Book> books = entityManager.createQuery("select b from Book b", Book.class).getResultList();
        Stream<Book> stream = books.stream();
        for (Predicate<Book> predicate : predicates) {
            stream = stream.filter(predicate);
        }
        return stream.map(bookMapper::bookToBookDto).collect(Collectors.toList());


    }

    public BookDto createBook(BookDto bookDto, String username) {
        if (!bookRepository.existsByTitleAndAuthor_Username(bookDto.getTitle(), username)) {
            Book book = bookMapper.bookDtoToBook(bookDto);
            Author author = authorService.getAuthor(username);
            book.setAuthor(author);
            book = bookRepository.save(book);
            return bookMapper.bookToBookDto(book);
        } else {
            throw new RuntimeException("Book with title:" + bookDto.getTitle() + " and author " + username + " already exists");
        }
    }

    public BookDto readBook(Long id) {
        return bookRepository.findById(id).map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new RuntimeException("Book not found with provided id:" + id));
    }

    public BookDto updateBook(BookDto bookDto) {
        Book book = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new RuntimeException("Book not found - id:" + bookDto.getId()));
        book.setDescription(bookDto.getDescription());
        book.setTitle(bookDto.getTitle());
        book.setPrice(bookDto.getPrice());
        bookRepository.save(book);
        return bookMapper.bookToBookDto(book);
    }

    public void deleteBook(Long id, String username) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with provided id:" + id));
        if (!StringUtils.equalsIgnoreCase(book.getAuthor().getUsername(), username)) {
            throw new RuntimeException("Unpublished book failed - Cannot unpublished a book from another author");
        }
        bookRepository.delete(book);
    }
}
