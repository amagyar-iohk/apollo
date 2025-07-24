// swift-tools-version:5.7
import PackageDescription

let package = Package(
    name: "ApolloLibrary",
    platforms: [
        .iOS(.v13),
        .macOS(.v11)
    ],
    products: [
        .library(
            name: "ApolloLibrary",
            targets: ["ApolloBinary"]
        ),
    ],

    targets: [
        // LOCAL
//          .binaryTarget(
//              name: "ApolloBinary",
//              path: "./apollo/build/packages/ApolloSwift/ApolloBinary.xcframework.zip"
//          ),

        // RELEASE
       .binaryTarget(
           name: "ApolloBinary",
           url: "https://github.com/hyperledger-identus/apollo/releases/download/v2.3.0/ApolloLibrary.xcframework.zip",
           checksum: "282ec34051bfc2daf9ca70754e2a13532c70e0531f054f22acc1753ba8065f0b"
       )
    ]
)
