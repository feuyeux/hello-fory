using Apache.Fory;

namespace Org.Feuyeux.HelloFory;

[ForyStruct]
public sealed class UserInfo
{
    public long? UserId { get; set; }

    public string? Name { get; set; }

    // int32 on the wire, matching every other language sample
    public int? Age { get; set; }

    public List<string>? Emails { get; set; }

    public Dictionary<string, string>? Properties { get; set; }

    public List<int>? Scores { get; set; }

    public bool? Active { get; set; }

    public long? CreatedAt { get; set; }

    // byte[] maps to Fory's BINARY blob
    public byte[]? Avatar { get; set; }

    public override string ToString() =>
        $"UserInfo(userId={UserId}, name={Name}, age={Age}, "
        + $"emails=[{string.Join(", ", Emails ?? new List<string>())}], "
        + $"properties={{{string.Join(", ", (Properties ?? new Dictionary<string, string>()).Select(kv => $"{kv.Key}={kv.Value}"))}}}, "
        + $"scores=[{string.Join(", ", Scores ?? new List<int>())}], "
        + $"active={Active}, createdAt={CreatedAt}, "
        + $"avatar=[{string.Join(", ", Avatar ?? Array.Empty<byte>())}])";
}

public static class Program
{
    /// <summary>Namespace + type name shared by every language sample.</summary>
    private const string Namespace = "org.feuyeux.fory";

    private const string TypeName = "UserInfo";

    private static readonly string[] Langs =
    [
        "java", "kotlin", "scala", "go", "js", "python", "rust", "csharp", "dart", "cpp"
    ];

    public static int Main()
    {
        // Create output directory if it doesn't exist
        string outputDir = Path.Combine("..", "workspace");
        Directory.CreateDirectory(outputDir);

        // Create a sample UserInfo object
        UserInfo userInfo = CreateSampleUserInfo();
        Console.WriteLine($"Created user: {userInfo}");

        // Create fory instance configured for cross-language compatibility
        // (the C# implementation is xlang-only, so there is no Xlang switch)
        Fory fory = Fory.Builder().TrackRef(true).Compatible(true).Build();
        // Register under the shared namespace + type name instead of the CLR type name.
        fory.Register<UserInfo>(Namespace, TypeName);

        // Serialize the user to bytes
        byte[] serialized = fory.Serialize(userInfo);

        // Write the serialized data to a file
        string outputFile = Path.Combine(outputDir, "userinfo_csharp.fory");
        File.WriteAllBytes(outputFile, serialized);
        Console.WriteLine($"Serialized to {outputFile}");

        // Try to read serialized data from other languages
        foreach (string lang in Langs)
        {
            string langFile = Path.Combine(outputDir, $"userinfo_{lang}.fory");
            if (!File.Exists(langFile))
            {
                continue;
            }

            try
            {
                UserInfo? fromFile = fory.Deserialize<UserInfo>(File.ReadAllBytes(langFile));
                Console.WriteLine($"Deserialized [{lang}] user info from {langFile}:{fromFile}");
            }
            catch (Exception e)
            {
                Console.WriteLine($"Failed to deserialize [{lang}]: {e.Message}");
            }
        }

        return 0;
    }

    private static UserInfo CreateSampleUserInfo() => new()
    {
        UserId = 12345L,
        Name = "John Doe",
        Age = 30,
        Emails = ["john.doe@example.com", "johndoe@gmail.com"],
        Properties = new Dictionary<string, string>
        {
            ["city"] = "San Francisco",
            ["country"] = "USA",
            ["role"] = "Developer",
        },
        Scores = [85, 90, 78, 92],
        Active = true,
        CreatedAt = DateTimeOffset.UtcNow.ToUnixTimeSeconds(),
        Avatar = [0, 1, 2, 3, 4, 5],
    };
}
