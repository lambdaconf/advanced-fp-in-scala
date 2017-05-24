package lambdaconf.effects

import matryoshka._
import monocle._
import scalaz._

import Scalaz._

object exercise1 {
  object Console {
    val putStrLn : String => IO[Unit] =
      line => IO(unsafePerformIO = () => println(line))

    val getStrLn : IO[String] =
      IO(unsafePerformIO = () => readLine())
  }

  final case class IO[A](unsafePerformIO: () => A)
  implicit val IOMonad: Monad[IO] = new Monad[IO] {
    def point[A](a: => A): IO[A] = IO(() => a)
    def bind[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
      IO(() => f(fa.unsafePerformIO()).unsafePerformIO())
  }

  import Console._

  val program : IO[String] =
    for {
      _ <- putStrLn("Hello! What is your name?")
      n <- getStrLn
      _ <- putStrLn("Hello, " + n + ", good to meet you!")
    } yield n

  final case class State[S, A](run: S => (S, A))

  object State {
    def gets[S]: State[S, S] = State(s => (s, s))

    def puts[S](s2: S): State[S, Unit] = State(s => (s2, ()))

    def modify[S](f: S => S): State[S, Unit] = State(s => (f(s), ()))
  }

  implicit def StateMonad[S]: Monad[State[S, ?]] = new Monad[State[S, ?]] {
    def point[A](a: => A): State[S, A] =
      State(s => (s, a))

    def bind[A, B](fa: State[S, A])(f: A => State[S, B]): State[S, B] =
      State(s => {
        val (s2, a) = fa.run(s)

        f(a).run(s2)
      })
  }

  // case class Box[A](value: A)
  // implicit val BoxFunctor = new Functor[Box] {
  //   def map[A, B](fa: Box[A])(f: A => B): Box[B] =
  //     Box(f(fa.value))
  // }
  // implicit class FunctorSyntax[F[_], A](self: F[A]) {
  //   def map[B](f: A => B)(implicit F: Functor[F]): F[B] =
  //     F.map(self)(f)
  // }
  // val box = Box(1)
  // box.map(a => a.toString)

  import State._

  val program2 : State[Int, Int] =
    for {
      i <- gets[Int]
      _ <- puts(i + 1)
      _ <- modify[Int](_ + 2)
      i <- gets[Int]
    } yield i

  program2.run(10)

  // implicit val ListTraverse = new Traverse[List] {
  //   def sequence[G[_]: Applicative, A](fga: List[G[A]]): G[List[A]] =
  //     fga match {
  //       case Nil => Applicative[G].point(List.empty[A])
  //       case a :: as => (a |@| sequence[G, A](as))(_ :: _)
  //     }
  // }

  trait MonadIO[F[_]] {
    def capture[A](effect: => A): F[A]

    def attempt[A](fa: F[A]): F[Either[String, A]]
  }
  implicit val MonadIOIO = new MonadIO[IO] {
    def capture[A](effect: => A): IO[A] =
      IO(() => effect)

    def attempt[A](fa: IO[A]): IO[Either[String, A]] =
      IO(() => {
        try { Right(fa.unsafePerformIO()) }
        catch {
          case e : Throwable => Left(e.toString())
        }
      })
  }

  sealed trait Level
  case object Fine extends Level
  case object Debug extends Level

  trait MonadLog[F[_]] {
    def log(level: Level, line: String): F[Unit]
  }
  implicit val MonadLogMonadIO = new MonadLog[IO] {
    def log(level: Level, line: String): IO[Unit] =
      IO(() => println(level.toString() + ": " + line))
  }

  def doLog[F[_]: MonadLog]: F[Unit] = ???

  def doIO[F[_]: MonadIO]: F[Unit] = ???

  def program[F[_]: MonadIO: MonadLog]: F[Unit] = ???

  def main = program[IO].unsafePerformIO()

  trait Empty[F[_]] {
    def empty[A]: F[A]
  }
  implicit val ListEmpty = new Empty[List] {
    def empty[A]: List[A] = Nil
  }
  trait Consable[F[_]] {
    def cons[A](a: A, as: F[A]): F[A]
  }
  implicit val ListConsable = new Consable[List] {
    def cons[A](a: A, as: List[A]): List[A] = a :: as
  }
  trait Unconsable[F[_]] {
    def uncons[A](fa: F[A]): Option[(A, F[A])]
  }
  implicit val ListUnconsable = new Unconsable[List] {
    def uncons[A](fa: List[A]): Option[(A, List[A])] =
      fa match {
        case Nil => None
        case a :: as => Some((a, as))
      }
  }

  def build1[F[_]: Consable: Empty]: F[Int] = {
    val C = implicitly[Consable[F]]
    val E = implicitly[Empty[F]]

    C.cons(1, E.empty[Int])
  }

  build1[List] : List[Int]

  case class OptionT[F[_], A](run: F[Option[A]])
  object OptionT {
    def some[F[_]: Applicative, A](a: A): OptionT[F, A] =
      OptionT(Option(a).point[F])

    def none[F[_]: Applicative, A]: OptionT[F, A] =
      OptionT(Option.empty[A].point[F])

    def lift[F[_]: Applicative, A](fa: F[A]): OptionT[F, A] =
      OptionT(fa.map(Option(_)))
  }

  implicit def OptionTMonad[F[_]: Monad] = new Monad[OptionT[F, ?]] {
    def point[A](a: => A): OptionT[F, A] =
      OptionT(Option(a).point[F])

    def bind[A, B](fa: OptionT[F, A])(f: A => OptionT[F,B]): OptionT[F, B] =
      OptionT(fa.run.flatMap {
        case None => Option.empty[B].point[F]
        case Some(a) => f(a).run
      })
  }

  type OptionalIO[A] = OptionT[IO, A]

  val o : OptionT[IO, Int] = OptionT.some(1)
}

object exercise2 {
  import exercise1._

