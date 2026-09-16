package org.feuyeux.fory

import java.nio.file.{Files, Paths}
import java.util.{HashMap => JHashMap, List => JList, Map => JMap}
import org.apache.fory.scala.ForyScala
import scala.jdk.CollectionConverters.*

/** Cross-language sample: writes one payload and reads back every other language's payload. */
object HelloFory:
  /** Namespace + type name shared by every language sample. */
  private val Namespace = "org.feuyeux.fory"
  private val TypeName = "UserInfo"

  private val Langs =
    List("java", "kotlin", "scala", "go", "js", "python", "rust", "csharp", "dart", "cpp", "swift")

  /** Uses java.util collections so the xlang wire types match the other samples. */
  class UserInfo:
    var userId: java.lang.Long = null
    var name: String = null
    var age: java.lang.Integer = null
    var emails: JList[String] = null
    var properties: JMap[String, String] = null
    var scores: JList[java.lang.Integer] = null
    var active: java.lang.Boolean = null
    var createdAt: java.lang.Long = null
    var avatar: Array[Byte] = null

    override def toString: String =
      s"UserInfo(userId=$userId, name=$name, age=$age, emails=$emails, properties=$properties, " +
        s"scores=$scores, active=$active, createdAt=$createdAt, avatar=${
            if avatar == null then null else avatar.mkString("[", ", ", "]")
          })"

  private def createSampleUserInfo(): UserInfo =
    val info = new UserInfo
    info.userId = 12345L
    info.name = "John Doe"
    info.age = 30
    info.emails = List("john.doe@example.com", "johndoe@gmail.com").asJava
    val props = new JHashMap[String, String]
    props.put("city", "San Francisco")
    props.put("country", "USA")
    props.put("role", "Developer")
    info.properties = props
    info.scores = List[java.lang.Integer](85, 90, 78, 92).asJava
    info.active = true
    info.createdAt = System.currentTimeMillis() / 1000
    info.avatar = Array[Byte](0, 1, 2, 3, 4, 5)
    info

  def main(args: Array[String]): Unit =
    // Create output directory if it doesn't exist
    val outputDir = Paths.get("../workspace")
    Files.createDirectories(outputDir)

    // Create a sample UserInfo object
    val userInfo = createSampleUserInfo()
    println(s"Created user: $userInfo")

    // Create fory instance configured for cross-language compatibility
    val fory = ForyScala
      .builder()
      .withXlang(true)
      .withRefTracking(true)
      .withCompatible(true)
      .build()
    // Register under the shared namespace + type name instead of the Scala package name.
    fory.register(classOf[UserInfo], Namespace, TypeName)

    // Serialize the user to bytes
    val serialized = fory.serialize(userInfo)

    // Write the serialized data to a file
    val outputFile = outputDir.resolve("userinfo_scala.fory")
    Files.write(outputFile, serialized)
    println(s"Serialized to $outputFile")

    // Try to read serialized data from other languages
    for lang <- Langs do
      val langFile = outputDir.resolve(s"userinfo_$lang.fory")
      if Files.exists(langFile) then
        try
          val userInfoFromFile = fory.deserialize(Files.readAllBytes(langFile), classOf[UserInfo])
          println(s"Deserialized [$lang] user info from $langFile:$userInfoFromFile")
        catch case e: Exception => println(s"Failed to deserialize [$lang]: ${e.getMessage}")
