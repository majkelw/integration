package edu.iis.mto.blog.rest.test;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

class CreateUserTests extends FunctionalTests {

    private static final String USER_API = "/blog/user";

    @Test
    public void createUserWithProperDataReturnsCreatedStatus() {
        JSONObject jsonObj = new JSONObject().put("email", "tracy1@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString())
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_CREATED)
                .when()
                .post(USER_API);
    }

    @Test
    public void createUserWithAlreadyExistingEmailReturnsConflictStatus() {
        JSONObject jsonObj = new JSONObject().put("email", "tracy2@domain.com");
        int statusCode = HttpStatus.SC_CREATED;
        for (int i = 0; i < 2; i++) {
            if (i > 0)
                statusCode = HttpStatus.SC_CONFLICT;

            given().accept(ContentType.JSON)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(jsonObj.toString())
                    .expect()
                    .log()
                    .all()
                    .statusCode(statusCode)
                    .when()
                    .post(USER_API);
        }
    }

}
