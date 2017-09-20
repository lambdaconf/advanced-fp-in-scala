package lambdaconf.patterns

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

object exercise1 {

  /** we have some potential errors*/
  sealed trait Error
  case object NotInTheMoodToTalkToHumans extends Error
  case object MiningBitcoinNoTimeToTalk extends Error

  /** this will give you String (but potentially an error as well) */
  val iWillGiveYouString: Double => Error \/ String =
    (d: Double) => if(d > 10) d.toString.right else NotInTheMoodToTalkToHumans.left

  /** this will give you Int (but potentially an error as well) */
  val iWillGiveYouInt: Long => Error \/ Int =
    (l: Long) => if(l < 0) l.toInt.right else MiningBitcoinNoTimeToTalk.left

    def calculation(d: Double, l: Long): Error \/ (String, Int) = for {
    s <- iWillGiveYouString(d)
    i <- iWillGiveYouInt(l)
  } yield (s, i)

  /** If I'm not interested with an error - I want to ignore it */
  val result: Option[(String, Int)] = ??? // calculate(20.0, -3)

  /** What is a potential issue with the returned type Error \/ (String, Int) */
  /** Run all those function calls and reason about the result, what can we do about it? */
  calculation(20.0, -2)
  calculation(20.0, 6)
  calculation(5.0, -2)
  calculation(5.0, 6)

}

object exercise2 {

  case class User(login: String)

  sealed trait Item
  case object Apple extends Item
  case object Orange extends Item
  /** Basket contains items */
  case class Basket(items: List[Item])

  /** I have to baskets */
  val basket1 = Basket(items =  List(Apple, Orange, Apple, Apple))
  val basket2 = Basket(items =  List(Apple, Apple, Orange))

  /** Could I merge two baskets together */
  val finalBasket: Basket = ??? // basket1 +  basket2

  val user1 = User("john")
  val user2 = User("pawel")
  val user3 = User("jeff")

  /** Could I merge two sessions together */
  val session1: Map[User, Basket] = Map(user1 -> basket1, user2 -> basket2)
  val session2: Map[User, Basket] = Map(user1 -> finalBasket, user2 -> basket1, user3 -> basket2)

  val session: Map[User, Basket] = ??? // session1 + session2

  /** Implement toBasket method, what needs to be added for this to work? */
  def toBasket(maybeItem: Option[Item]): Basket = ???
}

object exerciseA {
  def readRowCol(): (Int, Int) = {
    println("Please enter a row:")
    val row = readLine()
    println("Please enter a column:")
    val col = readLine()
    (row.toInt, col.toInt)
  }
}

object exerciseB {
  sealed trait Node
  final case object Root extends Node
  final case class Child(parent: Node, name: String) extends Node

  implicit val NodeMonoid: Monoid[Node] = ???
}

object exercise3 {
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
