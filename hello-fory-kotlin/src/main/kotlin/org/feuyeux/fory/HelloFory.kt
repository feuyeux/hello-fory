package org.feuyeux.fory

import java.nio.file.Files
import java.nio.file.Paths
import org.apache.fory.annotation.ForyStruct
import org.apache.fory.kotlin.ForyKotlin
import org.apache.fory.kotlin.register

/** Namespace-qualified type name shared by every language sample. */
private const val TYPE_NAME = "org.feuyeux.fory.UserInfo"

private val LANGS =
  listOf("java", "kotlin", "scala", "go", "js", "python", "rust", "csharp", "dart", "cpp", "swift")

/**
 * `@ForyStruct` makes fory-kotlin-ksp generate the static xlang serializer at build time; the
 * Kotlin runtime refuses reflective xlang structs.
 *
 * The fields are primary-constructor properties so they keep name-based wire identity like the
 * other samples (mutable properties would require explicit `@ForyField` ids).
 */
@ForyStruct
data class UserInfo(
  val userId: Long = 0L,
  val name: String = "",
  // Kotlin Int is int32 on the wire, matching every other language sample
  val age: Int = 0,
  val emails: List<String> = emptyList(),
  val properties: Map<String, String> = emptyMap(),
  val scores: List<Int> = emptyList(),
  val active: Boolean = false,
  val createdAt: Long = 0L,
  // A plain ByteArray is a BINARY blob; @ArrayType would make it a dense uint8 array
  val avatar: ByteArray = ByteArray(0),
) {
  override fun toString(): String =
    "UserInfo(userId=$userId, name=$name, age=$age, emails=$emails, properties=$properties, " +
      "scores=$scores, active=$active, createdAt=$createdAt, avatar=${avatar.contentToString()})"
}

private fun createSampleUserInfo() =
  UserInfo(
    userId = 12345L,
    name = "John Doe",
    age = 30,
    emails = listOf("john.doe@example.com", "johndoe@gmail.com"),
    properties = mapOf("city" to "San Francisco", "country" to "USA", "role" to "Developer"),
    scores = listOf(85, 90, 78, 92),
    active = true,
    createdAt = System.currentTimeMillis() / 1000,
    avatar = byteArrayOf(0, 1, 2, 3, 4, 5),
  )

fun main() {
  // Create output directory if it doesn't exist
  val outputDir = Paths.get("../workspace")
  Files.createDirectories(outputDir)

  // Create a sample UserInfo object
  val userInfo = createSampleUserInfo()
  println("Created user: $userInfo")

  // Create fory instance configured for cross-language compatibility
  val fory =
    ForyKotlin.builder().withXlang(true).withRefTracking(true).withCompatible(true).build()
  // Register under the shared namespace-qualified name instead of the Kotlin package name.
  fory.register<UserInfo>(TYPE_NAME)

  // Serialize the user to bytes
  val serialized = fory.serialize(userInfo)

  // Write the serialized data to a file
  val outputFile = outputDir.resolve("userinfo_kotlin.fory")
  Files.write(outputFile, serialized)
  println("Serialized to $outputFile")

  // Try to read serialized data from other languages
  for (lang in LANGS) {
    val langFile = outputDir.resolve("userinfo_$lang.fory")
    if (Files.exists(langFile)) {
      try {
        val fromFile = fory.deserialize(Files.readAllBytes(langFile), UserInfo::class.java)
        println("Deserialized [$lang] user info from $langFile:$fromFile")
      } catch (e: Exception) {
        println("Failed to deserialize [$lang]: ${e.message}")
      }
    }
  }
}
