[![](https://jitpack.io/v/io.samourai.code.wallet/ExtLibJ.svg)](https://jitpack.io/#io.samourai.code.wallet/ExtLibJ)

# ExtLibJ
BitcoinJ extensions: BIP39/44/47/49/84/86/340, bech32, bech32m, PSBT (BIP174).

Samourai toolkit.

See [documentation](README-DOC.md)

<img src="./doc/resources/diagram.png" alt="stack diagram"/>


## Build
Build with Maven:
```
mvn clean install -Dmaven.test.skip=true
```

Build & Run JUnit Tests with Maven:
```
mvn clean install
```


## Run JUnit Tests
Run JUnit Tests with Maven:
```
mvn clean test
```

Run JUnit Test Class with Maven:
```
mvn clean test -Dtest=xxxxTest
```

Run JUnit Test Classes with Maven:
```
mvn clean test -Dtest=xxxxTest1,xxxxTest2
```

Run JUnit Test Method with Maven:
```
mvn clean test -Dtest=xxxxTest#testA
```

Run JUnit Test Methods with Maven:
```
mvn clean test -Dtest=xxxxTest#testA+testB
```


## Features

### BIP39:

Samourai implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki), extends [bitcoinj](https://bitcoinj.github.io/).

### BIP44:

Samourai implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki), extends [bitcoinj](https://bitcoinj.github.io/).

### BIP47:

Samourai implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0047.mediawiki) by Justus Ranvier. Extends BIP44 implementation (above). Further modifications have been made to incorporate Segwit addresses into BIP47.

[BIP47 Source Code](https://code.samourai.io/wallet/ExtLibJ/-/tree/develop/java/com/samourai/wallet/bip47)

[BIP47 Tests](https://gist.github.com/SamouraiDev/6aad669604c5930864bd)

### BIP49 (Segwit):

Samourai P2SH-P2WPKH implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0049.mediawiki) by Daniel Weigl and includes support for BIP49-specific XPUBs: [YPUB](https://github.com/Samourai-Wallet/sentinel-android/issues/16).

### BIP69:

Samourai implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0069.mediawiki) by Kristov Atlas.

### BIP84 (Segwit):

Samourai implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0084.mediawiki) by Pavol Rusnak.

### BIP125 (Replace-by-fee, RBF):

Samourai implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0125.mediawiki) by David A. Harding and Peter Todd.

### BIP141 (Segwit):

Samourai spends to bech32 addresses P2WPKH based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0141.mediawiki) by Eric Lombrozo, Johnson Lau and Pieter Wuille.

### BIP173 (Segwit):

Samourai implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki) by Pieter Wuille and Greg Maxwell.

### BIP174 (Partially Signed Bitcoin Transaction Format, PSBT):

Samourai signing via PSBT implementation based on [original BIP](https://github.com/bitcoin/bips/blob/master/bip-0174.mediawiki) by  Andrew Chow.

### Spending:

Samourai spends include the possibility of including custom fees as well as the use of batch spending (build up a list of batched outputs for grouped spend and fee savings).

### Ricochet:

Samourai implementation of multi-hop spend designed to outrun the baying pack of #KYCRunningDogs.

Ricochet using nLockTime (staggered) will spread out hops over different blocks and make sure that hops do not appear all at once in the mempool.

### STONEWALL:

STONEWALL spend is designed to increase the number of combinations between inputs and outputs (transaction entropy). It replaces the previously used BIP126. The objective is to obtain a positive entropy score using [Boltzmann](https://github.com/Samourai-Wallet/boltzmann) evaluation of the transaction.

### Stowaway:

A Stowaway spend, also implemented as [PayJoin](https://joinmarket.me/blog/blog/payjoin/), is a collaborative-spend carried out with another user. UTXOs are joined and the spend amount is cloaked. It is based on an [idea](https://bitcointalk.org/index.php?topic=139581.0) by Gregory Maxwell.

### Tor:

Includes Tor bundled in-app.

### TestNet3:

MainNet/TestNet selection is displayed when sideloading a new installation. To switch networks, make a backup of your current wallet, uninstall/reinstall (sideload) and select desired network.

### License:

[GNU General Public License 3](https://code.samourai.io/wallet/ExtLibJ/-/blob/develop/LICENSE-ExtLibJ)

### Contributing:

All development goes in 'develop' branch - do not submit pull requests to 'master'.
