import Foundation
import Fory

/// Namespace + type name shared by every language sample.
private let typeName = "org.feuyeux.fory.UserInfo"

private let langs = [
    "java", "kotlin", "scala", "go", "js", "python", "rust", "csharp", "dart", "cpp", "swift",
]

@ForyStruct
struct UserInfo: Equatable {
    var userId: Int64?
    var name: String?
    // Int32 keeps `age` on the 32-bit wire type, matching every other sample.
    var age: Int32?
    var emails: [String]?
    var properties: [String: String]?
    // Int32 keeps the `scores` elements on the 32-bit wire type as well.
    var scores: [Int32]?
    var active: Bool?
    var createdAt: Int64?
    // Data maps to Fory's BINARY blob. `[UInt8]` would be a dense uint8 array
    // that the other samples cannot read back.
    var avatar: Data?
}

private func createSampleUserInfo() -> UserInfo {
    UserInfo(
        userId: 12345,
        name: "John Doe",
        age: 30,
        emails: ["john.doe@example.com", "johndoe@gmail.com"],
        properties: [
            "city": "San Francisco",
            "country": "USA",
            "role": "Developer",
        ],
        scores: [85, 90, 78, 92],
        active: true,
        createdAt: Int64(Date().timeIntervalSince1970),
        avatar: Data([0, 1, 2, 3, 4, 5])
    )
}

private func describe(_ user: UserInfo) -> String {
    let emails = (user.emails ?? []).joined(separator: ", ")
    let properties = (user.properties ?? [:])
        .sorted { $0.key < $1.key }
        .map { "\($0.key)=\($0.value)" }
        .joined(separator: ", ")
    let scores = (user.scores ?? []).map(String.init).joined(separator: ", ")
    let avatar = (user.avatar ?? Data()).map(String.init).joined(separator: ", ")
    return "UserInfo(userId=\(user.userId.map(String.init) ?? "nil"), "
        + "name=\(user.name ?? "nil"), age=\(user.age.map(String.init) ?? "nil"), "
        + "emails=[\(emails)], properties={\(properties)}, scores=[\(scores)], "
        + "active=\(user.active.map(String.init) ?? "nil"), "
        + "createdAt=\(user.createdAt.map(String.init) ?? "nil"), avatar=[\(avatar)])"
}

// The Swift implementation is xlang-only, so there is no xlang switch.
let fory = Fory(ref: true, compatible: true)
// Register under the shared namespace + type name instead of the Swift type name.
try fory.register(UserInfo.self, name: typeName)

// workspace/ sits next to the language sample directories, so the sample runs
// from its own directory.
let outputDir = URL(fileURLWithPath: "../workspace")
try FileManager.default.createDirectory(at: outputDir, withIntermediateDirectories: true)

let userInfo = createSampleUserInfo()
print("Created user: \(describe(userInfo))")

// Serialize the user to bytes
let serialized = try fory.serialize(userInfo)
let outputFile = outputDir.appendingPathComponent("userinfo_swift.fory")
try serialized.write(to: outputFile)
print("Serialized to \(outputFile.path)")

// Try to read serialized data from other languages
for lang in langs {
    let langFile = outputDir.appendingPathComponent("userinfo_\(lang).fory")
    guard FileManager.default.fileExists(atPath: langFile.path) else {
        continue
    }
    do {
        let fromFile: UserInfo = try fory.deserialize(try Data(contentsOf: langFile))
        print("Deserialized [\(lang)] user info from \(langFile.path):\(describe(fromFile))")
    } catch {
        print("Failed to deserialize [\(lang)]: \(error)")
    }
}
