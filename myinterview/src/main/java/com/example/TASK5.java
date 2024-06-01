package com.example;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
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

/**
 * Create an implementation of a Rest API .
 * Expose an API. Feel free to explore possibilities/functionalities/capabilities following Rest standard.
 * We suggest that your implementation have at least a CRUD scenario.
 *
 */


/*Anotação para iniciar uma aplicação spring boot */
@SpringBootApplication
public class TASK5 {
    public static void main(String[] args) {
        SpringApplication.run(TASK5.class, args);
    }
}


/*Entidade Person que sera gerenciada pelo jpa */
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


/*Controlador rest para as requisiçoes de person */
@RestController
class PersonController {

    @Autowired
    private PersonService personService;


    /* Busca uma pessoa informando o id*/
    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(personService.findById(id));
    }

    /*Retorna todas as pessoas */
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(personService.findALLPerson());
    }

    /*Paginação com filtragem dinamica por parametros como age e profession */
    @GetMapping()
    public ResponseEntity<?> findAllByQueryParameters(@RequestParam(required = true, name="age") Optional<Integer> age,
                                                      @RequestParam(required = true, name="profession") Optional<String> profession,
                                                      @RequestParam(required = true, name="page") Optional<Integer> page,
                                                      @RequestParam(required = true, name="size") Optional<Integer> size) {
        return ResponseEntity.ok(personService.findAllByQueryParameters(age,profession, page, size));
    }


    /*Salva uma pessoa*/
    @PostMapping
    public ResponseEntity<?> savePerson(@RequestBody Person person){
       return ResponseEntity.ok(personService.savePerson(person));
    }


    /*Atualizar pessoa passando uma nova entidade e passando o id da pessoa que quer atualizar */
    @PutMapping("{id}")
    public ResponseEntity<?> updateById(@PathVariable(name = "id") Long id, @RequestBody Person person){
        return ResponseEntity.ok(personService.update(id, person));
    }

    /*Deleta uma pessoa passando o id */
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

    /*Salva uma pessoa*/
    public Person savePerson(Person person){
      validPersonName(person.getName());
      return personRepository.save(person);
    }

    /*Valida se já não existe uma pessoa salvar no banco com o mesmo nome. Se existir lança uma exception
    * que o spring vai manipular e transformar como resposta para a requisição */
    private void validPersonName(String name){
        Optional<Person> person = personRepository.findByNameIgnoreCase(name);
        if (person.isPresent()) throw new NameMustBeUniqueException();
    }

    /*Busca uma pessoa por id se encontrar retorna a pessoa não encontrar lança uma exception que o
    * spring vai manipular e transformar como resposta para a requisição */
    public Person findById(Long id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) return person.get();
        throw new IdNotFoundException();
    }


    /* Lista todas as pessoas salvas no banco se não encontrar ninguem retorna uma lista vazia */
    public List<Person> findALLPerson() {
       return (List<Person>) personRepository.findAll();
    }

    /*Deleta uma pessoa por id */
    public void delete(Long id) {
        Person person = findById(id);
        personRepository.deleteById(id);
    }


    /*Busca a pessoa por id, valida se o novo nome está disponivel
    * A entidade com esse id buscado está em estado gerenciada então quando salva novamente um objeto com o mesmo
    * id gerenciado a entidade e atualizada */
    public Person update(Long id, Person person) {
        Person person1 = findById(id);
        if (!person1.getName().equalsIgnoreCase(person.getName())){
            validPersonName(person.getName());
        }
        person.setId(person1.getId());
        return personRepository.save(person);
    }

    /*Faz a paginação de pessoas com parametros dinamicos*/
    public Page<Person> findAllByQueryParameters(Optional<Integer> age, Optional<String> profession, Optional<Integer> page, Optional<Integer> size) {
        return personRepository.findAll(PersonSpecification.spec(age, profession), pageCreate.build(page, size) );
    }
}


/*Repositorio para realizar operações de banco de dados, estende CrudRepository para operações básicas
* e também extende JpaSpecificationExecutor para consultas dinamicas*/
@Repository
interface PersonRepository extends CrudRepository<Person, Long> , JpaSpecificationExecutor<Person> {
        Optional<Person> findByNameIgnoreCase(String name);
}


/*Specification para consultas dinamicas*/
abstract class PersonSpecification{
    /*Faz a consulta por idade */
    private static Specification<Person> ByAge(int age){
        return (root, cq, cb) -> cb.equal(root.get("age"), age);
    }

    /*Faz a consulta por profissao */
    private static Specification<Person> Byprofession(String profession){
        return (root, cq, cb) -> cb.like(cb.upper(root.get("profession")), "%" + profession.toUpperCase()  + "%");
    }

    /*Verifica quais parametros estão presentes e faz a contetanação das consultas */
    public static Specification<Person> spec(Optional<Integer> age, Optional<String> profession){
        Specification<Person> specification = Specification.where(null);
        if (age.isPresent())
         specification = specification.and(ByAge(age.get()));
        if (profession.isPresent())
            specification = specification.and(Byprofession(profession.get()));
        return specification;
    }
}

/*Página para a crição de uma Entidade Page que serve para paginação*/
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

}


/*Excessão utilizada para o nome que deve ser unico*/
class NameMustBeUniqueException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;
    public NameMustBeUniqueException() {
        super(HttpStatus.BAD_REQUEST, "firstName must be unique!");
    }
}

/*Excessão que o Id deve ser unico */
class IdNotFoundException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;
    public IdNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "id not found!");
    }
}


/*Configuração para permitir o paginamento*/
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
 class MySpringConfiguration {

}







