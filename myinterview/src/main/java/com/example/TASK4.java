package com.example;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Create an implementation of a Rest API client.
 * Prints out how many records exists for each gender and save this file to s3 bucket
 * API endpoint=> https://3ospphrepc.execute-api.us-west-2.amazonaws.com/prod/RDSLambda 
 * AWS s3 bucket => interview-digiage
 *
 */
public class TASK4 {
    public static void main(String[] args) {

        /*Define A url para qual será feita a requisição */
        String apiUrl = "https://3ospphrepc.execute-api.us-west-2.amazonaws.com/prod/RDSLambda";
        /*Define o nome do bucket da s3 onde o arquivo será salvo*/
        String s3Bucket = "interview-digiage";
        /*Define o caminho do objeto dentro do bucket*/
        String s3File = "genders.txt";
        /*Clia uma instancia do Http client serve para oferecer uma api http*/
        HttpClient c = HttpClient.newHttpClient();
        /*Cria um construtor de requisição http*/
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        /*Após o build o objeto request está pronto para realizar uma requisição */

        try {
            /*Envia a requisição criada no trecho anterior e faz o processamento da resposta*/
            /* Espera pela resposta da requisição e armazena na resp*/
            /* O BodyHandlers.ofString significa que queremos tratar o corpo da resposta como uma string*/
            HttpResponse<String> resp = c.send(request, HttpResponse.BodyHandlers.ofString());

            /*A resposta é convertida em um json array */
            JsonArray jsonA= new Gson().fromJson(resp.body(), JsonArray.class);

            /*Map utilizado para armazenar a contagem dos generos*/
            Map<String, Integer> genderCount = new HashMap<>();

            /*Um loop sobre o array de json
            * Cada elemento é convertido em um objeto json
            * É extraido o valor do gender e armazenado no map que coloca 0 se o valor for novo ou soma +1 se
            * o valor já existir */
            for (JsonElement elem : jsonA) {
                JsonObject person = elem.getAsJsonObject();
                String gender = person.get("gender").getAsString();
                genderCount.put(gender, genderCount.getOrDefault(gender, 0) + 1);
            }

            /*Um loop sobre o par de chaves e valor do map
            * Mostrando quantas pessoas existem em cada genero*/
            System.out.println("Quantidade de pessoas por gênero:");
            for (Map.Entry<String, Integer> entry : genderCount.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            saveToS3(genderCount,s3Bucket, s3File);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveToS3(Map<String, Integer> data, String bucketName, String filePath) {
        /*BasicAWSCredentials é um objeto que contem as credenciais de acesso para aws
        * Essa autenticacao serve para autenticar a requisicao*/
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAU7BHLOLBKPZTHAP2", "OLBdHAT62RJ5Odwl98JIbOWKL9LQxtOBYqNMQ9TY");

        /*É construido um cliente s3 Ele é configurado com as credencias
        e com a região onde o bucket está localizado  */
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_WEST_2)
                .build();

        /* Sera usado para armazenar os dados no arquivo*/
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        /* Sera usado para escrever no outputStream */
        PrintWriter writer = new PrintWriter(outputStream);
        writerData(writer, data);

        ObjectMetadata metadata = new ObjectMetadata();
        setoutputstreamsize(metadata, outputStream);

        /* Aqui é criado um objeto que representa os dados a serem carregados na aws
        * Contém os dados que foram previamente escritos*/
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        /*bucket: nome onde os dados são armazenados
        * filePath: caminho do objeto dentro do bucket
        *  metadata1: informações relacionadas aos dados*/
        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, inputStream, metadata);
        /*Envia os dados para a amazon e armazena no bucket previamente especificado*/
        s3Client.putObject(request);

        System.out.println("Dados salvos no Amazon S3");
    }

    private static void writerData(PrintWriter printWriter, Map<String, Integer> data){
        // Escreve os dados no stream ByteArrayOutputStream
        // Itera sobre data
        // Para cada entrada escreve a chave genero e a quantidade no printWriter*/
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            printWriter.println(entry.getKey() + ": " + entry.getValue());
        }
        /* Grava os dados no fluxo de saida*/
        printWriter.flush();
    }

    /* Cria um objeto de metadados que são informaçoes adicionais sobre o objeto que sera carregado na aws
     * Definine o tamanho do conteudo como o tamanho do outputStream que contem os dados que seram carregadors */
    private static void setoutputstreamsize(ObjectMetadata objectMetadata, ByteArrayOutputStream outputStream) {
        objectMetadata.setContentLength(outputStream.size());
    }


}