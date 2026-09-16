use fory::{Error, Fory, ForyStruct};
use std::collections::HashMap;
use std::fs;
use std::path::Path;
use std::time::{SystemTime, UNIX_EPOCH};

/// Namespace + type name shared by every language sample.
const TYPE_NAME: &str = "org.feuyeux.fory.UserInfo";

const LANGS: [&str; 11] = [
    "java", "kotlin", "scala", "go", "js", "python", "rust", "csharp", "dart", "cpp", "swift",
];

#[derive(ForyStruct, Debug, Clone)]
struct UserInfo {
    user_id: Option<i64>,
    name: Option<String>,
    age: Option<i32>,
    emails: Option<Vec<String>>,
    properties: Option<HashMap<String, String>>,
    scores: Option<Vec<i32>>,
    active: Option<bool>,
    created_at: Option<i64>,
    // `#[fory(bytes)]` selects Fory's BINARY blob schema. A bare `Vec<u8>` would be
    // serialized as a dense uint8 array, which Java `byte[]`, Python `bytes` and
    // JS `Type.binary()` cannot read back.
    #[fory(bytes)]
    avatar: Option<Vec<u8>>,
}

fn create_sample_user_info() -> UserInfo {
    let mut properties = HashMap::new();
    properties.insert("city".to_string(), "San Francisco".to_string());
    properties.insert("country".to_string(), "USA".to_string());
    properties.insert("role".to_string(), "Developer".to_string());

    let created_at = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_secs() as i64;

    UserInfo {
        user_id: Some(12345),
        name: Some("John Doe".to_string()),
        age: Some(30),
        emails: Some(vec![
            "john.doe@example.com".to_string(),
            "johndoe@gmail.com".to_string(),
        ]),
        properties: Some(properties),
        scores: Some(vec![85, 90, 78, 92]),
        active: Some(true),
        created_at: Some(created_at),
        avatar: Some(vec![0u8, 1, 2, 3, 4, 5]),
    }
}

fn deserialize_user(fory: &Fory, file_path: &Path) -> Result<UserInfo, Error> {
    let serialized = fs::read(file_path).expect("Failed to read file");
    let result = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
        fory.deserialize::<UserInfo>(&serialized)
    }));

    match result {
        Ok(Ok(user_info)) => Ok(user_info),
        Ok(Err(e)) => Err(e),
        Err(_) => Err(Error::InvalidData("Panic during deserialization".into())),
    }
}

fn main() -> Result<(), Error> {
    // Create output directory if it doesn't exist
    let output_dir = Path::new("../workspace");
    fs::create_dir_all(output_dir).expect("Failed to create output directory");

    // Create a sample UserInfo object
    let user_info = create_sample_user_info();
    println!("Created user: {:?}", user_info);

    // Create fory instance configured for cross-language compatibility
    let mut fory = Fory::builder()
        .xlang(true)
        .compatible(true)
        .track_ref(true)
        .build();
    fory.register_by_name::<UserInfo>(TYPE_NAME)?;

    // Serialize the user to bytes
    let serialized = fory.serialize(&user_info)?;

    // Write the serialized data to a file
    let output_file = output_dir.join("userinfo_rust.fory");
    fs::write(&output_file, &serialized).expect("Failed to write file");
    println!("Serialized to {:?}", output_file);

    // Try to read serialized data from other languages
    for lang in &LANGS {
        let lang_file = output_dir.join(format!("userinfo_{}.fory", lang));
        if lang_file.exists() {
            match deserialize_user(&fory, &lang_file) {
                Ok(user_info_from_file) => {
                    println!(
                        "Deserialized [{}] user info from {:?}:{:?}",
                        lang, lang_file, user_info_from_file
                    );
                }
                Err(e) => println!("Failed to deserialize [{}]: {:?}", lang, e),
            }
        }
    }

    Ok(())
}
