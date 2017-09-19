package lambdaconf.typeclasses

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

/**
  * Implement Eq type class
  */
object exercise1 {

  /** Before we start, try to reason what is wrong with the code below? */
  def check(i: Int): String =
    if(i == "1") "It is one!" else "It is something else then 1"
  check(1)
  check(2)

  /** 
    * 1. Create Eq type class with method equalz checking for equality of two object of type A
    * 2. Create extension method === 
    * 3. Create instance for Int
    * 4. Reimplelemnt check method using Eq
    */

}
