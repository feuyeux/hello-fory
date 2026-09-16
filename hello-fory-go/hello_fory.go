package main

import (
	"fmt"
	"os"
	"path/filepath"
	"time"

	fory "github.com/apache/fory/go/fory"
)

// typeName is the namespace-qualified type name shared by every language sample.
const typeName = "org.feuyeux.fory.UserInfo"

// langs lists the samples whose payloads we try to read back.
var langs = []string{"java", "kotlin", "scala", "go", "js", "python", "rust", "csharp", "dart", "cpp", "swift"}

type UserInfo struct {
	UserId     int64
	Name       string
	Age        int32
	Emails     []string
	Properties map[string]string
	Scores     []int32
	Active     bool
	CreatedAt  int64
	// `type=bytes` forces Fory's BINARY blob schema. Without it a []byte struct
	// field is written as a dense uint8 array, which the Java/Python/JS/Rust
	// samples cannot read back.
	Avatar []byte `fory:"type=bytes"`
}

func createSampleUserInfo() *UserInfo {
	return &UserInfo{
		UserId: 12345,
		Name:   "John Doe",
		Age:    30,
		Emails: []string{"john.doe@example.com", "johndoe@gmail.com"},
		Properties: map[string]string{
			"city":    "San Francisco",
			"country": "USA",
			"role":    "Developer",
		},
		Scores:    []int32{85, 90, 78, 92},
		Active:    true,
		CreatedAt: time.Now().Unix(),
		Avatar:    []byte{0, 1, 2, 3, 4, 5},
	}
}

func deserializeUser(f *fory.Fory, filePath string) (*UserInfo, error) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Printf("Recovered from panic: %v\n", r)
		}
	}()

	serialized, err := os.ReadFile(filePath)
	if err != nil {
		return nil, err
	}

	var userInfo UserInfo
	if err := f.Unmarshal(serialized, &userInfo); err != nil {
		return nil, err
	}

	return &userInfo, nil
}

func main() {
	// Create output directory if it doesn't exist
	outputDir := "../workspace"
	if err := os.MkdirAll(outputDir, 0755); err != nil {
		fmt.Printf("Failed to create output directory: %v\n", err)
		os.Exit(1)
	}

	// Create a sample UserInfo object
	userInfo := createSampleUserInfo()
	fmt.Printf("Created user: %+v\n", userInfo)

	// Create fory instance configured for cross-language compatibility
	f := fory.NewFory(
		fory.WithXlang(true),
		fory.WithRefTracking(true),
		fory.WithCompatible(true),
	)
	if err := f.RegisterStructByName(UserInfo{}, typeName); err != nil {
		fmt.Printf("Failed to register type: %v\n", err)
		os.Exit(1)
	}

	// Serialize the user to bytes
	serialized, err := f.Marshal(userInfo)
	if err != nil {
		fmt.Printf("Failed to serialize: %v\n", err)
		os.Exit(1)
	}

	// Write the serialized data to a file
	outputFile := filepath.Join(outputDir, "userinfo_go.fory")
	if err := os.WriteFile(outputFile, serialized, 0644); err != nil {
		fmt.Printf("Failed to write file: %v\n", err)
		os.Exit(1)
	}
	fmt.Printf("Serialized to %s\n", outputFile)

	// Try to read serialized data from other languages
	for _, lang := range langs {
		langFile := filepath.Join(outputDir, fmt.Sprintf("userinfo_%s.fory", lang))
		if _, err := os.Stat(langFile); err == nil {
			if userInfoFromFile, err := deserializeUser(f, langFile); err != nil {
				fmt.Printf("Failed to deserialize [%s]: %v\n", lang, err)
			} else {
				fmt.Printf("Deserialized [%s] user info from %s:%+v\n", lang, langFile, userInfoFromFile)
			}
		}
	}
}
