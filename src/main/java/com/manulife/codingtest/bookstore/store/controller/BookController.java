package com.manulife.codingtest.bookstore.store.controller;

import com.manulife.codingtest.bookstore.config.payload.MessageResponse;
import com.manulife.codingtest.bookstore.config.payload.ResponseType;
import com.manulife.codingtest.bookstore.config.web.Properties;
import com.manulife.codingtest.bookstore.security.service.AuthorService;
import com.manulife.codingtest.bookstore.store.domain.Book;
import com.manulife.codingtest.bookstore.store.payload.BookDto;
import com.manulife.codingtest.bookstore.store.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    AuthorService authorService;

    @Autowired
    Properties properties;

    @Operation(summary = "Book count")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/count")
    public ResponseEntity<?> getBookCount() {
        try {
            return ResponseEntity.ok(bookService.getCount());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "Book list")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/books")
    public ResponseEntity<?> getAllByPage(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "size", defaultValue = "5") int size) {
        try {
            List<BookDto> books = bookService.getAll(page, size);

            return ResponseEntity.ok(books);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "Book list by criteria")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/books/criteria")
    public ResponseEntity<?> getAllByCriteria(@RequestParam(value = "title", defaultValue = "") String title,
                                              @RequestParam(value = "author", defaultValue = "") String author,
                                              @RequestParam(value = "pricemin", defaultValue = "0") BigDecimal priceMin,
                                              @RequestParam(value = "pricemax", defaultValue = "0") BigDecimal priceMax) {
        try {

            Predicate<Book> titlePredicate = (x) -> StringUtils.contains(x.getTitle(), title);
            Predicate<Book> priceMinPredicate = (x) -> priceMin.compareTo(x.getPrice()) <= 0;
            Predicate<Book> priceMaxPredicate = (x) -> priceMax.compareTo(x.getPrice()) >= 0;

            List<Predicate<Book>> predicates = Arrays.asList(titlePredicate, priceMinPredicate, priceMaxPredicate);
            List<BookDto> books = bookService.getAllByTitleCriteria(predicates);

            return ResponseEntity.ok(books);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "Create Book")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookDto bookDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            String headerAuth = properties.getAuthorizationHeader(request);
            String requestUsername = properties.parseUsernameHeader(request);
            String author = authorService.getUsernameFromHeader(headerAuth, requestUsername);
            BookDto book = bookService.createBook(bookDto, author);

            return ResponseEntity.ok(book);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "Read Book by ID")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<?> readBook(@PathVariable("id") Long id) {
        try {
            BookDto book = bookService.readBook(id);
            return ResponseEntity.ok(book);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "Update Book")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update")
    public ResponseEntity<?> updateBook(@Valid @RequestBody BookDto bookDto) {
        try {
            return ResponseEntity.ok(bookService.updateBook(bookDto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

    @Operation(summary = "Delete Book by ID")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            String headerAuth = properties.getAuthorizationHeader(request);
            String requestUsername = properties.parseUsernameHeader(request);
            String username = authorService.getUsernameFromHeader(headerAuth, requestUsername);
            bookService.deleteBook(id, username);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ResponseType.ERROR, ex.getMessage()));
        }
    }

}
