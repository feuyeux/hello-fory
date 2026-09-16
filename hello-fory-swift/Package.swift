// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "hello-fory-swift",
    platforms: [
        .macOS(.v13)
    ],
    dependencies: [
        .package(url: "https://github.com/apache/fory.git", exact: "1.7.2")
    ],
    targets: [
        .executableTarget(
            name: "HelloFory",
            dependencies: [
                .product(name: "Fory", package: "fory")
            ],
            path: "Sources/HelloFory"
        )
    ]
)
