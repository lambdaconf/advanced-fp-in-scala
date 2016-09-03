package lambdaconf.typeclasses

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

object exercise1 {
  sealed trait PathLike[A] {
    // ???
  }
  object PathLike {
    def apply[A: PathLike]: PathLike[A] = implicitly[PathLike[A]]
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

  implicit val NodePathLike: PathLike[Node] = new PathLike[Node] {
    // ???
  }
}

object exercise4 {
  import exercise1._
  import exercise2._
  import exercise3._

  implicit class PathLikeSyntax[A: PathLike](self: A) {
    // ???
  }
}
