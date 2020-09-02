package br.ce.fabio.rest;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserXMLtest {

    @Test
    public void devoTrabalharComXML() {
        given()
                .when()
                    .get("http://restapi.wcaquino.me/usersXML/3")
                .then()
                    .statusCode(200)
                    .rootPath("user")
                    .body("name", is("Ana Julia"))
                    .body("@id", is("3")) //@ serve para referenciar um atributo. Para o xlm todos os valores são strings.
                    .body("filhos.name.size()", is(2))
                    .body("filhos.name[0]", is("Zezinho"))
                    .body("filhos.name", hasItem("Luizinho"))
                    .body("filhos.name", hasItems("Luizinho", "Zezinho"))
        ;
    }

    @Test
    public void devoFazerPesquisasAvancadasComXML() {
        given()
                .when()
                    .get("http://restapi.wcaquino.me/usersXML")
                .then()
                    .statusCode(200)
                    .body("users.user.size()", is(3))
                    .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
                    .body("users.user.@id", hasItems("1", "2", "3"))
                    .body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
                    .body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
                    .body("users.user.salary.find{it != null}", is("1234.5678"))
                    .body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
                    .body("users.user.age.collect{it.toInteger() * 2}", hasItems(60,50,40))
                    .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))

        ;
    }
}
