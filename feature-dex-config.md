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

- create entries in SamouraiConfig
- update BackendServer


3. EXPOSE SamouraiConfig from whirlpool-server (use branch "features/dex-config")

- create a new Controller to expose SamouraiConfig as simple JSON structure 
  from: GET <whirlpool-server>/rest/dex-config


4. implement DexConfigProvider.load() to load values from whirlpool-server

- more details coming soon (remote loading, signature verification...)


5. EXPOSE SamouraiConfig from decentralized Dojo and load it with DexConfigProvider.load()

- more details coming soon
