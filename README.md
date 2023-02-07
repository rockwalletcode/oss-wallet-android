<br>
RockWallet is the best way to get started with bitcoin. 
<br>
<br>

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/rockwalletcode/wallet-android/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/rockwalletcode/wallet-android/tree/main)

Our simple, streamlined design is easy for beginners, yet powerful enough for experienced users.

### Cutting-edge security

**RockWallet** utilizes the latest mobile security features to protect users from malware, browser security holes, and even physical theft.
On Android The user’s private key is encrypted using the Android Keystore, inaccessible to anyone other than the user.
On iOS the user’s private key is stored in the device keychain, secured by Secure Enclave, inaccessible to anyone other than the user.
Users are also able to backup their wallet using iCloud Keychain to store an encrypted backup of their recovery phrase.
The backup is encrypted with the RockWallet app PIN.


RockWallet is the best way to get started with bitcoin.
Our simple, streamlined design is easy for beginners, yet powerful enough for experienced users.

### Your Decentralized Bitcoin Wallet

Unlike other mobile bitcoin wallets, **RockWallet** users have the option to disable Fastsync converting the wallet into a standalone bitcoin client.
It connects directly to the bitcoin network using [SPV](https://en.bitcoin.it/wiki/Thin_Client_Security#Header-Only_Clients) mode, and doesn't rely on servers that can be hacked or disabled.
If BRD the company disappears, your private key can still be derived from the recovery phrase to recover your funds since your funds exist on the blockchain.

### Cutting-edge security

**BRD** utilizes the latest mobile security features to protect users from malware, browser security holes, and even physical theft.
On Android The user’s private key is encrypted using the Android Keystore, inaccessible to anyone other than the user.
On iOS the user’s private key is stored in the device keychain, secured by Secure Enclave, inaccessible to anyone other than the user.
Users are also able to backup their wallet using iCloud Keychain to store an encrypted backup of their recovery phrase.
The backup is encrypted with the RockWallet app PIN.

### Designed with New Users in Mind

Simplicity and ease-of-use is **RockWallet**'s core design principle. A simple recovery phrase (which we call a recovery key) is all that is needed to restore the user's wallet if they ever lose or replace their device. **RockWallet** is [deterministic](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki), which means the user's balance and transaction history can be recovered just from the recovery key.

### Features

- Supports wallets for Bitcoin, Bitcoin Cash, Ethereum and ERC-20 tokens, Ripple, Hedera, Tezos
- Single recovery key is all that's needed to backup your wallet
- Private keys never leave your device and are end-to-end encrypted when using iCloud backup
- Save a memo for each transaction (off-chain)

### Bitcoin Specific Features
- Supports importing [password protected](https://github.com/bitcoin/bips/blob/master/bip-0038.mediawiki) paper wallets
- Supports [JSON payment protocol](https://bitpay.com/docs/payment-protocol)
- Supports SegWit and bech32 addresses

### Localization

**RockWallet** is available in the following languages:

- Chinese (Simplified and traditional)
- Danish
- Dutch
- English
- French
- German
- Italian
- Japanese
- Korean
- Portuguese
- Russian
- Spanish
- Swedish

## About brd-mobile

This repository is the RockWallet Mobile monorepo for iOS and Android, powered by a collection of Kotlin Multiplatform Mobile ([KMM](https://kotlinlang.org/lp/mobile/)) modules codenamed Cosmos.

Cosmos breaks down into many modules that are bundled to produce a final Jar/AAR and Framework for mobile projects.
Each module contains only code related to a single feature, helping keep the project organized and improve incremental build times.

## Modules

The following modules are available, click on the name to learn more.

- [`cosmos-core`](/cosmos-core) Internal shared utilities for all other modules to leverage.
- [`cosmos-brd-api-client`](/cosmos-brd-api-client) A Hydra compatible API wrapper for Kotlin and Swift.
- [`cosmos-bundled`](/cosmos-bundled) Depends on all other modules to produce final dependency artifacts.

**Mobile Applications**

- [`brd-android`](/brd-android) A collection of gradle modules to build RockWallet Android.

## Development

### Prerequisites

- Install [OpenJDK 8+](https://adoptopenjdk.net/installation.html?variant=openjdk8)
- Download [Intellij IDEA](https://www.jetbrains.com/idea/) or [Android Studio](https://developer.android.com/studio/)

### Setup

1. Clone this repository `git clone git@github.com:breadwallet/brd-mobile.git --recurse-submodules`
2. (Optional, for RockWallet employees) checkout production resources: `git submodule update --checkout`
3. Open the `Cosmos` folder using Intellij IDEA or Android Studio
4. (iOS Development) Open the `brd-ios/breadwallet.xcworkspace` file in xcode

## Advanced Setup

### Blockset Client Token

(Android) The [Blockset client token](https://blockset.com/docs/v1/tools/authentication) can be set in [gradle.properties](gradle.properties) or by using `-PBDB_CLIENT_TOKEN="<client token>"`.
A default token is available for testing.

### (Android) Firebase

To enable Firebase services like Crashlytics, add the `google-services.json` file into the `brd-android/app` directory.
Without this file, runtime Firebase dependencies are still used but do not start and the Google Services gradle plugin is disabled so builds will succeed.


## Gradle Tasks

Here is a list of the most useful gradle tasks available.
For a comprehensive list of tasks run `./gradlew tasks` or `./gradlew :<module-name>:tasks`.


Build
```shell
# Build, test, and package all modules
./gradlew build
# Run all quality checks
./gradlew check
# Assemble RockWallet Android
./gradlew brd-android:app:assemble
```

Tests
```shell
# Run all tests, in all modules
./gradlew allTest
# Run all tests, in a single module
./gradlew :cosmos-brd-api-client:allTest
# Run Jvm tests
./gradlew jvmTest
# Run iOS Simulator tests
./gradlew iosX64Test
```

Packaging
```shell
# Package Jvm artifacts
./gradlew jvmJar
# Package iOS Frameworks (Simulator)
./gradlew linkDebugFrameworkIosX64 linkReleaseFrameworkIosX64
# Package iOS Frameworks (Device)
./gradlew linkDebugFrameworkIosArm64 linkReleaseFrameworkIosArm64
```

## Git History

This repository merges the commit history of [BRD iOS](https://github.com/breadwallet/breadwallet-ios) and [BRD Android](https://github.com/breadwallet/breadwallet-android).
To build RockWallet application versions before 4.10, please refer to the respective legacy git repository tags.

A light clone of this repository can be created with `git clone git@github.com:breadwallet/brd-mobile.git --depth 50`

### WARNING:

***Installation on jailbroken devices is strongly discouraged.***

Any jailbreak app can grant itself access to every other app's keychain data. This means it can access your wallet and steal your bitcoin by self-signing as described [here](http://www.saurik.com/id/8) and including `<key>application-identifier</key><string>*</string>` in its .entitlements file.

---

**RockWallet** is open source and available under the terms of the MIT license.

Source code is available at https://github.com/breadwallet
