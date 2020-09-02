package br.ce.fabio.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserJasonTest {

    public static final String URL_USER_1 = "http://restapi.wcaquino.me/users/1";
    public static final String URL_USER_2 = "http://restapi.wcaquino.me/users/2";
    public static final String URL_USER_3 = "http://restapi.wcaquino.me/users/3";
    public static final String URL_USER_4_ERROR = "http://restapi.wcaquino.me/users/4";
    public static final String BASE_URL_USER = "http://restapi.wcaquino.me/users";

    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
                .when()
                    .get(URL_USER_1)
                .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", containsString("Silva"))
                    .body("age", greaterThan(18));
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {
        Response response = RestAssured.request(Method.GET, URL_USER_1);

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
                    .get(URL_USER_2)
                .then()
                    .statusCode(200)
                    .body("name", containsString("Joaquina"))
                    .body("endereco.rua", is("Rua dos bobos")); //rua é um atributo de endereço por isso se usa
                                                                        // endereco.rua
    }

    @Test
    public void deveVerificarLista() {
        given()
                .when()
                    .get(URL_USER_3)
                .then()
                    .statusCode(200)
                    .body("name", containsString("Ana"))
                    .body("filhos", hasSize(2))
                    .body("filhos[0].name", is("Zezinho"))//para testar os atibutos da lista acessar a posição.
                    .body("filhos[1].name", is("Luizinho"))
                    .body("filhos.name", hasItem("Zezinho"))
                    .body("filhos.name", hasItems("Zezinho", "Luizinho"))
        ;
    }

    @Test
    public void deveRetornarErrorUsuarioInexistente() {
        given()
                .when()
                    .get(URL_USER_4_ERROR)
                .then()
                    .statusCode(404)
                    .body("error", is("Usuário inexistente"))
        ;

    }

    @Test
    public void deveVerificarListaRaiz() {
        given()
                .when()
                    .get(BASE_URL_USER)
                .then()
                    .statusCode(200)
                    .body("$", hasSize(3)) // '$' é uma referência á raiz da lista, do json.
                    .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
                    .body("age[1]", is(25))
                    .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho"))) //Bucas uma lista dentro de outra lista.
                    .body("salary", contains(1234.5677f, 2500, null))
        ;
    }

    @Test
    public void devoFazerVerificacoesAvancadas() {
        given()
                .when()
                    .get(BASE_URL_USER)
                .then()
                    .statusCode(200)
                    .body("$", hasSize(3)) // '$' é uma referência á raiz da lista, do json.
                    .body("age.findAll{it <= 25}.size()", is(2))
                    .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
                    .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
                    .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
                    .body("find{it.age <= 25}.name", is("Maria Joaquina"))
                    .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
                    .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva","Maria Joaquina"))
                    .body("name.collect{it.toUpperCase()}", hasItems("MARIA JOAQUINA"))
                    .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItems("MARIA JOAQUINA"))
                    .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"),arrayWithSize(1)))
                    .body("age.collect{it * 2}", hasItems(60, 50, 40))
                    .body("id.max()", is(3))
                    .body("salary.min()", is(1234.5678f))
        ;
    }

    @Test
    public void devoUnirJsonPathComJAVA() {
        ArrayList names =
            given()
                    .when()
                    .get(BASE_URL_USER)
                    .then()
                    .statusCode(200)
                    .extract().path("name.findAll{it.startsWith('Maria')}")
            ;

        assertEquals(1, names.size());
        assertTrue(names.get(0).equals("Maria Joaquina"));
        assertEquals(names.get(0), "Maria Joaquina");

    }
}
