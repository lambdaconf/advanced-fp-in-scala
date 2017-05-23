package lambdaconf.typeclasses

import matryoshka._
import monocle._

object exercise1 {
  /**
   * forAll[A](a => read(show(a)) == Some(a))
   */
  trait Debuggable[A] {
    def show(a: A): String

    def read(s: String): Option[A]
  }
  object Debuggable {
    def apply[A](implicit D: Debuggable[A]) = D

    implicit val DebuggableString = new Debuggable[String] {
      def show(a: String): String = a

      def read(s: String): Option[String] = Some(s)
    }

    implicit val DebuggableInt = new Debuggable[Int] {
      def show(a: Int): String = a.toString

      def read(s: String): Option[Int] =
        scala.util.Try(s.toInt).toOption
    }
  }

  implicit class DebuggableSyntax[A](a: A) {
    def show(implicit D: Debuggable[A]) = D.show(a)
  }

  1.show

  case class Box[A](value: A)
  object Box {
    implicit def DebuggableBox[A](implicit D: Debuggable[A]): Debuggable[Box[A]] = new Debuggable[Box[A]] {
      def show(a: Box[A]): String = D.show(a.value)

      def read(s: String): Option[Box[A]] = D.read(s).map(Box(_))
    }
  }

  def foo[A: Debuggable](a: A): String =
    a.show


  /**
   * forall[A](a => append(zero, a) == a)
   * forall[A](a => append(a, zero) == a)
   * forall[L, A](a => parent(descend1(l, a)) == Some((l, a)))
   */
  trait PathLike[L, A] {
    def zero: A

    def parent(path: A): Option[(L, A)]

    def descend1(label: L, path: A): A

    def append(a1: A, a2: A): A
  }
  object PathLike {
    def apply[L, A](implicit P: PathLike[L, A]): PathLike[L, A] = P
  }
}

object exercise2 {
  import exercise2._
}

object exercise3 {
  import exercise1._
  import exercise2._

  sealed trait Node
  final case object Root extends Node
  final case class Child(parent: Node, name: String) extends Node

  implicit val NodePathLike: PathLike[String, Node] = new PathLike[String, Node] {
    def append(a1: Node, a2: Node): Node =
      (a1, a2) match {
        case (a1, Root) => a1
        case (a1, Child(p, l)) => Child(append(a1, p), l)
      }
    def descend1(label: String, path: Node): Node = Child(path, label)
    def parent(path: Node): Option[(String, Node)] = path match {
      case Root => None
      case Child(p, n) => Some((n, p))
    }
    def zero: Node = Root
  }
}

object exercise4 {
  import exercise1._
  import exercise2._
  import exercise3._

  implicit class PathLikeSyntax[L, A](self: A) {
    def parent(implicit P: PathLike[L, A]) = P.parent(self)

    def / (l: L)(implicit P: PathLike[L, A]): A = P.descend1(l, self)
  }

  // path / "foo" / "bar"
  // path.parent
}
