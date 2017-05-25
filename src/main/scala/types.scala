package lambdaconf.types

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

object exercise1 {
  case class Person(age: Int, name: String)
  case class Address(street: String, zip: String, state: String, country: String)

  type LocatedPerson = (Person, Address)

  case class LocatedPerson2(person: Person, address: Address)

  final case class Line[A](p1: A, p2: A, p3: A, p4: A)
  final case class CheckersBoard(value: Line[Line[Option[CheckerPiece]]])

  sealed trait CheckerPiece
  case object RedPiece extends CheckerPiece
  case object BlackPiece extends CheckerPiece
  case object CrownedRedPiece extends CheckerPiece
  case object CrownedBlackPiece extends CheckerPiece
}

object exercise2 {
  final case class Box[A](value: A)
}

object exercise3 {
  // 1. scala.collection.List
  // 2. F[_, _]
  // 3. Option
  // 4. Int
  // 5. T[_[_], _]
  // T : [* => *, *] => *
  // T : (* => *) => * => *
  // T[List, Int]

  def magicAlgorithm[T[_[_], _], A](input: T[Option, A]): T[Option, A] =
    ???
}

object exercise4 {
  trait StackModule {
    type Stack[_]

    def newStack[A]: Stack[A]

    def push[A](v: A, s: Stack[A]): Stack[A]

    def pop[A](s: Stack[A]): Option[(A, Stack[A])]
  }

  trait SmartList[A] { self =>
    type A0

    val list : List[A0]

    val f0 : A0 => A

    def run: List[A] = list.map(f0)

    def map[B](f: A => B): SmartList[B] =
      new SmartList[B] {
        type A0 = self.A0

        val list = self.list

        val f0 = self.f0.andThen(f)
      }
  }
  object SmartList {
    def apply[A](l: List[A]): SmartList[A] = new SmartList[A] {
      type A0 = A

      val f0 = (a: A) => a

      val list = l
    }
  }

  trait FileSystem {
    // ???
  }
}

object exercise5 {
  sealed trait Example[F[_]] {
    def value: F[String]
  }

  val ExampleOption = new Example[Option] {
    def value: Option[String] = Some("foo")
  }

  /*
  def foo1[B](v: B): Example[({type Apply[A] = Either[A, B]})#Apply] = new Example[({type Apply[A] = Either[A, B]})#Apply] { // <-- ???
    def value: Either[String, B] = Right(v)
  }
  def foo2[B](v: B): Example[Either[?, B]] = new Example[Either[?, B]] {
    def value: Either[String, B] = Right(v)
  }
  */
}
