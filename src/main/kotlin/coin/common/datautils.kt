package coin.common

import java.util.*

object IdProvider {
  fun get(): String {
    return UUID.randomUUID().toString().replace("-", "")
  }
}

interface Repository<E, K> {
  fun findAll(): Collection<E>
  fun findById(id: K): E?
  fun save(entity: E)
}