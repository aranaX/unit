package org.oms.farmacia;

import com.mangofactory.swagger.configuration.DocumentationConfig;
import java.util.Arrays;
import org.oms.farmacia.model.Account;
import org.oms.farmacia.model.AccountRepository;
import org.oms.farmacia.model.Bookmark;
import org.oms.farmacia.model.BookmarkRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//TODO http://spring.io/guides/tutorials/bookmarks/
@SpringBootApplication
public class Application {

        @Bean
        CommandLineRunner init(AccountRepository accountRepository,
                        BookmarkRepository bookmarkRepository) {
            return (evt) -> Arrays.asList(
                                "jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
                                .forEach(
                                    a -> {
                                        Account account = accountRepository.save(new Account(a,
                                                "password"));
                                        bookmarkRepository.save(new Bookmark(account,
                                                "http://bookmark.com/1/" + a, "A description"));
                                        bookmarkRepository.save(new Bookmark(account,
                                                "http://bookmark.com/2/" + a, "A description"));
                                    });
        }
       
    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }

}
