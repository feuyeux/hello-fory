package org.feuyeux.io;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.fory.Fory;
import org.apache.fory.logging.LoggerFactory;

@Slf4j
public class HelloFory {
  /** Namespace shared by every language sample so the payloads stay interchangeable. */
  private static final String NAMESPACE = "org.feuyeux.fory";

  private static final String TYPE_NAME = "UserInfo";

  private static final Path OUTPUT_DIR = Paths.get("../workspace");

  private static final List<String> LANGS =
      List.of("java", "kotlin", "scala", "go", "js", "python", "rust", "csharp", "dart", "cpp");

  public static void main(String[] args) throws IOException {
    // Enable SLF4J logging for Fory (same as Fory uses internally)
    LoggerFactory.useSlf4jLogging(true);

    // Create output directory if it doesn't exist
    Files.createDirectories(OUTPUT_DIR);

    // Create a sample UserInfo object
    UserInfo userInfo = createSampleUserInfo();
    log.info("Created user: {}", userInfo);
    // Create fory instance configured for cross-language compatibility
    Fory fory = Fory.builder().withXlang(true).withRefTracking(true).withCompatible(true).build();
    // Register under an explicit namespace + type name instead of the Java package name,
    // otherwise the other languages cannot resolve the type.
    fory.register(UserInfo.class, NAMESPACE, TYPE_NAME);

    // Serialize the user to bytes
    byte[] serialized = fory.serialize(userInfo);

    // Write the serialized data to a file
    Path outputFile = OUTPUT_DIR.resolve("userinfo_java.fory");
    Files.write(outputFile, serialized);
    log.info("Serialized to {}", outputFile);

    // Try to read serialized data from other languages
    for (String lang : LANGS) {
      Path langFile = OUTPUT_DIR.resolve("userinfo_" + lang + ".fory");
      if (Files.exists(langFile)) {
        try {
          UserInfo userInfoFromFile = deserializeUser(fory, langFile);
          log.info("Deserialized [{}] user info from {}:{}", lang, langFile, userInfoFromFile);
        } catch (Exception e) {
          log.error("Failed to deserialize [{}]", lang, e);
        }
      }
    }
  }

  private static UserInfo createSampleUserInfo() {
    UserInfo info = new UserInfo();
    info.setUserId(12345L);
    info.setName("John Doe");
    info.setAge(30);
    info.setEmails(Arrays.asList("john.doe@example.com", "johndoe@gmail.com"));

    HashMap<String, String> props = new HashMap<>();
    props.put("city", "San Francisco");
    props.put("country", "USA");
    props.put("role", "Developer");
    info.setProperties(props);

    info.setScores(new ArrayList<>(Arrays.asList(85, 90, 78, 92)));
    info.setActive(true);
    info.setCreatedAt(System.currentTimeMillis() / 1000);
    info.setAvatar(new byte[] {0, 1, 2, 3, 4, 5});

    return info;
  }

  private static UserInfo deserializeUser(Fory fory, Path filePath) throws IOException {
    byte[] serialized = Files.readAllBytes(filePath);
    return fory.deserialize(serialized, UserInfo.class);
  }
}

@Data
class UserInfo implements Serializable {
  private Long userId;
  private String name;
  private Integer age;
  private List<String> emails;
  private Map<String, String> properties;
  private List<Integer> scores;
  private Boolean active;
  private Long createdAt;
  private byte[] avatar;
}
