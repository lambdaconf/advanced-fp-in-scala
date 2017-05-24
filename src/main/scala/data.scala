package lambdaconf.data

import matryoshka._
import monocle._
import scalaz.{Lens => _, _}

import Scalaz._

object exercise1 {
  // sealed trait List[A]
  // final case class Cons[A](a: A, as: List[A]) extends List[A]
  // final case class Nil[A]() extends List[A]
  //
  // case class Person(name: String, age: Int)
  //
  // def sum(v: List[Int]): Int = v match {
  //   case Cons(a, as) => a + sum(as)
  //   case Nil() => 0
  // }

  case class Stream[A](a: A, as: () => Stream[A])

  def fibonacci: Stream[Int] = {
    def fib(p1: Int, p2: Int): Stream[Int] =
      Stream(p1 + p2, () => fib(p2, p1 + p2))

    fib(0, 1)
  }

  // 1. All prime numbers
  // 2. A JSON document
  case class Stats()
  case class Player(stats: Stats, inv: List[Item])
  case class Grid[A](value: Vector[Vector[A]])
  case class NPC(stats: Stats)
  case class Item(damageDone: Int)
  sealed trait Objective
  case class KillEnemy(npc: NPC, bounty: Int) extends Objective
  case class Cell(npcs: List[NPC], inv: List[Item])
  case class World(grid: Grid[Cell])
  case class GameWorld(me: Player, party: List[Player], map: World, obj: Objective)

  val world : GameWorld = ???

  val _Me : Lens[GameWorld, Player] =
    Lens(s => s.me, s => a => s.copy(me = a))

  val _Stats : Lens[Player, Stats] =
    Lens(s => s.stats, s => a => s.copy(stats = a))

  val _Inv : Lens[Player, List[Item]] =
    Lens(s => s.inv, s => a => s.copy(inv = a))

  val _KillEnemy : Prism[Objective, KillEnemy] =
    Prism(s => s match {
      case v @ KillEnemy(_, _) => Some(v)
      case _ => None
    }, v => (v : Objective))

  (_Me -> _Inv).set(world)(Nil)

  // S - Superstructure before the operation
  // T - Superstructure after the operation
  // A - Focus before the operation
  // B - Focus after the operation
  case class Lens[S, A](
    get: S => A,
    set: S => A => S
  ) { self =>
    def -> [B](that: Lens[A, B]): Lens[S, B] =
      Lens(
        get = (s: S) => that.get(self.get(s)),
        set = (s: S) => (b: B) =>
          self.set(s)(that.set(self.get(s))(b))
      )

    def -> [B](that: Prism[A, B]): Optional[S, B] =
      ???

    // def -> [B](that: Prism[A, B]): Prism[S, O

    // def gets: State[S, A] =
    //   for {
    //     s <- State.gets[S]
    //   } yield get(s)
    //
    // def puts(a: A): State[S, Unit] =
    //   for {
    //     s <- State.gets[S]
    //     _ <- State.puts(set(s)(a))
    //   } yield ()
  }

  case class Prism[S, A](
    get : S => Option[A],
    set : A => S
  ) { self =>
    def -> [B](that: Prism[A, B]): Prism[S, B] =
      Prism(
        get = s => self.get(s).flatMap(a => that.get(a)),
        set = b => self.set(that.set(b))
      )
  }

  case class Optional[S, A](
    get : S => Option[A],
    set : S => A => S
  )

  // type Lens[S, A] = LensP[S, S, A, A]
}

object exercise2 {
  sealed trait Boolean {
    // ???
  }
  object Boolean {
    val True = new Boolean {
      // ???
    }
    val False = new Boolean {
      // ???
    }
  }

  sealed trait Either[A, B] {
    // ???
  }
  object Either {
    def left[A, B](v: A): Either[A, B] = new Either[A, B] {
      // ???
    }
    def right[A, B](v: B): Either[A, B] = new Either[A, B] {
      // ???
    }
  }

