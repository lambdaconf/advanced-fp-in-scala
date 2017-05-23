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
  type Input = String
  type ErrorMessage = String

  type Parser[A] =
    Input => Either[ErrorMessage, (Input, A)]

  def fail[A](err: String): Parser[A] =
    (input: String) => Left(err)

  def anyChar: Parser[Char] =
    (input: String) =>
      if (input.length == 0) Left("Expected character but found end of input")
      else {
        val c = input.charAt(0)

        Right((input.drop(1), c))
      }

  def satisfies[A](p: Parser[A])(f: A => Boolean): Parser[A] =
    (input: String) =>
      p(input) match {
        case Left(err) => Left(err)
        case Right((input, a)) =>
          if (f(a)) Right((input, a))
          else Left("Value did not satisfy predicate: " + a)
      }

  val parseLeftCurlyBracket = satisfies(anyChar)(_ == '{')

  val parseLeftSquareBracket = satisfies(anyChar)(_ == '[')

  val parseLeftAnyBracket =
    or(parseLeftCurlyBracket, parseLeftSquareBracket)

  def or[A](left: Parser[A], right: Parser[A]): Parser[A] =
    (input: String) =>
      left(input) match {
        case Left(err) => right(input)
        case x => x
      }

  def then[A, B](first: Parser[A],
                 snd: Parser[B]): Parser[(A, B)] =
    ???
}

object exercise4 {
  val squareF : (=> Int) => Int = x => x * x

  def squareM(x: => Int): Int = x * x

  trait Id {
    def apply[A](x: A): A
  }

  object identity extends Id {
    def apply[A](x: A): A = x
  }

  identity(1) // 1
  identity[String]("foo") // "foo"

  def snd[A, B](v: (A, B)): B = ???
}
