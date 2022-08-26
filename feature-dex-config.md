FEATURE: DEX-CONFIG
____________________


We have several critical URLs hardcoded into SamouraiWallet dependencies.

Currently these URLs are single point of failure for SamouraiWallet:
if samourai DNS were blacklisted, we would need to update each url, recompile and release everything.

As a first step, we would like to expose such URLs from whirlpool-server, and load it remotely from ExtLibJ.
This way, we would only need to update config from whirlpool-server to update all clients remotely.
Later, we will expose such URLs from a decentralized Dojo hosting system.


1. SEE DRAFT CLIENT IMPLEMENTATION in extlibj branch "feature/dex-config"

- SamouraiConfig will be used for Samourai configuration (BackendServer, SorobanServer)
- DexConfigProvider will manage config loading
- DexConfigProviderTest will test it


2. EXTERNALIZE SorobanServer URLs to SamouraiConfig

- see how BackendServer was connected to DexConfigProvider as example
- connect SorobanServer to DexConfigProvider as well


3. EXPOSE SamouraiConfig from whirlpool-server

Waiting for Dojo DEX hosting, SamouraiConfig will be exposed as DexConfigResponse on https://pool.whirl.mx:8081/rest/dex-config
  => ask zeroleak to update whirlpool-server for reflecting your changes on ExtLibJ


4. implement DexConfigProvider.load() to load values from whirlpool-server

- load values from https://pool.whirl.mx:8081/rest/dex-config
IBackendClient is an httpClient implemented both by ExtLibJ consumers (samourai-wallet-android and whirlpool-client-cli)

you can pass IBackendClient to DexConfigProvider constructor and use 
IBackendClient.getJson(url, DexConfigResponse.class, headers) to fetch remote JSON easily, see how it works in BackendApi.
DexConfigResponse.samouraiConfig can be unserialized using JSONUtils.getObjectMapper().readValue(str, SamouraiConfig.class)

- verify signature  using MessageSignUtilGeneric:
DexConfigResponse.signature should be signing DexConfigResponse.samouraiConfig with DexConfigResponse.SIGNING_ADDRESS

- use DexConfigProviderTest for testing it



5. EXPOSE SamouraiConfig from decentralized Dojo and load it with DexConfigProvider.load()

- more details coming soon
