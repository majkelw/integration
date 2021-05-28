package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class CreatePostTests extends FunctionalTests {

    private static final String USER_POST_API = "/blog/user/{userId}/post";

    @Test
    public void createPostWithConfirmedStatusShouldResponseWithCreatedStatus() {
        long idUserConfirmed = 1L;

        JSONObject jsonObj = new JSONObject().put("entry", "entry content");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString())
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_CREATED)
                .when()
                .post(USER_POST_API, idUserConfirmed);
    }

    @Test
    public void createPostWithConfirmedStatusShouldResponseWithBadRequestStatus() {
        long isUserNotConfirmed = 2L;
        JSONObject jsonObj = new JSONObject().put("entry", "entry content");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString())
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .post(USER_POST_API, isUserNotConfirmed);
    }

}