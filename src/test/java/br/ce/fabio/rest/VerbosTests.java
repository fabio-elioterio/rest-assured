package br.ce.fabio.rest;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class VerbosTests {

    @Test
    public void deveSalvarUsuario() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\": \"Jose\", \"age\": 50 }")
                .when()
                    .post("http://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Jose"))
                    .body("age", is(50))
                ;

    }

    @Test
    public void deveSalvarUsuarioMap() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Usuario via map");
        params.put("age", 25);

        given()
                .log().all()
                .contentType("application/json")
                .body(params)
                .when()
                .post("http://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Usuario via map"))
                .body("age", is(25))
        ;

    }

    @Test
    public void deveSalvarUsuarioObjeto() {
        User user = new User("Usuario via objeto", 35);

        given()
                .log().all()
                .contentType("application/json")
                .body(user)
                .when()
                    .post("http://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Usuario via objeto"))
                    .body("age", is(35))
        ;

    }

    @Test
    public void deveDeserializarObjetoAoSalvarUsuario() {
        User user = new User("Usuario deserializado", 35);

       User usuarioInserido = given()
                .log().all()
                .contentType("application/json")
                .body(user)
                .when()
                    .post("http://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .extract().body().as(User.class)
        ;

        System.out.println(usuarioInserido);
        assertEquals("Usuario deserializado", usuarioInserido.getName());
        assertThat(usuarioInserido.getAge(), is(35));
        assertThat(usuarioInserido.getId(), notNullValue());

    }

    @Test
    public void naoDeveSalvarUsuarioSemNome() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"age\": 50 }")
                .when()
                .post("http://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(400)
                .body("id", is(nullValue()))
                .body("error", is("Name é um atributo obrigatório"))
        ;

    }

    @Test
    public void deveSalvarUsuarioViaXML() {
        given()
                .log().all()
                .contentType(ContentType.XML)
                .body("<user><name>Jose</name><age>50</age></user>")
                .when()
                    .post("http://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Jose"))
                .body("user.age", is("50"                     ))
        ;

    }

    @Test
    public void deveDeserializarXMLAoSalvarUsuario() {
        User user = new User("Usuario XML", 40);

        User usuarioInserido = given()
                .log().all()
                .contentType(ContentType.XML)
                .body(user)
                .when()
                    .post("http://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                    .extract().body().as(User.class )
        ;
        assertThat(usuarioInserido.getId(), is(notNullValue()));
        assertThat(usuarioInserido.getName(), is("Usuario XML"));
        assertThat(usuarioInserido.getAge(), is(40));
        assertThat(usuarioInserido.getSalary(), nullValue());
    }

    @Test
    public void deveSalvarUsuarioViaXMLUsandoObjeto() {
        User user = new User("Usuario XML", 40);
        given()
                .log().all()
                .contentType(ContentType.XML)
                .body(user)
                .when()
                .post("http://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Usuario XML"))
                .body("user.age", is("40"))
        ;

    }

    @Test
    public void deveAlterarUsuario() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Usuario alterado\", \"age\": 80 }")
                .when()
                    .put("http://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario alterado"))
                    .body("age", is(80))
                    .body("salary", is(notNullValue()))
        ;

    }

    @Test
    public void deveCustomizarURL() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Usuario alterado\", \"age\": 80 }")
                .when()
                    .put("http://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario alterado"))
                    .body("age", is(80))
                    .body("salary", is(notNullValue()))
        ;

    }

    @Test
    public void deveCustomizarURLParte2() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Usuario alterado\", \"age\": 80 }")
                .pathParam("entidade", "users")
                .pathParam("userId", "1")
                .when()
                    .put("http://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario alterado"))
                    .body("age", is(80))
                    .body("salary", is(notNullValue()))
        ;

    }

    @Test
    public void deveRemoverUsuario() {
        given()
                .log().all()
                .when()
                    .delete("http://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(204)
                ;
    }

    @Test
    public void naoDeveRemoverUsuarioInexistente() {
        given()
                .log().all()
                .when()
                    .delete("http://restapi.wcaquino.me/users/1000")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("error", is("Registro inexistente"))
        ;
    }
}
