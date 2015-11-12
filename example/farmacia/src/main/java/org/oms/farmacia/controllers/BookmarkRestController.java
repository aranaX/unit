package org.oms.farmacia.controllers;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import java.util.Collection;
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


@RestController
@RequestMapping("/bookmarks/{userId}/bookmarks")        
class BookmarkRestController {

    private final BookmarkRepository bookmarkRepository;

    private final AccountRepository accountRepository;

    @RequestMapping(value = "/{userId}/bookmarks", method = RequestMethod.POST)
    @ApiOperation(value = "Agrega nuevo bookmark")
    ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {
        this.validateUser(userId);
        return this.accountRepository
                    .findByUsername(userId)
                    .map(account -> {
                        Bookmark result = bookmarkRepository.save(new Bookmark(account,
                                            input.uri, input.description));
                        
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setLocation(ServletUriComponentsBuilder
                                .fromCurrentRequest().path("/{id}")
                                .buildAndExpand(result.getId()).toUri());
                        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
                    }).get();

	}

	@RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
	Bookmark readBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
		this.validateUser(userId);
		return this.bookmarkRepository.findOne(bookmarkId);
	}

	@RequestMapping(method = RequestMethod.GET)
	Collection<Bookmark> readBookmarks(@PathVariable String userId) {
		this.validateUser(userId);
		return this.bookmarkRepository.findByAccountUsername(userId);
	}

	@Autowired
	BookmarkRestController(BookmarkRepository bookmarkRepository,
			AccountRepository accountRepository) {
		this.bookmarkRepository = bookmarkRepository;
		this.accountRepository = accountRepository;
	}

	private void validateUser(String userId) {
		this.accountRepository.findByUsername(userId).orElseThrow(
				() -> new UserNotFoundException(userId));
	}
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("could not find user '" + userId + "'.");
    }
}
