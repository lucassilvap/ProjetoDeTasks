package main.java.com.example;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

    public Person() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }
}

@RestController
class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(personService.findById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(personService.findALLPerson());
    }

    @PostMapping
    public ResponseEntity<?> savePerson(@RequestBody Person person){
       return ResponseEntity.ok(personService.savePerson(person));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id") Long id){
        personService.delete(id);
        return ResponseEntity.ok("successfully removed");
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateById(@PathVariable(name = "id") Long id, @RequestBody Person person){
        return ResponseEntity.ok(personService.update(id, person));
    }
}

@Service
class PersonService{

    @Autowired
    PersonRepository personRepository;

    public Person savePerson(Person person){
      validPersonName(person.getName());
      return personRepository.save(person);
    }

    private void validPersonName(String name){
        Optional<Person> person = personRepository.findByNameIgnoreCase(name);
        if (person.isPresent()) throw new NameMustBeUniqueException();
    }

    public Person findById(Long id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) return person.get();
        throw new IdNotFoundException();
    }

    public List<Person> findALLPerson() {
       return (List<Person>) personRepository.findAll();
    }

    public void delete(Long id) {
        Person person = findById(id);
        personRepository.deleteById(id);
    }

    public Person update(Long id, Person person) {
        Person person1 = findById(id);
        validPersonName(person.getName());
        person.setId(person1.getId());
        return personRepository.save(person);
    }
}

@Repository
interface PersonRepository extends CrudRepository<Person, Long> {
        Optional<Person> findByNameIgnoreCase(String name);
}


class NameMustBeUniqueException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;
    public NameMustBeUniqueException() {
        super(HttpStatus.BAD_REQUEST, "firstName must be unique !");
    }
}

class IdNotFoundException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;
    public IdNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "id not found!");
    }
}






