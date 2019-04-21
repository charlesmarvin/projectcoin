package coin.identity

import coin.common.Repository
import java.time.LocalDate

interface PersonService {
  fun findById(id: String): Person
  fun resolve(firstName: String, lastName: String, dob: LocalDate, ssnSerialNumber: String): Person?
}

class InMemoryPersonService(
  private val personRepository: PersonRepository
) : PersonService {
  override fun findById(id: String): Person {
    return when (val person = personRepository.findById(id)) {
      null -> throw NotFoundException()
      else -> person
    }
  }

  override fun resolve(firstName: String, lastName: String, dob: LocalDate, ssnSerialNumber: String): Person? {
    return personRepository.findAll().firstOrNull { p ->
      p.dob == dob && p.nationalIdentifier.number.endsWith(ssnSerialNumber)
    }
  }


}

interface PersonRepository : Repository<Person, String>

class InMemoryPersonRepository : PersonRepository {
  private val repository: MutableMap<String, Person> = mutableMapOf()

  override fun findAll(): Collection<Person> {
    return repository.values
  }

  override fun findById(id: String): Person? {
    return repository[id]
  }

  override fun save(entity: Person) {
    repository[entity.id] = entity
  }

}

class NotFoundException(message: String = "Could find person with given id.") :
  RuntimeException(message)