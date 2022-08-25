FEATURE: DEX-CONFIG
____________________


We have several critical URLs hardcoded into SamouraiWallet dependencies.

Currently these URLs are single point of failure for SamouraiWallet:
if samourai DNS were blacklisted, we would need to update each url, recompile and release everything.

As a first step, we would like to expose such URLs from whirlpool-server, and load it remotely from ExtLibJ.
This way, we would only need to update config from whirlpool-server to update all clients remotely.
Later, we will expose such URLs from a decentralized Dojo hosting system.


1. SEE DRAFT CLIENT IMPLEMENTATION in extlibj branch "features/dex-config"

- SamouraiConfig will be used for Samourai configuration (BackendServer, SorobanServer)
- DexConfigProvider will manage config loading
- DexConfigProviderTest will test it


2. EXTERNALIZE each critical URL to SamouraiConfig

- see how BackendServer was connected to DexConfigProvider as example
- connect SorobanServer to DexConfigProvider as well


3. EXPOSE SamouraiConfig from whirlpool-server

- Waiting for Dojo DEX hosting, SamouraiConfig will be exposed to https://pool.whirl.mx:8081/rest/dex-config
  => ask zeroleak to update whirlpool-server for reflecting last ExtLibJ changes


4. implement DexConfigProvider.load() to load values from whirlpool-server

- load values from https://pool.whirl.mx:8081/rest/dex-config
- verify payload signature (more details coming soon)


5. EXPOSE SamouraiConfig from decentralized Dojo and load it with DexConfigProvider.load()

- more details coming soon
