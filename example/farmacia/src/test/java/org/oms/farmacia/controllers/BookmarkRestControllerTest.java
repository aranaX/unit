package org.oms.farmacia.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oms.farmacia.Application;
import org.oms.farmacia.model.Account;
import org.oms.farmacia.model.AccountRepository;
import org.oms.farmacia.model.Bookmark;
import org.oms.farmacia.model.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Josh Long
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class BookmarkRestControllerTest {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));


    private String userName = "jhoeller";

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Account account;

    private List<Bookmark> bookmarkList = new ArrayList<>();
    @Autowired
    BookmarkRestController bookmarkRestController;
    
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private WebApplicationContext webApplicationContext;
    @Mock
    private AccountRepository accountRepository;
    
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test(expected = UserNotFoundException.class)
    public void userNotFound() {        
        Bookmark bookmark = new Bookmark(account, "", "");
        bookmarkList.add(bookmark);

        when(accountRepository.findByUsername( userName )).thenReturn( null );
                
        bookmarkRestController.add( "george", bookmark);                
    }

    @Test
    public void readSingleBookmark()  {
        Bookmark bookmark = new Bookmark(account, "", "");
        Bookmark bookmarkResponse;

        String uri = "http://bookmark.com/1/" + userName;
        String description = "A description";
        Account account = new Account(userName, "");
        bookmark.setId(1L);

        bookmarkList.add(bookmark);
        when(accountRepository.findByUsername( userName )).thenReturn(account);
        when(bookmarkRepository.findOne(bookmarkList.get(0).getId())).thenReturn(bookmark);

        bookmarkResponse = bookmarkRestController.readBookmark(userName, bookmarkList.get(0).getId());

        assertEquals(bookmarkResponse.getId(), this.bookmarkList.get(0).getId());
        assertEquals(bookmarkResponse.getUri(), uri);
        assertEquals(bookmarkResponse.getDescription(), description);
    }

    @Test
    public void readBookmarks(){
        List<Bookmark> bookmarkListResponse;
        Bookmark bookmark, bookmark2;


        String uri = "http://bookmark.com/1/" + userName;
        String uri2 = "http://bookmark.com/2/" + userName;
        String description = "A description";

        this.bookmarkList = new ArrayList<>();

        bookmark = new Bookmark(account, "http://bookmark.com/1/" + userName, "A description");
        bookmark.setId(1L);
        bookmark2 = new Bookmark(account, "http://bookmark.com/2/" + userName, "A description");
        bookmark2.setId(2L);

        bookmarkList.add(bookmark);
        bookmarkList.add(bookmark2);

        when(accountRepository.findByUsername(userName)).thenReturn(account);
        when(bookmarkRepository.findByAccountUsername(userName)).thenReturn(bookmarkList);

        bookmarkListResponse = (List<Bookmark>) bookmarkRestController.readBookmarks(userName);

        assertEquals(bookmarkListResponse.size(), 2);
        assertEquals(bookmarkListResponse.get(0).getId(), this.bookmarkList.get(0).getId());
        assertEquals(bookmarkListResponse.get(0).getUri(), uri );
        assertEquals(bookmarkListResponse.get(0).getDescription(), description);
        assertEquals(bookmarkListResponse.get(1).getId(), this.bookmarkList.get(1).getId());
        assertEquals(bookmarkListResponse.get(1).getUri(), uri2 );
        assertEquals(bookmarkListResponse.get(1).getDescription(), description);

    }

    @Test
    public void createBookmark() throws Exception {
        String uri = "http://spring.io";
        String description = "a bookmark to the best resource for Spring news and information";
        Bookmark bookmarkResponse, bookmarkSent;

        bookmarkSent = new Bookmark(this.account, uri, description );
        bookmarkResponse = new Bookmark(this.account, uri, description );
        bookmarkResponse.setId(1L);

        when(accountRepository.findByUsername(userName)).thenReturn(account);
        when(bookmarkRepository.save(bookmarkSent)).thenReturn(bookmarkResponse);

        assertEquals(bookmarkRestController.add(userName, bookmarkSent).getStatusCode(), HttpStatus.CREATED);

    }

}