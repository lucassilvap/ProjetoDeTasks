package main.java.com.example;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private int age;
    private String profession;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Person() {

    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

@RestController
class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PageCreate pageCreate;

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(personService.findById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(personService.findALLPerson());
    }

    @GetMapping()
    public ResponseEntity<?> findAllByQueryParameters(@RequestParam(required = true, name="age") Optional<Integer> age,
                                                      @RequestParam(required = true, name="profession") Optional<String> profession,
                                                      @RequestParam(required = true, name="page") Optional<Integer> page,
                                                      @RequestParam(required = true, name="size") Optional<Integer> size) {
        return ResponseEntity.ok(personService.findAllByQueryParameters(age,profession, page, size));
    }

    @PostMapping
    public ResponseEntity<?> savePerson(@RequestBody Person person){
       return ResponseEntity.ok(personService.savePerson(person));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateById(@PathVariable(name = "id") Long id, @RequestBody Person person){
        return ResponseEntity.ok(personService.update(id, person));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id") Long id){
        personService.delete(id);
        return ResponseEntity.ok("successfully removed");
    }

}

@Service
class PersonService{

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PageCreate pageCreate;

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

    public Page<Person> findAllByQueryParameters(Optional<Integer> age, Optional<String> profession, Optional<Integer> page, Optional<Integer> size) {
        return personRepository.findAll(PersonSpecification.spec(age, profession), pageCreate.build(page, size) );
    }
}

@Repository
interface PersonRepository extends CrudRepository<Person, Long> , JpaSpecificationExecutor<Person> {
        Optional<Person> findByNameIgnoreCase(String name);
}


abstract class PersonSpecification{
    private static Specification<Person> ByAge(int age){
        return (root, cq, cb) -> cb.equal(root.get("age"), age);
    }

    private static Specification<Person> Byprofession(String profession){
        return (root, cq, cb) -> cb.like(cb.upper(root.get("profession")),  profession.toUpperCase());
    }

    public static Specification<Person> spec(Optional<Integer> age, Optional<String> profession){
        Specification<Person> specification = Specification.where(null);
        if (age.isPresent())
         specification = specification.and(ByAge(age.get()));
        if (profession.isPresent())
            specification = specification.and(Byprofession(profession.get()));
        return specification;
    }
}

 @Service
 class PageCreate {

    int pageDefault = 0;
    int sizeDefault = 10;

    public PageCreate setPage(int page) {
        this.pageDefault = page;
        return this;
    }

    public PageCreate setSize(int size) {
        this.sizeDefault = size;
        return this;
    }

    public PageRequest build (Optional<Integer> page, Optional<Integer> size) {
        if(page.isPresent())
            setPage(page.get());
        if(size.isPresent())
            setSize(size.get());
        return PageRequest.of(pageDefault, sizeDefault);
    }

    public PageImpl<Person> pageImp (List<Person> personList, Page<Person> page){
        return new PageImpl<Person>(personList, page.getPageable(), page.getTotalElements());
    }

}

class NameMustBeUniqueException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;
    public NameMustBeUniqueException() {
        super(HttpStatus.BAD_REQUEST, "firstName must be unique!");
    }
}

class IdNotFoundException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;
    public IdNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "id not found!");
    }
}

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
 class MySpringConfiguration {

}







