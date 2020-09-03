package br.ce.fabio.rest;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class FileTes {

    @Test
    public void deveObrigarEnvioArquivo() {
        given()
                .log().all()
                .when()
                    .post("http://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("error", is("Arquivo não enviado"))
                ;
    }

    @Test
    public void deveFazerUploadDoArquivo() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/print.png"))
                .when()
                    .post("http://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", is("print.png"))
        ;
    }

    @Test
    public void deveBaixarAquivo() throws IOException {
        byte[] image = given()
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/download")
                .then()
                .statusCode(200)
                .extract().asByteArray();

        File imagem = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(imagem);
        out.write(image);
        out.close();

        Assert.assertThat(imagem.length(), lessThan(100000L));
    }

}
