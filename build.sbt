import sbt._

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.3"

val monocleVersion = "1.5.0"

val scalazVersion = "7.2.23"

val matryoshkaVersion = "0.18.3"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-state"   % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-refined" % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-law"     % monocleVersion % "test",
  "org.scalaz"                  %%  "scalaz-core"     % scalazVersion,
  "org.scalaz"                  %%  "scalaz-concurrent" % scalazVersion,
  "com.slamdata"                %%  "matryoshka-core" % matryoshkaVersion
)

// for @Lenses macro support
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)

// For kind projector, `Either[String, ?]`
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")
