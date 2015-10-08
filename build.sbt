name := "olympus-streaming"
version := "1.0"
scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.4.1",
    "org.apache.spark" %% "spark-streaming" % "1.4.1",
    "org.apache.spark" %% "spark-streaming-kafka" % "1.4.1",
    "org.apache.kafka" %% "kafka" % "0.8.2.0" 
)

/*
 * This chooses a "merge strategy" based on information about the file.
 * There may be multiple files with the same name, to mash them all into one
 * jar you need to do some careful merging
 */
assemblyMergeStrategy in assembly := {
  /* Config files can be concatenated */
  case file if Assembly.isConfigFile(file) =>
    MergeStrategy.concat
    
  /* If it's a Readme in any directory, rename it */
  case PathList(pathlist @ _*) if Assembly.isReadme(pathlist.last) => 
    MergeStrategy.rename
    
  /* If the file is a license, rename it */
  case PathList(pathlist @ _*) if Assembly.isLicenseFile(pathlist.last) =>
    MergeStrategy.rename

  /* If the top directory is `META-INF` */
  case PathList("META-INF", pathlist @ _*) =>
    (pathlist.map(_.toLowerCase)) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) => MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
 
  /* 
   * All other files just choose the first one 
   * Note: This could cause problems in the future
   */
  case _ => MergeStrategy.first
}

