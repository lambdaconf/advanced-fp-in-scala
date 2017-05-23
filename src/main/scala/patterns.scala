package lambdaconf.patterns

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

object exercise1 {
  def parseInt(s: String): Option[Int] =
    scala.util.Try(s.toInt).toOption

  def readRowCol(): Option[(Int, Int)] = {
    println("Please enter a row:")
    val row = readLine()
    println("Please enter a column:")
    val col = readLine()
    (parseInt(row), parseInt(col)) match {
      case (Some(a), Some(b)) => Some((a, b))
      case _ => None
    }
  }

  // def readFile(path: String): Either[String, Array[Byte]] = ???
  sealed trait FileSystemError
  final case class FileNotFound(path: String) extends FileSystemError
  final case class FileLocked(path: String, expires: Int) extends FileSystemError
  final case class FilePermission(path: String, group: String) extends FileSystemError

  def readFile(path: String): FileSystemError \/ Array[Byte] = ???
}

object exercise2 {
  sealed trait Node
  final case object Root extends Node
  final case class Child(parent: Node, name: String) extends Node

  implicit val NodeMonoid: Monoid[Node] = ???
}

object exercise3 {
  def head: List ~> Option = new NaturalTransformation[List, Option] {
    def apply[A](v: List[A]): Option[A] = v match {
      case Nil => None
      case h :: _ => Some(h)
    }
  }

  case class Eager[A](run: A)

  case class Name[A](private val run0: () => A) {
    def run = run0()
  }

  case class Need[A](private val run0: () => A) {
    lazy val run = run0()
  }

  final case class Thunk[A](run: () => A)

  implicit val MonadThunk: Monad[Thunk] = ???
}

object exercise4 {
  def filter[A](f: A => Boolean, l: List[A]): List[A] = {
    val foldable = Foldable[List]

    ???
  }
}

object exercise5 {
  trait List[A] { self =>
    def fold[Z](nil: => Z, cons: (Z, A) => Z): Z

    final def :: (next: A): List[A] = new List[A] {
      def fold[Z](nil: => Z, cons: (Z, A) => Z): Z = {
        cons(self.fold(nil, cons), next)
      }
    }
  }
  object List {
    def empty[A]: List[A] = new List[A] {
      def fold[Z](nil: => Z, cons: (Z, A) => Z): Z = nil
    }
  }

  implicit val ListTraverse: Traverse[List] = ???
}