  // Cheat: type Option[A] = Either[Unit, A]
  sealed trait Option[A] {
    // ???
  }
  object Option {
    def none[A]: Option[A] = new Option[A] {
      // ???
    }
    def some[A](v: A): Option[A] = new Option[A] {
      // ???
    }
  }
}

object exercise3 {
  trait Natural { self =>
    def fold[Z](zero: => Z, succ: Z => Z): Z

    def succ: Natural = new Natural {
      def fold[Z](zero: => Z, succ: Z => Z): Z = succ(self.fold(zero, succ))
    }
    def + (that: Natural): Natural = new Natural {
      def fold[Z](zero: => Z, succ: Z => Z): Z = that.fold[Natural](self, _.succ).fold[Z](zero, succ)
    }
    def * (that: Natural): Natural = new Natural {
      def fold[Z](zero: => Z, succ: Z => Z): Z =
        that.fold[Natural](Natural.zero, _ + self).fold[Z](zero, succ)
    }
    def isZero: Boolean = fold[Boolean](true, _ => false)
    def toInt: Int = fold[Int](0, _ + 1)
    override def toString = toInt.toString
  }
  object Natural {
    val zero = new Natural { def fold[Z](zero: => Z, succ: Z => Z): Z = zero }
    def of(v: Int): Natural = if (v == 0) zero else of(v - 1).succ
  }

  trait Integer { self =>
    // ???
  }
}

object exercise4 {
  sealed trait JSON
  final case object Null extends JSON
  final case class Array(value: List[JSON]) extends JSON
  final case class Object(value: List[(String, JSON)]) extends JSON
  final case class Number(value: String) extends JSON
  final case class Boolean(value: Boolean) extends JSON

  val _Null     : Prism[JSON, Unit] = ???
  val _Array    : Prism[JSON, List[JSON]] = ???
  val _Object   : Prism[JSON, List[(String, JSON)]] = ???
  val _Number   : Prism[JSON, String] = ???
  val _Boolean  : Prism[JSON, Boolean] = ???
}

object exercise5 {
  import exercise4._

  sealed trait ContactType
  case object Business extends ContactType
  case object Personal extends ContactType

  case class Person(name: String, age: Int, contactType: ContactType)

  val _name : Lens[Person, String] = ???
  val _age : Lens[Person, Int] = ???
  val _contactType : Lens[Person, ContactType] = ???

  val _Business : Prism[ContactType, Unit] = ???
  val _Personal : Prism[ContactType, Unit] = ???

  def encodePerson(v: Person): JSON = ???

  def decodePerson(v: JSON): Option[Person] = ???
}

object exercise6 {
  sealed trait JSON[A]
  // ???

  case class Person(name: String, parent: Option[Person])

  sealed trait Expr {
    def + (v: Expr): Expr = Add(this, v)
    def * (v: Expr): Expr = Add(this, v)
  }
  case class Num(i: Int) extends Expr
  case class Add(l: Expr, r: Expr) extends Expr
  case class Mul(l: Expr, r: Expr) extends Expr
  implicit class IntToExpr(i: Int) {
    def lit: Expr = Num(i)
  }

  val e : Expr = 1.lit * (4.lit + 2.lit)

  val TraverseJson: Traverse[JSON] = ???

  object fix {
    sealed trait Expr[A]
    case class Num[A](i: Int) extends Expr[A]
    case class Add[A](l: A, r: A) extends Expr[A]
    case class Mul[A](l: A, r: A) extends Expr[A]

    case class Fix[F[_]](value: F[Fix[F]])

    type RecursiveExpr = Fix[Expr]

    val expr : RecursiveExpr = Fix(Mul(Fix(Num(1)), Fix(Num(2))))
  }
}

object exercise7 {
  import exercise6._

  type RecursiveJSON = Fix[JSON]

  val ExampleJSON : RecursiveJSON = ???

  def detectStringNumbers(v: RecursiveJSON): RecursiveJSON = {
    val functorT: FunctorT[Fix] = implicitly[FunctorT[Fix]]

    import functorT._


    ???
  }
}
