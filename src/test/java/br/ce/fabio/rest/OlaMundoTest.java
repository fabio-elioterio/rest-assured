package br.ce.fabio.rest;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class OlaMundoTest {

    @Test
    public void testOlaMundo() {

        Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
        assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        assertTrue(response.statusCode() == 200);
        assertTrue("O status code deveria ser 200",response.statusCode() == 200);
        assertEquals(200, response.statusCode());

        //restorna um validatableResponse
        ValidatableResponse validator  = response.then();
        validator.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured() {
        Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
        //then() restorna um validatableResponse
        ValidatableResponse validator  = response.then();
        validator.statusCode(200);

        get("http://restapi.wcaquino.me/ola").then().statusCode(200);

        given()
                .when()
                    .get("http://restapi.wcaquino.me/ola")
                .then()
                    .statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHamcrest() {
        assertThat("Maria", is("Maria"));
        assertThat(128, is(128));
        assertThat(128, isA(Integer.class));
        assertThat(128d, isA(Double.class));
        assertThat(128d, greaterThan(120d));
        assertThat(128d, lessThan(150d));

        List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        assertThat(impares, Matchers.<Integer>hasSize(5));
        assertThat(impares, containsInAnyOrder(3,5,7,9,1));
        assertThat(impares, hasItems(3,7,9,1));
    }

    @Test
    public void devoValidarOBody() {
        given()
                .when()
                    .get("http://restapi.wcaquino.me/ola")
                .then()
                    .statusCode(200)
                    .body(is("Ola Mundo!"))
                    .body(containsString("Mundo"))
                    .body(is(not(notNullValue())));
    }

}
