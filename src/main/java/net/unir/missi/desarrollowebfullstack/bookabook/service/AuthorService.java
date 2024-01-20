package net.unir.missi.desarrollowebfullstack.bookabook.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.api.AuthorRequest;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.sql.Author;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.sql.Book;
import net.unir.missi.desarrollowebfullstack.bookabook.data.repository.AuthorRepository;
import net.unir.missi.desarrollowebfullstack.bookabook.data.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AuthorService implements IAuthorService{
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<AuthorRequest> getAllAuthors(String firstName, String lastName, LocalDate birthDate, String nationality, String email, String webSite, String biography, Long bookWritted) throws RuntimeException
    {
        try {

            if (firstName!=null
                    || lastName !=null
                    || birthDate !=null
                    || nationality!=null
                    || email!=null
                    || webSite!=null
                    || biography!=null
                    || bookWritted!=null){

                Book bookList = bookRepository.getById(bookWritted);

                return authorRepository.search(firstName,lastName, birthDate,nationality,email,webSite,biography,bookList).stream().map(AuthorRequest::new).collect(Collectors.toList());
            }else {
                return authorRepository.findAll().stream().map(AuthorRequest::new).collect(Collectors.toList());
            }
        }catch (Exception e){
            throw new RuntimeException("Database Failed;"+e.getMessage(),e);
        }
    }

    @Override
    public AuthorRequest createAuthor(AuthorRequest author) throws RuntimeException
    {
        try {
            Author authorModel = new Author(author);

            return new AuthorRequest(authorRepository.save(authorModel));
        }catch (Exception e){
            throw new RuntimeException("Database Failed;");
        }
    }

    @Override
    public AuthorRequest getAuthorById(String idAuthor) throws RuntimeException
    {
        try {
            log.error("ERROR antes de modelo");
            Author authorModel = authorRepository.getById(Long.valueOf(idAuthor));

            if(authorModel!=null)
                return new AuthorRequest(authorModel);
            else
                return null;
        }catch (Exception e){
            throw new RuntimeException("Database Failed;"+e.getMessage());
        }
    }

    @Override
    public AuthorRequest modifyAuthorData(AuthorRequest tempAuthor, AuthorRequest authorData) throws RuntimeException
    {
        try {
            Author authorToChange = authorRepository.getById(tempAuthor.getId());
            if(authorToChange!=null) {

                //Si el elemento recibido del autor es nulo, significa que no existe, y por ende no debemos modificarlo
                if (authorData.getFirstName() != null)
                    authorToChange.setFirstName(authorData.getFirstName());

                if (authorData.getLastName() != null)
                    authorToChange.setLastName(authorData.getLastName());

                if (authorData.getBirthDate() != null)
                    authorToChange.setBirthDate(authorData.getBirthDate());

                if (authorData.getEmail() != null)
                    authorToChange.setEmail(authorData.getEmail());

                if (authorData.getWebSite() != null)
                    authorToChange.setWebSite(authorData.getWebSite());

                if (authorData.getNationality() != null)
                    authorToChange.setNationality(authorData.getNationality());

                if (authorData.getBiography() != null)
                    authorToChange.setBiography(authorData.getBiography());

                authorRepository.save(authorToChange);

                return new AuthorRequest(authorToChange);
            }else
                throw new RuntimeException("Database Failed;");
        }catch (Exception e){
            throw new RuntimeException("Database Failed;");
        }

    }

    @Override
    public AuthorRequest modifyAllAuthorData(AuthorRequest prev, AuthorRequest authorData) throws RuntimeException
    {
        try {
            Author optionalAuthor = authorRepository.getById(prev.getId());
            if(optionalAuthor!=null) {
                Author authorToChange = optionalAuthor;
                //Si el elemento recibido del autor es nulo, significa que no existe, y por ende no debemos modificarlo
                    authorToChange.setFirstName(authorData.getFirstName());

                    authorToChange.setLastName(authorData.getLastName());

                    authorToChange.setBirthDate(authorData.getBirthDate());

                    authorToChange.setEmail(authorData.getEmail());

                    authorToChange.setWebSite(authorData.getWebSite());

                    authorToChange.setNationality(authorData.getNationality());

                    authorToChange.setBiography(authorData.getBiography());

                authorRepository.save(authorToChange);

                return new AuthorRequest(authorToChange);
            }else
                throw new RuntimeException("Database Failed;");
        }catch (Exception e){
            throw new RuntimeException("Database Failed;");
        }
    }

    @Override
    public AuthorRequest deleteAuthor(AuthorRequest prev) throws RuntimeException
    {
        Author author = authorRepository.getById(prev.getId());
        try {
            if(author!=null) {
                authorRepository.delete(author);
                return prev;
            }else{
                throw new RuntimeException("Database Failed;");
            }
        }catch (Exception e){
            throw new RuntimeException("Database Failed;");
        }
    }

}
