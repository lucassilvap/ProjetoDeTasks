package main.java.com.example;

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

        String apiUrl = "https://3ospphrepc.execute-api.us-west-2.amazonaws.com/prod/RDSLambda";
        String s3BucketName = "interview-digiage";
        String s3FilePath = "genders.txt";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body().toString());
            JsonArray jsonArray = new Gson().fromJson(response.body(), JsonArray.class);
            Map<String, Integer> genderCounts = new HashMap<>();

            for (JsonElement element : jsonArray) {
                JsonObject person = element.getAsJsonObject();
                String gender = person.get("gender").getAsString();
                genderCounts.put(gender, genderCounts.getOrDefault(gender, 0) + 1);
            }

            System.out.println("Quantidade de pessoas por gênero:");
            for (Map.Entry<String, Integer> entry : genderCounts.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            saveToS3(genderCounts,s3BucketName, s3FilePath);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveToS3(Map<String, Integer> data, String bucketName, String filePath) {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAU7BHLOLBKPZTHAP2", "OLBdHAT62RJ5Odwl98JIbOWKL9LQxtOBYqNMQ9TY");
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_WEST_2)
                .build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        // Escreve os dados no stream
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            writer.println(entry.getKey() + ": " + entry.getValue());
        }
        writer.flush();

        // Cria um objeto de metadados
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(outputStream.size());

        // Carrega os dados no S3
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, inputStream, metadata);
        s3Client.putObject(request);

        System.out.println("Dados salvos no Amazon S3");
    }


}