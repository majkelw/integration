package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class LikePostTests extends FunctionalTests {

    private static final String USER_POST_API = "/blog/user/{userId}/post";
    private static final String USER_LIKE_POST_API = "/blog/user/{userId}/like/{postId}";
    private static final long ID_USER_AUTHOR = 1L;
    private static final long ID_USER_NEW = 2L;
    private static final long ID_USER_LIKED_POST = 3L;
    private static final long ID_POST = 1L;

    @BeforeEach
    public void setUp() {
        JSONObject jsonObj = new JSONObject().put("entry", "entry content");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString())
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_CREATED)
                .when()
                .post(USER_POST_API, ID_USER_AUTHOR);
    }


    @Test
    public void likedPostByUserConfirmedStatusShouldResponseOkStatus() {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .post(USER_LIKE_POST_API, ID_USER_LIKED_POST, ID_POST);
    }

    @Test
    public void doubleLikedDPostByUserConfirmedStatusShouldStillResponseOkStatus() {
        for (int i = 0; i < 2; i++) {
            given().accept(ContentType.JSON)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .expect()
                    .log()
                    .all()
                    .statusCode(HttpStatus.SC_OK)
                    .when()
                    .post(USER_LIKE_POST_API, ID_USER_LIKED_POST, ID_POST);
        }
    }

    @Test
    public void likedPostByNotConfirmedUserShouldResponseBadRequestStatus() {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .post(USER_LIKE_POST_API, ID_USER_NEW, ID_POST);
    }

    @Test
    public void likedPostByConfirmedAuthorShouldResponseBadRequestStatus() {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .post(USER_LIKE_POST_API, ID_USER_AUTHOR, ID_POST);
    }

}
