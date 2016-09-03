package lambdaconf.functions

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

object exercise1 {
  // Domain: {Vegetables, Fruits, Meat, Dairy, Eggs}
  // Codomain: {Love, Like, Neutral, Dislike, Hate}
}

object exercise2 {
  val compareStrings: (Char => Char) => (String, String) => Boolean = ???
}

object exercise3 {
  type Parser[A] = String => Either[String, (String, A)]

  def or[A](left: Parser[A], right: Parser[A]): Parser[A] = ???
}

object exercise4 {
  def snd[A, B](v: (A, B)): B = ???
}
