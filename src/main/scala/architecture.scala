package lambdaconf.architecture

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

object exercise1 {
  final case class CashAmount()
  final case class AtmError(message: String)
  final case class TransactionID(identifier: String)
  final case class AccountID(identifier: String)

  trait Atm[F[_]] {
    def withdraw(account: AccountID, amount: CashAmount): Free[F, Either[AtmError, TransactionID]]

    def deposit(account: AccountID, amount: CashAmount): Free[F, TransactionID]

    def accounts: Free[F, NonEmptyList[AccountID]]

    def transfer(from: AccountID, to: AccountID, amount: CashAmount): Free[F, Either[AtmError, TransactionID]]

    def balance(account: AccountID): Free[F, CashAmount]
  }
  sealed trait AtmF[A]
  object AtmF {
    final case class Withdraw(account: AccountID, amount: CashAmount) extends AtmF[Unit]
    // ???
  }
  implicit val AtmAtmF: Atm[AtmF] = ???
}

object exercise2 {
  sealed trait LogLevel
  object LogLevel {
    final case object Info extends LogLevel
    final case object Debug extends LogLevel
  }

  trait Logging[F[_]] {
    def log(level: LogLevel, message: String): Free[F, Unit]
  }
  sealed trait LoggingF[A]
  object LoggingF {
    final case class Log(level: LogLevel, message: String) extends LoggingF[Unit]
    // ???
  }
  implicit val LoggingLoggingF: Logging[LoggingF] = ???
}

object exercise3 {
  import exercise1._
  import exercise2._

  def logAtm: AtmF ~> LoggingF = ???
}

object exercise4 {
  import exercise1._
  import exercise2._
  import exercise3._

  final case class Bank(/* ??? */)
  final case class Log(/* ??? */)

  def interpretAtm: AtmF ~> State[Bank, ?] = ???

  def interpretLog: LoggingF ~> State[Log, ?] = ???

  type Final[A] = State[(Bank, Log), A]

  def interpretProgram: AtmF ~> Final = ???

  def exampleProgram[F[_]](implicit atm: Atm[F]): Free[F, CashAmount] = for {
    accounts <- atm.accounts
    _        <- atm.deposit(accounts.head, CashAmount())
    balance  <- atm.balance(accounts.head)
  } yield balance

  val exampleBalance: CashAmount =
    exampleProgram[AtmF].foldMap[Final](interpretProgram).eval((Bank(), Log()))
}

object exercise5 {
  import exercise1._
  import exercise2._
  import exercise3._
  import exercise4._

  trait Console[F[_]] {
    def readLine: Free[F, String]

    def println(line: String): Free[F, Unit]
  }
  sealed trait ConsoleF[A]
  object ConsoleF {
    final case object ReadLine extends ConsoleF[String]
    // ???
  }
  implicit val ConsoleConsoleF: Console[ConsoleF] = ???

  sealed trait ConsoleLogging[F[_]] extends Console[F] with Logging[F]

  sealed trait ConsoleLoggingF[A]
  object ConsoleLoggingF {
    final case class ConsoleTerm[A](op: ConsoleF[A]) extends ConsoleLoggingF[A]
    final case class LoggingTerm[A](op: LoggingF[A]) extends ConsoleLoggingF[A]
  }

  implicit val ConsoleLoggingConsoleLoggingF: ConsoleLogging[ConsoleLoggingF] = ???

  def program: Free[ConsoleLoggingF, Unit] = ???
}
