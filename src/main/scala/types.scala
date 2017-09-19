package lambdaconf.types

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

/** 
  * Create ADT representing geometric figures using traits and case classes
  * A figure can be a rectangle or trapezoid or cicrle
  * Rectangle has side a and side b
  * Trapezoid has base a, base b and height h
  * Circle has radious r
  * 
  * Can you think of other representation using different machinery?
  */
object exercise1 {
  sealed trait Figure
}

/**
  * Call me maybe
  */
object exercise2 {
  /** Maybe is of kind *. Change it to be * -> * so that it can hold values of any type */
  sealed trait Maybe
  case class Just(value: String)
  case object Empty

  val gotIt = Just("hello")
  val nah = Empty
  // val gotNum = Just(10)
  // case class User(name: String)
  // val maybeUser = Just(User("Swift"))
}

/**
  * Call each function with any argument so that the code compiles.
  * Please provide the types explicitly in [ ]
  */
object exercise3 {
  def func1[A](a: A): A = a
  def func2[F[_], A](f: F[A]): F[A] = f
  def func3[E[_, _], A, B](e: E[A, B]): E[A, B] = e
  def func4[T[_[_]], F[_]](t: T[F]): T[F] = t
}


object exercise4 {
  sealed trait Example[F[_]] {
    def value: F[String]
  }

  /*
  new Example[_] { // <-- ???
    def value: Either[String, Int] = Right(2)
  }
  */
}

/** Explore the mysteries of magic box */
object exercise5 {

  /** This is a magic box. */
  trait MagicBox[A] {
    type B
    def create[C](c: C): MagicBox[C]
    def get: A

    def map(f: A => B): MagicBox[B] = create[B](f(get))
  }

  /** create a class IntMagicBox[A] that is a MagicBox where B is an Int */
  // class IntMagicBox[A] ???

  /** 
    Method transformAndExtract should take a MagicBox and function f, apply to map method and then
    retrive the value using get method.
    
    Implement transformAndExtract
    */
  // def transformAndExtract[A](mb: MagicBox[A])(f: A => ???): ??? = ???

  // val strIntMagicBox = new IntMagicBox[String]("hello existential")
  val length: String => Int = _.length
  /** call transformAndExtract with instance of intMagicBox and function length to test that it works*/
  // transformAndExtract[String](strIntMagicBox)(length)

}
