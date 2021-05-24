package edu.iis.mto.blog.domain.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    private User user;
    private User user2;
    private static final String INVALID_VALUE = "<//\\>";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("john@domain.com");
        user.setAccountStatus(AccountStatus.NEW);

        user2 = new User();
        user2.setFirstName("Michal");
        user2.setLastName("Nowak");
        user2.setEmail("mike@domain.com");
        user2.setAccountStatus(AccountStatus.CONFIRMED);
    }


    @Test
    void shouldFindNoUsersIfRepositoryIsEmpty() {
        List<User> users = repository.findAll();
        assertThat(users, hasSize(0));
    }


    @Test
    void shouldFindOneUsersIfRepositoryContainsOneUserEntity() {
        User persistedUser = entityManager.persist(user);
        List<User> users = repository.findAll();
        assertThat(users, hasSize(1));
        assertThat(users.get(0)
                        .getEmail(),
                equalTo(persistedUser.getEmail()));
    }

    @Test
    void shouldStoreANewUser() {
        User persistedUser = repository.save(user);
        assertThat(persistedUser.getId(), notNullValue());
    }

    @Test
    void shouldFindOneUserWhenFirstNameIsRight() {
        User persistedUser = repository.save(user);
        System.out.println("size " + repository.findAll().size());
        List<User> users = repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("an", INVALID_VALUE, INVALID_VALUE);
        assertThat(users, hasSize(1));
        assertEquals(persistedUser, users.get(0));
    }

    @Test
    void shouldFindOneUserIfRepositoryContainsLastName() {
        User persistedUser = repository.save(user);
        System.out.println("size " + repository.findAll().size());
        List<User> users = repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(INVALID_VALUE, "ski", INVALID_VALUE);
        assertThat(users, hasSize(1));
        assertEquals(persistedUser, users.get(0));
    }

    @Test
    void shouldFindTwoUsersIfRepositoryContainsEmailAddress() {
        User persistedUser = repository.save(user);
        User persistedUser2 = repository.save(user2);
        List<User> users = repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(INVALID_VALUE, INVALID_VALUE, ".com");
        assertThat(users, hasSize(2));
        assertEquals(persistedUser, users.get(0));
        assertEquals(persistedUser2, users.get(1));
    }

    @Test
    void shouldNotFindAnyUsersWhenAllValuesAreWrong() {
        repository.save(user);
        List<User> users = repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(INVALID_VALUE, INVALID_VALUE, INVALID_VALUE);
        assertThat(users, hasSize(0));
    }


}
