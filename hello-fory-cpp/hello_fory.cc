#include <chrono>
#include <cstdint>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <map>
#include <string>
#include <vector>

#include "fory/serialization/fory.h"

namespace {

/// Namespace-qualified type name shared by every language sample.
constexpr const char *kTypeName = "org.feuyeux.fory.UserInfo";

const std::vector<std::string> kLangs = {"java",  "kotlin", "scala", "go", "js", "python",
                                        "rust",  "csharp", "dart",  "cpp", "swift"};

}  // namespace

struct UserInfo {
  int64_t user_id = 0;
  std::string name;
  // int32 on the wire, matching every other language sample
  int32_t age = 0;
  std::vector<std::string> emails;
  std::map<std::string, std::string> properties;
  // Declared as a list<int32> below; a bare std::vector<int32_t> would become a
  // dense int32 array, which the other samples read as garbage or reject.
  std::vector<int32_t> scores;
  bool active = false;
  int64_t created_at = 0;
  // std::vector<uint8_t> maps to Fory's BINARY blob
  std::vector<uint8_t> avatar;

  FORY_STRUCT(UserInfo, user_id, name, age, emails, properties,
              (scores, fory::F().list(fory::T::int32())), active, created_at, avatar);
};

namespace {

std::string ToString(const UserInfo &info) {
  std::string out = "UserInfo(userId=" + std::to_string(info.user_id) + ", name=" + info.name +
                    ", age=" + std::to_string(info.age) + ", emails=[";
  for (size_t i = 0; i < info.emails.size(); ++i) {
    out += (i ? ", " : "") + info.emails[i];
  }
  out += "], properties={";
  bool first = true;
  for (const auto &kv : info.properties) {
    out += (first ? "" : ", ") + kv.first + "=" + kv.second;
    first = false;
  }
  out += "}, scores=[";
  for (size_t i = 0; i < info.scores.size(); ++i) {
    out += (i ? ", " : "") + std::to_string(info.scores[i]);
  }
  out += "], active=" + std::string(info.active ? "true" : "false") +
         ", createdAt=" + std::to_string(info.created_at) + ", avatar=[";
  for (size_t i = 0; i < info.avatar.size(); ++i) {
    out += (i ? ", " : "") + std::to_string(static_cast<int>(info.avatar[i]));
  }
  out += "])";
  return out;
}

UserInfo CreateSampleUserInfo() {
  UserInfo info;
  info.user_id = 12345;
  info.name = "John Doe";
  info.age = 30;
  info.emails = {"john.doe@example.com", "johndoe@gmail.com"};
  info.properties = {{"city", "San Francisco"}, {"country", "USA"}, {"role", "Developer"}};
  info.scores = {85, 90, 78, 92};
  info.active = true;
  info.created_at = std::chrono::duration_cast<std::chrono::seconds>(
                        std::chrono::system_clock::now().time_since_epoch())
                        .count();
  info.avatar = {0, 1, 2, 3, 4, 5};
  return info;
}

}  // namespace

int main() {
  // Create output directory if it doesn't exist
  const std::filesystem::path output_dir = "../workspace";
  std::filesystem::create_directories(output_dir);

  // Create a sample UserInfo object
  const UserInfo user_info = CreateSampleUserInfo();
  std::cout << "Created user: " << ToString(user_info) << std::endl;

  // Create fory instance configured for cross-language compatibility
  auto fory =
      fory::serialization::Fory::builder().xlang(true).track_ref(true).compatible(true).build();
  // Register under the shared namespace-qualified name instead of the C++ type name.
  if (auto registered = fory.register_struct<UserInfo>(kTypeName); !registered.ok()) {
    std::cerr << "Failed to register type: " << registered.error().message() << std::endl;
    return 1;
  }

  // Serialize the user to bytes
  auto serialized = fory.serialize(user_info);
  if (!serialized.ok()) {
    std::cerr << "Failed to serialize: " << serialized.error().message() << std::endl;
    return 1;
  }

  // Write the serialized data to a file
  const std::filesystem::path output_file = output_dir / "userinfo_cpp.fory";
  {
    std::ofstream out(output_file, std::ios::binary);
    out.write(reinterpret_cast<const char *>(serialized.value().data()),
              static_cast<std::streamsize>(serialized.value().size()));
  }
  std::cout << "Serialized to " << output_file.string() << std::endl;

  // Try to read serialized data from other languages
  for (const std::string &lang : kLangs) {
    const std::filesystem::path lang_file = output_dir / ("userinfo_" + lang + ".fory");
    if (!std::filesystem::exists(lang_file)) {
      continue;
    }
    std::ifstream in(lang_file, std::ios::binary);
    const std::vector<uint8_t> bytes((std::istreambuf_iterator<char>(in)),
                                     std::istreambuf_iterator<char>());
    auto decoded = fory.deserialize<UserInfo>(bytes);
    if (decoded.ok()) {
      std::cout << "Deserialized [" << lang << "] user info from " << lang_file.string() << ":"
                << ToString(decoded.value()) << std::endl;
    } else {
      std::cout << "Failed to deserialize [" << lang << "]: " << decoded.error().message()
                << std::endl;
    }
  }
  return 0;
}
