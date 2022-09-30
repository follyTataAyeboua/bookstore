package com.manulife.codingtest.bookstore.store.service;

import com.google.common.base.Preconditions;
import com.manulife.codingtest.bookstore.security.domain.Author;
import com.manulife.codingtest.bookstore.security.service.AuthorService;
import com.manulife.codingtest.bookstore.store.domain.Book;
import com.manulife.codingtest.bookstore.store.mapper.BookMapper;
import com.manulife.codingtest.bookstore.store.payload.BookDto;
import com.manulife.codingtest.bookstore.store.repository.BookRepository;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorService authorService;

    @PersistenceContext
    private EntityManager entityManager;

    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    public long getCount() {
        logger.info("Processing getCount");
        return bookRepository.count();
    }

    public List<BookDto> getAll(int page, int size) {
        Preconditions.checkArgument(page >= 0, "Page cannot be less than 0");
        Preconditions.checkArgument(size >= 0, "Size cannot be less than 0");
        logger.info("Processing getAll page:{} - size{}", page, size);
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
        logger.info("Processing getAllByTitleCriteria");
        List<Book> books = entityManager.createQuery("select b from Book b", Book.class).getResultList();
        Stream<Book> stream = books.stream();
        for (Predicate<Book> predicate : predicates) {
            stream = stream.filter(predicate);
        }
        return stream.map(bookMapper::bookToBookDto).collect(Collectors.toList());


    }

    public BookDto createBook(BookDto bookDto, String username) {
        Preconditions.checkNotNull(bookDto, "Book cannot be null");
        Preconditions.checkArgument(StringUtils.isNotEmpty(username), "Username cannot be null or empty");
        logger.info("Processing createBook for title:{} by {}", bookDto.getTitle(), username);
        if (!bookRepository.existsByTitleAndAuthor_Username(bookDto.getTitle(), username)) {
            Book book = bookMapper.bookDtoToBook(bookDto);
            Author author = authorService.getAuthor(username);
            book.setAuthor(author);
            book = bookRepository.save(book);
            return bookMapper.bookToBookDto(book);
        } else {
            logger.error("Book with title:{} and author {} already exists", bookDto.getTitle(), username);
            throw new RuntimeException("Book with title:" + bookDto.getTitle() + " and author " + username + " already exists");
        }
    }

    public BookDto readBook(Long id) {
        Preconditions.checkNotNull(id, "Id cannot be null");
        logger.info("Processing readBook by id:{}", id);
        return bookRepository.findById(id).map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new RuntimeException("Book not found with provided id:" + id));
    }

    public BookDto updateBook(BookDto bookDto) {
        Preconditions.checkNotNull(bookDto, "Book cannot be null");
        Preconditions.checkNotNull(bookDto.getId(), "Book ID cannot be null");
        logger.info("Processing updateBook - id:{}, title:{}, description:{}, price:{}",
                bookDto.getId(), bookDto.getTitle(), bookDto.getDescription(), bookDto.getPrice());

        Book book = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new RuntimeException("Book not found - id:" + bookDto.getId()));
        book.setDescription(bookDto.getDescription());
        book.setTitle(bookDto.getTitle());
        book.setPrice(bookDto.getPrice());
        bookRepository.save(book);
        return bookMapper.bookToBookDto(book);
    }

    public void deleteBook(Long id, String username) {
        Preconditions.checkNotNull(id, "Book ID cannot be null");
        Preconditions.checkArgument(StringUtils.isNotEmpty(username), "Username cannot be null or empty");

        logger.info("Processing deleteBook - id:{}, username:{}", id, username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with provided id:" + id));
        if (!StringUtils.equalsIgnoreCase(book.getAuthor().getUsername(), username)) {
            logger.error("Unpublished book {} failed - Cannot unpublished a book from another author", book.getTitle());
            throw new RuntimeException("Unpublished book failed - Cannot unpublished a book from another author");
        }
        bookRepository.delete(book);
    }
}