  sealed trait ConsoleF[A]
  case class ReadLine[A](line: String => A) extends ConsoleF[A]
  case class WriteLine[A](line: String, next: A) extends ConsoleF[A]
  case class RandomNumber[A](num: Double => A) extends ConsoleF[A]

  sealed trait Free[F[_], A]
  final case class Point[F[_], A](value: A) extends Free[F, A]
  final case class Effect[F[_], A](effect: F[A]) extends Free[F, A]
  final case class Bind[A0, F[_], A](m: Free[F, A0], f: A0 => Free[F, A]) extends Free[F, A]

  def liftF[F[_], A](fa: F[A]): Free[F, A] = Effect(fa)

  def foldFree[F[_], M[_]: Monad, A](fa: Free[F, A])(f: F ~> M): M[A] =
    fa match {
      case Point(a) => a.point[M]
      case Effect(fa) => f(fa)
      case b : Bind[a0, F, A] =>
        foldFree(b.m)(f).flatMap(a0 =>
          foldFree[F, M, A](b.f(a0))(f)
        )
    }

  implicit def MonadFree[F[_]]: Monad[Free[F, ?]] = new Monad[Free[F, ?]] {
    def point[A](a: => A): Free[F,A] =
      Point[F, A](a)

    def bind[A, B](fa: Free[F,A])(f: A => Free[F,B]): Free[F,B] =
      Bind[A, F, B](fa, f)
  }

  type Console[A] = Free[ConsoleF, A]

  def getStrLn: Console[String] = liftF(ReadLine(a => a))
  def putStrLn(line: String): Console[Unit] = liftF(WriteLine(line, ()))
  def getRandom: Console[Double] = liftF(RandomNumber(a => a))

  val getNumber: Console[Int] =
    for {
      _ <- putStrLn("Guess a number between 0 and 10: ")
      g <- getStrLn
      i <- scala.util.Try(g.toInt).toOption.fold(getNumber)(i => i.point[Console])
    } yield i

  val loop: Console[Unit] =
    for {
      i <- getNumber
      r <- getRandom
      val r2 = (r * 10.5).round.toInt
      _ <- if (i == r2) putStrLn("You're a WINNER!")
           else putStrLn("No, sorry. Try again.").flatMap(_ => loop)
    } yield ()

  val program : Console[String] =
    for {
      _ <- putStrLn("Hello! What is your name?")
      n <- getStrLn
      _ <- if (n == "John") putStrLn("Hello, instructor!")
           else putStrLn("Hello, attendee, " + n + "!")
    } yield n

  val consoleFToIO: ConsoleF ~> IO = new NaturalTransformation[ConsoleF, IO] {
    def apply[A](v: ConsoleF[A]): IO[A] = v match {
      case ReadLine(f) => IO(() => f(readLine()))
      case WriteLine(l, a) => IO(() => { println(l); a })
      case RandomNumber(f) => IO(() => f(scala.util.Random.nextDouble()))
    }
  }

  // foldFree(program)(consoleFToIO).unsafePerformIO()

  sealed trait StateF[S, A]
  case class Gets[S, A](next: S => A) extends StateF[S, A]
  case class Puts[S, A](s: S, next: A) extends StateF[S, A]

  type SafeState[S, A] = Free[StateF[S, ?], A]

  type Trampoline[A] = Free[Function0, A]
}

object exercise3 {
  final case class State[S, A](/* ??? */) {
    def evalState(s: S): A = ???
  }

  implicit def StateMonad[S]: Monad[State[S, ?]] = ???
}

object exercise4 {
  import exercise3._

  def sort[A: Order](list: List[A]): List[A] =
    (??? : State[List[A], List[A]]).evalState(list)
}
