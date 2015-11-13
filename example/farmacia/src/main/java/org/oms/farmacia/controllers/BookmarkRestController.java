package org.oms.farmacia.controllers;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.oms.farmacia.model.Account;
import org.oms.farmacia.model.AccountRepository;
import org.oms.farmacia.model.Bookmark;
import org.oms.farmacia.model.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;

@Api(value = BookmarkRestController.SERVICE_ID, description = BookmarkRestController.SERVICE_DESCRIPTION)
@RestController
@RequestMapping(BookmarkRestController.SERVICE_RM)
class BookmarkRestController {

    public static final String SERVICE_ID = "BOOKMARK";
    public static final String SERVICE_DESCRIPTION = "BOOKMARKS API - v1";
    public static final String SERVICE_RM = "/v1/bookmark";


    private final BookmarkRepository bookmarkRepository;
    private final AccountRepository accountRepository;

    @Autowired
    BookmarkRestController(BookmarkRepository bookmarkRepository,
                           AccountRepository accountRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.accountRepository = accountRepository;
    }

    @ApiOperation("Add bookmark to user")
    @RequestMapping(value = "/{userId}/bookmarks", method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {
        this.validateUser(userId);

        Account account = this.accountRepository.findByUsername(userId);

        Bookmark result = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}/bookmarks/{bookmarkId}", method = RequestMethod.GET)
    Bookmark readBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
        this.validateUser(userId);
        return this.bookmarkRepository.findOne(bookmarkId);
    }

    @RequestMapping(value="/{userId}/bookmarks", method = RequestMethod.GET)
    Collection<Bookmark> readBookmarks(@PathVariable String userId) {
        this.validateUser(userId);
        return this.bookmarkRepository.findByAccountUsername(userId);
    }


    private void validateUser(String userId) {
        if( this.accountRepository.findByUsername(userId) == null )
                throw new UserNotFoundException(userId);
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("could not find user '" + userId + "'.");
    }
}
