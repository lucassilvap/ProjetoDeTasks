package main.java.com.example;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/**
 * Create an implementation of a Rest API .
 * Expose an API. Feel free to explore possibilities/functionalities/capabilities following Rest standard.
 * We suggest that your implementation have at least a CRUD scenario.
 *
 */

@SpringBootApplication
public class TASK5 {
    public static void main(String[] args) {
        SpringApplication.run(TASK5.class, args);
    }
}

@Entity
class  Person{

    private String name;
    private String age;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Person() {
    }

    public Person(String name, String age) {
        this.name = name;
        this.age = age;
    }
}

@RestController
class PersonController {

    @GetMapping
    public String getPerson(){
        return "estou rodando novamente";
    }

}

@Service
class PersonService{

}

@Repository
interface PersonRepository{

}

    @Configuration
    class DataSourceConfig {

        @Bean
        public DataSource getDataSource() {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName("org.h2.Driver");
            dataSourceBuilder.url("jdbc:h2:mem:test");
            dataSourceBuilder.username("SA");
            dataSourceBuilder.password("");
            return dataSourceBuilder.build();
        }
}


