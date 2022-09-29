package com.manulife.codingtest.bookstore.store.service;

import com.manulife.codingtest.bookstore.security.domain.Author;
import com.manulife.codingtest.bookstore.security.service.AuthorService;
import com.manulife.codingtest.bookstore.store.domain.Book;
import com.manulife.codingtest.bookstore.store.payload.BookDto;
import com.manulife.codingtest.bookstore.store.repository.BookRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class BookServiceTest {

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    AuthorService authorService;

    private BookService bookService = new BookService();

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {

        Author author = new Author();
        author.setId(0L);
        author.setUsername("Author");
        author.setEmail("email1");

        Author author1 = new Author();
        author1.setId(1L);
        author1.setUsername("Author1");

        Book book = new Book();
        book.setId(0L);
        book.setTitle("Book");
        book.setAuthor(author);

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book1");
        book1.setAuthor(author1);


        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book12");
        book2.setAuthor(author1);

        List<Book> allBooks = Arrays.asList(book, book1, book2);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.findAll()).thenReturn(allBooks);

        Mockito.when(authorService.getAuthor(Mockito.any())).thenReturn(author);
        Mockito.when(bookRepository.existsByTitleAndAuthor_Username(book.getTitle(), author.getUsername())).thenReturn(true);

        BookDto dto = new BookDto();
        dto.setTitle("Dto");
        dto.setDescription("Dto");
        dto.setPrice(BigDecimal.ONE);
        Mockito.when(bookRepository.existsByTitleAndAuthor_Username(dto.getTitle(), author.getUsername())).thenReturn(false);

        Book dtoSaved = new Book();
        dtoSaved.setTitle("Dto");
        dtoSaved.setDescription("Dto");
        dtoSaved.setPrice(BigDecimal.ONE);
        dtoSaved.setId(4L);
        dtoSaved.setAuthor(author);
        Mockito.when(bookRepository.save(Mockito.any())).thenReturn(dtoSaved);


        bookService.bookRepository = bookRepository;
        bookService.authorService = authorService;

    }


    @Test
    public void whenGetAllWithDefault_thenReturnAllBooks() {
        List<BookDto> books = bookService.getAll(0, 0);
        assertThat(books.size()).isEqualTo(3);
    }

    @Test
    public void whenCreateBook_thenReturnBook() {
        BookDto dto = new BookDto();
        dto.setTitle("Dto");
        BookDto bookDto = bookService.createBook(dto, "Author");

        assertThat(bookDto.getPrice()).isEqualTo(BigDecimal.ONE);
        assertThat(bookDto.getTitle()).isEqualTo("Dto");
        assertThat(bookDto.getDescription()).isEqualTo("Dto");
        assertThat(bookDto.getAuthorDto().getUsername()).isEqualTo("Author");
        assertThat(bookDto.getAuthorDto().getEmail()).isEqualTo("email1");
    }

    @Test(expected = RuntimeException.class)
    public void whenCreateExistingBook_thenThrowException() {
        BookDto dto = new BookDto();
        dto.setTitle("Book");
        bookService.createBook(dto, "Author");
    }

    @Test
    public void whenReadBookWithId_thenReturnBook() {
        BookDto bookDto = bookService.readBook(0L);

        assertThat(bookDto.getTitle()).isEqualTo("Book");
        assertThat(bookDto.getAuthorDto().getUsername()).isEqualTo("Author");
        assertThat(bookDto.getAuthorDto().getEmail()).isEqualTo("email1");
    }

    @Test(expected = RuntimeException.class)
    public void whenReadBookWithMissingId_thenThrowException() {
        bookService.readBook(1L);
    }


    @Test
    public void whenUpdateBook_thenReturnBook() {
        BookDto dto = new BookDto();
        dto.setId(0L);
        dto.setTitle("NewBookTitle");
        dto.setDescription("NewBookDescription");
        BookDto bookDto = bookService.updateBook(dto);

        assertThat(bookDto.getTitle()).isEqualTo("NewBookTitle");
        assertThat(bookDto.getDescription()).isEqualTo("NewBookDescription");
        assertThat(bookDto.getAuthorDto().getUsername()).isEqualTo("Author");
        assertThat(bookDto.getAuthorDto().getEmail()).isEqualTo("email1");
    }

    @Test(expected = RuntimeException.class)
    public void whenUpdateBookWithMissingId_thenThrowException() {
        BookDto dto = new BookDto();
        dto.setId(1L);
        bookService.updateBook(dto);
    }

    @Test(expected = RuntimeException.class)
    public void whenDeleteBookWithMissingId_thenThrowException() {
        bookService.deleteBook(1L, "");
    }

    @Test(expected = RuntimeException.class)
    public void whenDeleteBookWithWithAuthor_thenThrowException() {
        bookService.deleteBook(0L, "");
    }

    @Test(expected = Test.None.class)
    public void whenDeleteBook_thenNoException() {
        bookService.deleteBook(0L, "Author");
    }
}
