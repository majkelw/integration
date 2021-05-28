package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class SearchUserTests extends FunctionalTests {

    private static final String FIND_USER_POST = "/blog/user/find";

    private Response getResponse(String searchString) {
        return given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .param("searchString", searchString)
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(FIND_USER_POST);
    }

    @Test
    public void shouldSearchUserByLastName() {
        String searchLastName = "Steward";
        Response response = getResponse(searchLastName);
        assertEquals(1, response.jsonPath().getList("$").size());
        assertEquals(searchLastName, response.body().path("[0].lastName"));
    }

    @Test
    public void shouldSearchUserByEmail() {
        String searchEmail = "adam@domain.com";
        Response response = getResponse(searchEmail);
        assertEquals(1, response.jsonPath().getList("$").size());
        assertEquals(searchEmail, response.body().path("[0].email"));
    }

    @Test
    public void shouldSearchTwoUsersByFirstName() {
        String searchFirstName = "John";
        Response response = getResponse(searchFirstName);
        int size = response.jsonPath().getList("$").size();
        assertEquals(2, size);
        for (int i = 0; i < size; i++) {
            String str = "[" + i + "].firstName";
            assertEquals(searchFirstName, response.body().path(str));
        }
    }

    @Test
    public void shouldNotSearchUserIfDoesNotExist() {
        assertEquals(0, getResponse("Jacob").jsonPath().getList("$").size());
    }

    @Test
    public void shouldNotSearchUserIfUserWasRemoved() {
        assertEquals(0, getResponse("paul@domain.com").jsonPath().getList("$").size());
    }
}
