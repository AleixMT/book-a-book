package net.unir.missi.desarrollowebfullstack.bookabook.repository;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.unir.missi.desarrollowebfullstack.bookabook.model.sql.Author;
import net.unir.missi.desarrollowebfullstack.bookabook.model.sql.Book;
import net.unir.missi.desarrollowebfullstack.bookabook.config.search.SearchCriteria;
import net.unir.missi.desarrollowebfullstack.bookabook.config.search.SearchOperation;
import net.unir.missi.desarrollowebfullstack.bookabook.config.search.SearchStatement;
import net.unir.missi.desarrollowebfullstack.bookabook.model.sql.Client;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthorRepository {

    private final AuthorJpaRepository authorJpaRepository;


    public List<Author> findAll() {
        return authorJpaRepository.findAll();
    }
    public Author getById(Long id) {
        return authorJpaRepository.findById(id).orElse(null);
    }

    public Author save(Author author) {
        return authorJpaRepository.save(author);
    }

    public void delete(Author author) {
        authorJpaRepository.delete(author);
    }

    /*private void addSearchStatement(SearchCriteria<Client> spec, String key, String value, SearchOperation operation) {
        if (StringUtils.isNotBlank(key)) {
            spec.add(new SearchStatement(key, value, operation));
        }
    }*/

    public List<Author> search(String firstName, String lastName, LocalDate birthDate, String nationality, String email, String webSite, String biography, Book booksWritted) {
        SearchCriteria<Author> spec = new SearchCriteria<>();

        if (StringUtils.isNotBlank(firstName)) {
            spec.add(new SearchStatement("firstName", firstName, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(lastName)) {
            spec.add(new SearchStatement("lastName", lastName, SearchOperation.MATCH));
        }

        if(birthDate!=null) {
            if (StringUtils.isNotBlank(String.valueOf(birthDate))) {
                spec.add(new SearchStatement("birthDate", birthDate, SearchOperation.EQUAL));
            }
        }

        if (StringUtils.isNotBlank(nationality)) {
            spec.add(new SearchStatement("nationality", nationality, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(email)) {
            spec.add(new SearchStatement("email", email, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(webSite)) {
            spec.add(new SearchStatement("webSite", webSite, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(biography)) {
            spec.add(new SearchStatement("biography", biography, SearchOperation.MATCH));
        }

        List<Author> listAuthor = authorJpaRepository.findAll(spec);
        List<Author> filteredAuthors;

        if (booksWritted != null) {
            filteredAuthors = listAuthor.stream()
                    .filter(author -> author.getBooksWritten().stream()
                            .anyMatch(book -> Objects.equals(booksWritted.getId(), book.getId())))
                    .collect(Collectors.toList());
        }else{
            filteredAuthors = listAuthor;
        }

        return filteredAuthors;
    }



}
