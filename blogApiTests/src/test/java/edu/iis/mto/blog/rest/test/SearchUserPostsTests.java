package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;

public class SearchUserPostsTests extends FunctionalTests {

    private static final String USER_POST_API = "/blog/user/{userId}/post";
    private static final String USER_LIKE_POST_API = "/blog/user/{userId}/like/{postId}";
    private static final String SEARCH_USER_POST_API = "/blog/user/{userId}/post";
    private static final long ID_USER_AUTHOR = 3L;
    private static final long ID_USER_AUTHOR_REMOVED = 4L;

    private long createUserPost(long authorId) {

        JSONObject jsonObj = new JSONObject().put("entry", "example entry content");
        Response response = given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString())
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_CREATED)
                .when()
                .post(USER_POST_API, authorId);

        return response.jsonPath().getLong("id");
    }

    private void createLikePost(long idUserLikedPost, long idPost) {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .post(USER_LIKE_POST_API, idUserLikedPost, idPost);
    }

    @Test
    public void everySinglePostCreatedByUserShouldBeLikedByTwoOtherUsers() {
        for (int i = 0; i < 10; i++) {
            long id = createUserPost(ID_USER_AUTHOR);
            createLikePost(1L, id);
            createLikePost(5L, id);
        }

        Response response = given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(SEARCH_USER_POST_API, ID_USER_AUTHOR);

        long i = 0;
        while (true) {
            String str = "[" + i + "].likesCount";
            Integer likesCount = response.body().path(str);
            if (likesCount == null)
                break;
            i++;
            assertEquals(2, likesCount);
        }

    }

    @Test
    public void everySinglePostCreatedByUserShouldNotBeLikedByOtherUsers() {
        for (int i = 0; i < 10; i++)
            createUserPost(5L);

        Response response = given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(SEARCH_USER_POST_API, 5L);

        long i = 0;
        while (true) {
            String str = "[" + i + "].likesCount";
            Integer likesCount = response.body().path(str);
            if (likesCount == null)
                break;
            i++;
            assertEquals(0, likesCount);
        }

    }

    @Test
    public void searchPostsIfAuthorDoesNotExistShouldResponseWithBadRequest() {
        long userDoesNotExistId = 1000L;
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .get(SEARCH_USER_POST_API, userDoesNotExistId);
    }


    @Test
    public void searchPostsWhenAuthorWasRemovedShouldResponseWithBadRequest() {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .get(SEARCH_USER_POST_API, ID_USER_AUTHOR_REMOVED);
    }

}
