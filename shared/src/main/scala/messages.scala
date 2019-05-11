package metrifier
package shared

import higherkindness.mu.rpc.protocol._

object model {

  type PersonAggregation =
    (Person, Person, Person, Person, PersonLinkList, PersonLinkList, PersonList)

  @message
  case class PersonList(persons: List[Person], count: Int) {

    def add(person: Person): PersonList = PersonList(persons = persons :+ person, count + 1)

  }

  @message
  case class PersonLinkList(links: List[PersonLink], count: Int)

  @message
  case class PersonId(id: String)

  @message
  case class Person(
      id: String,
      name: PersonName,
      gender: String,
      location: Location,
      email: String,
      picture: Option[Picture])

  case class PersonName(title: String, first: String, last: String)

  case class Location(street: String, city: String, state: String, postCode: Int)

  case class Picture(large: String, medium: String, thumbnail: String)

  case class PersonLink(p1: Person, p2: Person)

  case class DatabaseException(message: String, maybeCause: Option[Throwable] = None)
      extends RuntimeException(message) {

    maybeCause foreach initCause

  }

}
