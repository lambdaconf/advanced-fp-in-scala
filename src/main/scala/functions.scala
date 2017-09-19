package lambdaconf.functions

import matryoshka._
import monocle._
import scalaz._, Scalaz._

/**
  * Write an arbitrary function that shows your preferences towards different groceries.
  * Run your function with different arguments.
  */
object exercise1 {
  // Domain: {Vegetables, Fruits, Meat, Dairy, Eggs}
  // Codomain: {Love, Like, Neutral, Dislike, Hate}

  sealed trait Groceries
  case object Vegetables extends Groceries
  case object Fruits extends Groceries
  case object Meat extends Groceries
  case object Dairy extends Groceries
  case object Eggs extends Groceries

  sealed trait Attitude
  case object Love extends Attitude
  case object Like extends Attitude
  case object Neutral extends Attitude
  case object Dislike extends Attitude

  // preference(Eggs)
}

object exercise2 {
  /** "Fresh Food" sells Apples and Oranges */
  sealed trait Item
  case object Apple extends Item
  case object Orange extends Item

  /** They calculate price for items */
  val price: Item => Double = {
    case Apple => 3
    case Orange => 4
  }

  /** And apply some discounts from time to time */
  val discount: Item => Double = {
    case Apple => 0.1
  }

  /** 
    * Write function finalPrice that will calculate the final price for the item by
    * applying dicount to initial price
    * 
    * Call the function for both Apples and Oranges. Is everything in order? How can you fix it?
    */
  val finalPrice: Item => Double = ???
  finalPrice(Apple)
  finalPrice(Orange)
}

/**
  * Write a function that gives a prize for a comment.
  * Try to write it by reusing existing functions: rateComment and prize
  */
object exercise3 {

  import exercise1._

  val rateComment: String => Attitude = (str: String) =>
    if(str.contains("Scala")) Love else Neutral

  val prize: Attitude => Groceries = {
    case Love => Vegetables
    case _    => Eggs
  }

  val prizeForComment: String => Groceries = ???
}

/**
  * Follow the origins of quantum physics!
  */
object exercise4 {
  /** Cats are cute animals. */
  case class Cat(happiness: Int, alive: Boolean)

  /** They love food! */
  sealed trait Food
  case object Meat extends Food
  case object Milk extends Food

  /**
    * You can do a lot with a cat: pet it, feed it or even hiss on it.  
    * Your actions will alter its level of happiness
    */
  val pet: Cat => Cat =
    cat => cat.copy(happiness = cat.happiness + 2)
  val hiss: Cat => Cat =
    cat => cat.copy(happiness = cat.happiness - 10)
  val feed: (Cat, Food) => Cat = {
    case (c, Milk) => c.copy(happiness = c.happiness + 1)
    case (c, Meat) => c.copy(happiness = c.happiness + 3)
  }

  /** Schrodinger is a crazy fellow. He closed his cat in a box!
    * Now Schrodinger does not know whether the cat is dead or alive but he seems happy about it.
    */
  class SchrodingerBox(private val cat: Cat)
  object SchrodingerBox {
    def apply(cat: Cat): SchrodingerBox =
      if(cat.happiness <= 3)
        new SchrodingerBox(cat.copy(alive = false))
      else new SchrodingerBox(cat)
  }
  /**
    * He has no intentions of revealing if the cat is dead or alive, but if it is, Schrodinger
    * would like to feed it and pet it from time to time.
    * 
    * Your goals:
    * 1. Fulfil mad scientist dream and write a method (in SchrodingerBox) called "interact"
    * that allows applying all `Cat => Cat` functions on a cat (as long as the cat is alive)
    * 2. Try running running your function with `pet` and `hiss` functions.
    * 3. What about feed function? Can we still feed a cat within a SchrodingerBox?
    */
}

object exercise5 {

  /** "Fresh Food" sells Apples and Oranges */
  sealed trait Item
  case object Apple extends Item
  case object Orange extends Item

  /** They have different prices for London and different for the rest of United Kingdom */
  val priceLondon: Item => Double = {
    case Apple => 3
    case Orange => 4
  }
  val priceRestUK: Item => Double = {
    case Apple => 2
    case Orange => 3
  }

  /** They sometimes apply discounts for the items they sell */
  val mondayDiscount: Item => Double = {
    case Apple => 0.1
    case Orange => 0.0
  }
  val holidayDiscount: Item => Double = {
    case Apple => 0.1
    case Orange => 0.3
  }
  val noDicount: Item => Double = i => 0.0

  /** 
    * Create a function called createCheckout that crafts a checkout for a given shop
    * 
    * createCheckout will:
    * 1. take as a parameter a strategy to calculate original price
    * 2. take as a parameter a strategy to apply discount
    * 3. return a function that will take an Item as parameter and return a final price for
    *    that item as a result
    */
  val createCheckout: (Item => Double) => (Item => Double) => (Item => Double) = ???

  val checkout: Item => Double = createCheckout(priceLondon)(mondayDiscount)
}

/**
  * Implement compose, curry, uncurry
  */
object exercise6 {
  def compose[A, B, C](f: A => B, g: B => C): A => C = ???
  def curry[A, B, C](f: (A, B) => C): A => B => C = ???
  def uncurry[A, B, C](f:  A => B => C): (A, B) => C = ???
}
