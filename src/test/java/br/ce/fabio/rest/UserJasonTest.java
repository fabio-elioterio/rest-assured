package br.ce.fabio.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class UserJasonTest {

    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
                .when()
                    .get("http://restapi.wcaquino.me/users/1")
                .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", containsString("Silva"))
                    .body("age", greaterThan(18));
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");

        //path
        assertEquals(new Integer(1), response.path("id"));

        //jsonpath
        JsonPath jsonPath = new JsonPath(response.asString());
        assertEquals(1, jsonPath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);
    }

    @Test
    public void deveVerificarSegundoNivel() {

        given()
                .when()
                    .get("http://restapi.wcaquino.me/users/2")
                .then()
                    .statusCode(200)
                    .body("name", containsString("Joaquina"))
                    .body("endereco.rua", is("Rua dos bobos")); //rua é um atributo de endereço por isso se usa
                                                                        // endereco.rua
    }
}
