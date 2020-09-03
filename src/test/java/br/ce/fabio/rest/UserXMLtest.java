package br.ce.fabio.rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class UserXMLtest {

    public static RequestSpecification requestSpecification;
    public static ResponseSpecification responseSpecification;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
//        RestAssured.port = 80;   // Exemplos de metodos statics do rest assured
//        RestAssured.basePath = "/v2";
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(200);
        responseSpecification = responseSpecBuilder.build();

        RestAssured.requestSpecification = requestSpecification;
        RestAssured.responseSpecification = responseSpecification;

    }

    @Test
    public void devoTrabalharComXML() {
        given()
                .when()
                    .get("/usersXML/3")
                .then()
//                    .statusCode(200)
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
                    .get("/usersXML")
                .then()
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

    @Test
    public void devoFazerPesquisasAvancadasComXMLEJava() {
        ArrayList<NodeImpl> nomes = given()
                .when()
                    .get("/usersXML")
                .then()
                    .statusCode(200)
                    .extract().path("users.user.name.findAll{it.toString().contains('n')}");

         assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
         assertEquals("Ana Julia".toUpperCase(), nomes.get(1).toString().toUpperCase());
         assertEquals(2, nomes.size());

    }
    @Test
    public void devoFazerPesquisasAvancadasComXPath() {
       given()
                .when()
                    .get("/usersXML")
                .then()
                    .statusCode(200)
                    .body(hasXPath("count(/users/user)", is("3")))
                    .body(hasXPath("/users/user[@id = '1']"))
                    .body(hasXPath("//user[@id = '1']"))
                    .body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
                    .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))
                    .body(hasXPath("//name", is("João da Silva")))
                    .body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
                    .body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
                    .body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))
                    .body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
                    .body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
                    .body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))
               ;

    }

}
