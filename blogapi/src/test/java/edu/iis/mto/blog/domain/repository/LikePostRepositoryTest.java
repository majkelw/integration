package edu.iis.mto.blog.domain.repository;


import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

@DataJpaTest
class LikePostRepositoryTest {

    @Autowired
    private LikePostRepository likePostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;

    private User authorPost;
    private User userWhoLikedPost;
    private BlogPost blogPost;
    private LikePost savedLikePost;


    @BeforeEach
    public void setUp() {
        authorPost = new User();
        authorPost.setFirstName("Jan");
        authorPost.setLastName("Kowalski");
        authorPost.setEmail("john@domain.com");
        authorPost.setAccountStatus(AccountStatus.CONFIRMED);

        userWhoLikedPost = new User();
        userWhoLikedPost.setFirstName("Michal");
        userWhoLikedPost.setLastName("Nowak");
        userWhoLikedPost.setEmail("mike@domain.com");
        userWhoLikedPost.setAccountStatus(AccountStatus.CONFIRMED);

        userRepository.save(authorPost);
        userRepository.save(userWhoLikedPost);

        blogPost = new BlogPost();
        blogPost.setUser(authorPost);
        blogPost.setEntry("entry");

        LikePost likePost = new LikePost();
        likePost.setPost(blogPost);
        likePost.setUser(userWhoLikedPost);

        List<LikePost> likes = new ArrayList<LikePost>() {
            {
                add(likePost);
            }
        };
        blogPost.setLikes(likes);
        blogPostRepository.save(blogPost);
        savedLikePost = likePostRepository.save(likePost);
    }

    @Test
    public void shouldFindLikePost() {
        Optional<LikePost> likePostOptional = likePostRepository.findByUserAndPost(userWhoLikedPost, blogPost);
        assertTrue(likePostOptional.isPresent());
        assertEquals(savedLikePost, likePostOptional.get());
        assertEquals(userWhoLikedPost, likePostOptional.get().getUser());
    }

    @Test
    public void shouldNotFindLikePostIfArgumentsAreNull() {
        Optional<LikePost> likePostOptional = likePostRepository.findByUserAndPost(null, null);
        assertFalse(likePostOptional.isPresent());
    }


    @Test
    public void shouldNotFindLikePostIfUserDidNotLikeIt() {
        Optional<LikePost> likePostOptional = likePostRepository.findByUserAndPost(authorPost, blogPost);
        assertFalse(likePostOptional.isPresent());
    }

    @Test
    public void shouldStoreNewLikePost() {
        User userDummy = new User();
        userDummy.setEmail("user@gmail.com");
        userDummy.setAccountStatus(AccountStatus.CONFIRMED);
        userDummy.setFirstName("Grzegorz");
        userDummy.setLastName("Kowal");
        userRepository.save(userDummy);

        LikePost likePost = new LikePost();
        likePost.setPost(blogPost);
        likePost.setUser(userDummy);
        LikePost persistedLikePost = likePostRepository.save(likePost);
        assertThat(persistedLikePost.getId(), notNullValue());

    }

    @Test
    public void shouldFindThreeUsersWhoLikedPost() {
        String[] names = {"Kasia", "Barbara", "Kamil"};
        String[] surnames = {"Kwiatkowska", "Kowalska", "Nowakowski"};
        String[] emails = {"kkwiatkowska@gmail.com", "bkowalska@vp.pl", "kamilkowalski@gmail.com"};
        List<User> users = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            User user = new User();
            user.setFirstName(names[i]);
            user.setLastName(surnames[i]);
            user.setEmail(emails[i]);
            user.setAccountStatus(AccountStatus.CONFIRMED);
            users.add(user);
            userRepository.save(user);
        }

        BlogPost blogPostDummy = new BlogPost();
        blogPostDummy.setUser(users.get(0));
        blogPostDummy.setEntry("text");
        blogPostRepository.save(blogPostDummy);

        List<LikePost> likePosts = new ArrayList<>();
        for (User user : users) {
            LikePost likePostDummy = new LikePost();
            likePostDummy.setUser(user);
            likePostDummy.setPost(blogPostDummy);
            likePostRepository.save(likePostDummy);
            likePosts.add(likePostDummy);
        }
        blogPostDummy.setLikes(likePosts);
        for (int i = 0; i < likePosts.size(); i++) {
            Optional<LikePost> likePostOptional = likePostRepository.findByUserAndPost(users.get(i), blogPostDummy);
            assertTrue(likePostOptional.isPresent());
            assertEquals(likePostOptional.get().getId(), likePosts.get(i).getId());
        }
    }

}