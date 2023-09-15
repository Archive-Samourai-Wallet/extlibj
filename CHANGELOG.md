# CHANGELOG
Changelog for ExtLibJ.

## Unreleased
### 

- build: add CHANGELOG generation *(2023-09-15)*
- bitcoinj version with TransactionInput.setValue() *(2023-06-01)*

## 0.0.46
### 

- fix "min relay fee not met" on Tx0 1sat/b: adjust ESTIMATED_OPRETURN_LEN *(2023-07-29)*
- Documentation *(2023-07-12)*

## 0.0.46-featureTx0
### 

- rename .getPub() to getBipPub() to avoid confusion with .getXPub() *(2023-06-09)*
- remove unused UnspentOutput.pubkey *(2023-06-09)*
- BipWallet: add logs *(2023-06-05)*
- rename UtxoProvider.getNextChangeAddress() -> getNextAddressChange() *(2023-05-31)*
- BipWallet refactoring: register wallets by XPub, remove POSTMIX_BIP84_AS_*, add BipWallet.bipFormatDefault, add BipWallet.getNextAddress(BipFormat) *(2023-05-31)*
- add UTXO(path,xpub) constructor *(2023-05-29)*
- add UTXO.xpub *(2023-05-29)*
- FeeUtil: modify nyte count for OP_RETURN *(2023-05-25)*
- rename SpendSelectionBoltzmann -> SpendSelectionStonewall *(2023-03-02)*
- Multi Tx0x2 Cahoots Type *(2023-03-01)*
- avoid txid collisions when using multiple MockUtxoProvider *(2023-02-12)*
- add KeyBagTest *(2023-02-12)*
- add KeyBagTest *(2023-02-12)*
- add KeyBag helper *(2023-02-11)*
- add UnspentOutput.computePath() *(2023-02-11)*
- add WalletSupplier.getAddress(UnspentOutput) *(2023-02-11)*
- add BipWallet.getWallet(UnspentOutput) *(2023-02-11)*
- prepare for Tx0x2 *(2022-12-04)*
- add TxUtil.findOutputByAddress() *(2022-12-04)*
- revert previous change *(2022-12-04)*
- add Cahoots2x charts *(2022-12-03)*
- factorize cahoots *(2022-12-03)*
- add new CahootsType: Tx0X2 *(2022-12-02)*

## 0.0.45
### 

- XPUB: add static method to build xpub from raw data *(2023-06-07)*
- MessageSignUtilGeneric: modify for bech32 *(2023-06-07)*
- add other script types to message sign/verify *(2023-06-06)*
- update WhirlpoolServer.MAINNET.signingAddress *(2023-04-28)*
- Point: modify equals() method *(2023-03-22)*

## 0.0.44
### 

- Create new package for PSBT classes *(2023-03-07)*

## 0.0.43
### 

- Modify group value test *(2023-02-17)*

## 0.0.42
### 

- removed unused imports in XPUB *(2023-01-23)*
- XPUB decoder *(2023-01-23)*

## 0.0.41
### 

- FidelityTimelocksTest: refactor *(2022-12-31)*
- refactor segwit address classes *(2022-12-28)*
- Stonewallx2 example doc update CahootsContext *(2022-12-07)*
- FidelityTimelocksTest: add certificate signing example *(2022-11-14)*
- refactor timelock address test vectors *(2022-11-13)*
- TimelockAddress: break out general timelock address from fidelity bond specific address *(2022-11-13)*
- FidelityTimelocksTest: add present and future transactions *(2022-11-12)*
- FidelityTimelocksTest *(2022-10-11)*
- FidelityTimelockAddress *(2022-10-11)*
- SegwitAddress: add P2WSH *(2022-10-11)*
- allow custom chain indexes *(2022-10-11)*

## 0.0.40
### 

- STONEWALL modif *(2022-12-05)*

## 0.0.39
### 

- add Cahoots2x documentation *(2022-12-03)*
- move Cahoots2x.strCollabChange to STONEWALLx2 *(2022-12-03)*
- factorize cahoots *(2022-12-03)*
- allow Stowaway counterparty to contribute with POSTMIX utxos as inputs, but receive outputs to DEPOSIT *(2022-11-24)*

## 0.0.38
### 

- Remove comment and unnecessary chainsupplier *(2022-11-22)*
- Remove Stowaway locktime enforcement. Didn't know Sparrow currently uses current block. *(2022-11-18)*

## 0.0.37
### 

- fix "not possible to launch Whirlpool with tor enabled" *(2022-11-12)*
- Move ChainSupplier to CahootsWallet constructor *(2022-11-11)*
- Testing locktime enforcement *(2022-11-10)*
- Revert "Revert commit and set RBF to disabled for Stowaways and Stonewallx2s" *(2022-11-10)*
 * This reverts commit 3ddd9ac823d291d5c20c74dc54ef0bb3d905dd42.
- Revert "Add RBF check" *(2022-11-10)*
 * This reverts commit f875a85a392d194530fe40887fae75db8e71403f.
- Revert "Better wording for RBF error" *(2022-11-10)*
 * This reverts commit d12409e8d74e107af5012d5b98dfe5dd118b9fb2.
- Revert "Better wording for RBF error pt 2" *(2022-11-10)*
 * This reverts commit 9f2d268ad2ed36fd9daa66048f600e1eb7517715.

## 0.0.36
### 

- Better wording for RBF error pt 2 *(2022-11-07)*
- Better wording for RBF error *(2022-11-07)*
- Add RBF check *(2022-11-06)*
- Revert commit and set RBF to disabled for Stowaways and Stonewallx2s *(2022-11-02)*

## 0.0.35
### 

- add WhirlpoolServer *(2022-10-27)*
- add DexConfigProvider.lastLoad *(2022-10-27)*
- update DexConfigResponse constructor for whirlpool-server *(2022-09-15)*
- Connect SorobanServer to DexConfigProvider *(2022-09-13)*
- add dex-config specs *(2022-09-13)*
- Externalize whirpool server URLs to samouraiconfig *(2022-09-13)*
- DexConfig: use explicit exception for DexConfigProvider.load() + throw exception on invalid signature *(2022-09-13)*
- revert original soroban server testnet url *(2022-09-13)*
- Fetch values from whirlpool server and assign them to samourai config *(2022-09-13)*
- Update soroban server testnet url *(2022-09-13)*
- add dex-config specs *(2022-09-13)*
- fix getter sorobanServerMainnetOnion *(2022-09-13)*
- add dex-config specs *(2022-09-13)*
- add dex-config specs *(2022-09-13)*
- add dex-config specs *(2022-09-13)*
- add dex-config specs *(2022-09-13)*
- Externalize whirpool server URLs to samouraiconfig *(2022-09-08)*
- fix getter sorobanServerMainnetOnion *(2022-09-01)*
- revert original soroban server testnet url *(2022-09-01)*
- DexConfig: use explicit exception for DexConfigProvider.load() + throw exception on invalid signature *(2022-09-01)*
- Fetch values from whirlpool server and assign them to samourai config *(2022-08-29)*
- Update soroban server testnet url *(2022-08-27)*
- add dex-config specs *(2022-08-26)*
- Connect SorobanServer to DexConfigProvider *(2022-08-26)*
- add dex-config specs *(2022-08-26)*
- add dex-config specs *(2022-08-25)*
- add dex-config specs *(2022-08-25)*
- add dex-config specs *(2022-08-25)*
- add dex-config specs *(2022-08-25)*

## 0.0.33.15-cahootsForCli
### 

- improve SpendTx support for Cahoots *(2022-10-19)*
- adapt for cahoots update *(2022-10-14)*
- rename asyncUtil.blockingSingle() -> blockingGet() *(2022-10-13)*
- add IStompClientService + IHttpClientService for java-websocket-client *(2022-10-08)*
- IHttpClient: use Single result instead of Observable *(2022-10-04)*
- if file is not found, default to 2 BTC threshold *(2022-09-20)*
- remove import *(2022-09-19)*
- Fix file reading *(2022-09-19)*
- Add log stuff *(2022-09-19)*
- Threshold config (WIP) *(2022-09-19)*
- adapt Cahoots for CLI: use CahootsUtxoProvider to wire CahootsWallet with UtxoProvider *(2022-08-20)*
- adapt CahootsUtxo.getKey() for Sparrow *(2022-08-20)*
- adapt IPushTx for CLI *(2022-08-20)*
- adapt Cahoots for CLI: move CahootsWallet from service constructor to CahootsContext *(2022-08-20)*
- add AsyncUtil.blockingLast() *(2022-08-20)*
- revert BipWallet changes related to POSTMIX_BIP84 and use POSTMIX_BIP84_AS_BIP49 + POSTMIX_BIP84_AS_BIP44 *(2022-08-20)*

## 0.0.33.15
### 

- refactor code *(2022-10-07)*
- include P2WSH in output size calcs *(2022-10-07)*

## 0.0.33.14
### 

- Remove bitcoinj.wallet.KeyChainGroup from CryptoTestUtil for PaymentCodeTest *(2022-07-19)*

## 0.0.33.13
### 

- fix NullpointerException with explicit exception for Bech32UtilGeneric.computeScriptPubKey(invalidAddress) *(2022-09-08)*

## 0.0.33.12
### 

- fix edge-case saas/stowaway failures: sometimes stowaway step3/4 was signing with different utxos than those added in step1/2 *(2022-09-05)*
 * &#x3D;&gt; added CahootsContext.inputs to keep track of these utxos
- add cahoots debug info *(2022-09-05)*

## 0.0.33.11
### 

- FeeUtil: adjust output length *(2022-09-05)*
- add SamouraiWalletConst.SAAS_PCODE_* *(2022-09-02)*
- fix nullpointer on Stonewallx2 with TAPROOT: getReceiveWallet(account,P2TR) throws nullpointer. *(2022-09-02)*
 * like-typed output is not implemented for TAPROOT &#x3D;&gt; handle TAPROOT mix output as SEGWIT_NATIVE
- Stonewallx2 + SAAS: modify fee calc to include P2TR *(2022-09-02)*
- factorize code *(2022-09-02)*
- FormatsUtilGeneric: isValidP2TR() *(2022-08-30)*
- Point: replace assertions by exceptions *(2022-08-30)*
- FeeUtil: include P2TR in output size calcs *(2022-08-30)*

## 0.0.33.10
### 

- modify pom.xml *(2022-08-25)*

## 0.0.33.10-cahoots-to-paynym-early4
### 

- FeeUtil: modify lengths *(2022-08-26)*
- add FeeUtil.estimatedSizeSegwit() failing test for Cahoots size estimation *(2022-08-25)*
- add FeeUtil.estimatedSizeSegwit() failing test for Cahoots size estimation *(2022-08-25)*
- remove unnecessary duplicate fee calculation *(2022-08-25)*

## 0.0.33.10-cahoots-to-paynym-early3
### 

- add Stonewallx2Context.paynymDestination set by initiator when spending to a paynym. This allows android to check STONEWALLx2.getPaynymDestination() to increment paynym counter after successfull broadcast *(2022-08-25)*

## 0.0.33.10-cahoots-to-paynym-early2
### 

- restore CahootsContext.newCounterparty() *(2022-08-24)*

## 0.0.33.10-cahoots-to-paynym-early1
### 

- add Stonewallx2Context.paynymDestination set by initiator when spending to a paynym. This allows android to check STONEWALLx2.getPaynymDestination() to increment paynym counter after successfull broadcast *(2022-08-24)*
- specialize CahootsContext and move CahootsContext.newInitiatorX() / newCounterpartyX() => Stonewallx2Context, StowawayContext, MultiCahootsContext *(2022-08-24)*
- add Cahoots fee checks *(2022-08-24)*

## 0.0.33.9
### 

- v0.0.33.9 *(2022-08-24)*
- Revert back to 8 *(2022-08-23)*
- v0.0.33.9 *(2022-08-23)*

## 0.0.33.8
### 

- v0.0.33.8 *(2022-08-18)*
- More refactoring (WIP) *(2022-08-17)*
- Refactoring some stuff relating to fetching wallets *(2022-08-17)*
- Refactoring some stuff relating to fetching wallets *(2022-08-17)*
- Add debug log *(2022-08-17)*
- Fix compilation issue *(2022-08-17)*
- WIP: Testing like-type output fix. *(2022-08-17)*

## 0.0.33.7
### 

- v0.0.33.7 *(2022-08-12)*
- Fix account issue *(2022-08-11)*
- Testing *(2022-08-11)*
- Set amount for cahoots context for sender (might not work, still need to test) *(2022-08-11)*
- revert unnecessary change *(2022-08-11)*
- move multiCahoots specific code to MultiCahootsService *(2022-08-11)*
- Fix null issue with spend amount *(2022-08-11)*
- Add yet another null check *(2022-08-11)*
- Add null check *(2022-08-11)*
- Move some safety checks around *(2022-08-11)*
- Reverse some changes *(2022-08-11)*
- Reverse validation2 *(2022-08-11)*
- Reverse validation *(2022-08-11)*
- Rewrite order of multicahoots to take into account the Stonewall fee amount when performing Stowaway. WIP *(2022-08-11)*
- Simplify isSaaSCounterparty check *(2022-08-11)*
- Make SaaS counterparty receive Stowaway to deposit *(2022-08-11)*
- Fix type mismatch *(2022-08-11)*
- Fix Stowaway account issue for Saas Counterparty *(2022-08-11)*
- Add debug statements *(2022-08-11)*
- Set account earlier on, as well as fingerprint *(2022-08-11)*
- Move account set to earlier in step *(2022-08-11)*
- Rewrites support for prioritizing non-Whirlpool UTXOs for Stowaways. *(2022-08-11)*

## 0.0.33.6
### 

- add Cahoots fee selection: CahootsContext.feePerB *(2022-08-10)*

## 0.0.33.5
### 

- Remove checkNoExtraFee as it's been made redundant by zeroleak's recent changes to the maxSpendAmount/verifiedSpendAmount computation stuff. Mine also appeared to be broken in the first place for some instances. *(2022-08-08)*
- Push Stonewallx2 first. *(2022-08-01)*
- Remove dev-testing fee addition *(2022-07-27)*
- Add more logging *(2022-07-27)*
- Add some logging to narrow down some issues *(2022-07-27)*

## 0.0.33.4
### 

- force account #0 for Stowaway counterparty *(2022-07-26)*
- fix TAPROOT signature *(2022-07-26)*
- fix SendFactoryGeneric.makeTransaction() not signing TAPROOT inputs *(2022-07-25)*

## 0.0.33.3
### 

- fix MultiCahoots.pushTx *(2022-07-23)*

## 0.0.33.2
### 

- add WALLET_INDEX.RICOCHET_* *(2022-07-16)*
- Cahoots: fix computeSpendAmount() *(2022-07-16)*
- FormatUtilGeneric.isValidBIP47OpReturn() : fix exception on invalid address *(2022-07-16)*

## 0.0.33.1
### 

- fix "Cahoots verifiedSpendAmount mismatch" when extracting *(2022-07-09)*
- add test multiCahoots_bip84_extract() *(2022-07-09)*

## 0.0.33
### 

- factorize Cahoots code *(2022-07-08)*
- stonewallx2 contributor mix output like-typed with destination *(2022-07-08)*
- add CahootsContext.account *(2022-07-06)*

## 0.0.32
### 

- externalize Cahoots.pushTx() logic to ExtLibJ *(2022-07-05)*
- externalize MultiCahoots constants to ExtLibJ *(2022-07-05)*
- replace Cahoots2x.verifiedSpendAmount with checkMaxSpendAmount() *(2022-07-04)*
- remove whirlpool-client dependency by moving XManagerClient to ExtLibJ *(2022-07-04)*
- move CahootsWallet.computeBalance() -> CahootsUtxo.sumValue() to keep naming as UTXO.sumValue() / UnspentOutput.sumValue() *(2022-07-04)*
- move ManualCahootsService.reply()/verifyResponse() to AbstractCahootsService *(2022-07-04)*
 * + use Cahoots2x/Cahoots2xService to share code between Stonewallx2/Stowaway
- Add condition that destination address must be bech32 for us to send funds out of the wallet *(2022-07-02)*
- Set extraction threshold to 2 BTC *(2022-06-28)*
- Fix TypeInteraction step *(2022-06-22)*
- Use previous payload for next one *(2022-06-22)*
- WIP: Simplify steps *(2022-06-22)*
- Finalize fee amounts *(2022-06-22)*
- Add additional 400 satoshi fee just in case to cover any fees from the Stonewallx2 itself. *(2022-06-22)*
- Increase fee to 3% with a cap at 0.002 BTC *(2022-06-22)*
- Fix issue where address is sometimes the default service address. Attempt to get fresh address with multiple tries. Use our own address as fallback. *(2022-06-22)*
- Clean up code *(2022-06-22)*
- log our address being sent to *(2022-06-22)*
- Revert "Merge branch 'junit-testing' into 'develop'" *(2022-06-22)*
 * This reverts merge request !17
- Add log for when funds are extracted *(2022-06-22)*
- Fix Stowaway issue *(2022-06-17)*
- Update libraries *(2022-06-17)*
- Use java.util.Optional for JacksonHttpClient *(2022-06-17)*
- Update xmanager dependency *(2022-06-17)*
- WIP: Extract funds using xpub because porting the Paynym shit over right now is not worth it *(2022-06-16)*
- WIP: Replace old step with multi step *(2022-06-09)*
- Test additions *(2022-06-07)*
- Test additions *(2022-06-06)*
- Test additions *(2022-06-05)*
- Test additions *(2022-06-05)*
- Add fund extraction to external wallet (WIP) *(2022-06-04)*
- Test additions *(2022-06-03)*
- Test changes *(2022-05-27)*
- make computeMaxSpendAmount() specific per CahootsType *(2022-05-23)*
- Move receive address generation for counterparty to new correct step *(2022-05-21)*
- make multiCahoots reuse stonewallx2 & stowaway *(2022-05-21)*
- Fix step check issue *(2022-05-21)*
- Testing some stuff, changing on how the steps are handled *(2022-05-21)*
- Fix issue with setting TypeInteraction *(2022-05-21)*
- Fix bug with verifying spend amounts on wrong step *(2022-05-21)*
- working on some more stuff. making sure the final tx is on the user's end, not server's *(2022-05-21)*
- Add line in Stonewallx2Service to reset totalSelectedAmount when selectedUTXO is reset while still calculating *(2022-05-21)*
 * also remove printlns
- Add line to reset totalSelectedAmount integer so this should fix the offset bug *(2022-05-21)*
- It seems inputs are getting ignore sometimes, not entirely sure why... This is a weird bug *(2022-05-21)*
- Simplify logging... still not sure where its going wrong *(2022-05-21)*
- more log, narrowing down the issue *(2022-05-21)*
- Add more logs *(2022-05-21)*
- Log amount for debugging *(2022-05-21)*
- Reverting some changes and also trying to fix a weird math issue regarding change outputs... *(2022-05-21)*
- testing some stuff... *(2022-05-21)*
- Fix output finding stuff since there should only be 1 output at this point in the cahoots *(2022-05-21)*
- Does not work, fixing issue with receive address duplication for sender wallet *(2022-05-21)*
- Remove all printlns as it works! *(2022-05-21)*
- once more *(2022-05-21)*
- Combing over the discrepancies between collab vs sender *(2022-05-21)*
- Fix some change address issues *(2022-05-21)*
- Re-add fee subtraction for Stowaway as I accidentally removed it. *(2022-05-21)*
- Fix output finding for sender fee deduction *(2022-05-21)*
- Some more bug fixes... I think this is it tho *(2022-05-21)*
- Add some output calc debug printlns *(2022-05-21)*
- Fix redundant fee after fixing counterparty/sender issues *(2022-05-21)*
- Re-add some debug lines. *(2022-05-21)*
- realized addresses were being converted to legacy lol *(2022-05-21)*
- WIP, some more fixes. *(2022-05-21)*
- Minor bug fixes in regards to counterparty fee calculation *(2022-05-21)*
- WIP: I think I got all of the steps mapped out correctly, so there should be no more weird insufficient balance errors *(2022-05-21)*
- testing some stuff with stonewall fees *(2022-05-20)*
- Fee fixes *(2022-05-20)*
- Fix fee calculation *(2022-05-20)*
- add additional conditions for verifiedSpendAmount > maxSpendAmount *(2022-05-20)*
- WIP: Logging some stuff to debug *(2022-05-20)*
- Simplify UTXO filtering between txs *(2022-05-20)*
- Make sure verifyResponse only runs on certain steps (WIP) *(2022-05-20)*
- Add check for verified spend amount. Counterparty does not pay fees for Stowaway or Stonewallx2 in Multi *(2022-05-20)*
- make MultiCahootsServiceTest reproductible *(2022-05-20)*
- Add check for SorobanInteraction responses when setting up stonewall portion *(2022-05-20)*
- Add else for standard reply *(2022-05-20)*
- Add check for not multi type *(2022-05-20)*
- Add multicahoots type interaction for broadcast *(2022-05-20)*
- Fix issue with isDone() to account for extra steps in Multi cahoots *(2022-05-20)*
- Added testing for parsing a String *(2022-05-18)*
- derive P2TR address format *(2022-05-01)*
- Remove printlns *(2022-04-26)*
- include Taproot in returned BIP_FORMATs *(2022-04-26)*
- Add remaining steps and ensure transaction is built properly. No idea if this actually works in practice as it still needs testing with actual wallets. *(2022-04-21)*
- Heavy WIP: Stonewallx2 stuff *(2022-04-19)*
- Add some additional variables. Still needs testing. *(2022-04-18)*
- Add new Cahoots type *(2022-04-18)*

## 0.0.31-1
### 

- SpendBuilder: honor preselectedInputs *(2022-06-23)*
- skip logging stacktrace for HttpException *(2022-06-23)*
- SpendBuilder: honor preselectedInputs *(2022-06-06)*
- skip logging stacktrace for HttpException *(2022-06-01)*
- update Z85Test.decode() *(2022-04-21)*
- add Z85Test.decode() *(2022-04-20)*
- add RicochetUtilGeneric *(2022-04-20)*

## 0.0.31
### 

- Cahoots: use BipWallet to prevent address-reuse *(2022-03-21)*
- add SweepUtilGeneric *(2022-03-19)*
- add BackendApi.fetchXPub() *(2022-03-09)*
- add PayloadUtilGeneric.readBackup() + PayloadUtilGeneric.writeBackup() *(2022-03-09)*

## 0.0.30
### 

- Sparrow: introduce methods for retrieving private keys to avoid requiring exporting bitcoinj packages *(2022-03-04)*
- use AsyncUtil *(2022-03-04)*
- add backendApi.PushTxException *(2022-03-02)*
- Sparrow: support soroban users with non-zero bip47 accounts *(2022-02-24)*
- Taproot: Private Key Tweaking, Schnorr Transaction Signing *(2022-02-17)*

## 0.0.26-4
### 

- add AddressFactoryWalletStateIndexHandler + update AddressFactoryGeneric *(2022-02-25)*

## 0.0.29
### 

- use java-http-client 1.0.6 *(2022-02-16)*
- use java-http-client 1.0.5 *(2022-02-16)*
- use jdk 8 *(2022-02-16)*
- use bitcoinj 003 *(2022-02-15)*
- fix cointype in path *(2022-02-14)*
- add PaynymApi implementation *(2022-02-13)*
- add HD_WalletFactoryGeneric.restoreWalletFromWords() *(2022-02-13)*
- adapt for jdk6 *(2022-02-07)*
- add BipDerivation + BipFormat + WalletSupplier + IndexHandlerSupplier + SamouraiAccountIndex *(2022-02-02)*
- P2TR and output pubkey helper methods *(2022-01-26)*
- BIP340, BIP86, Schnorr *(2022-01-21)*
- Util update *(2022-12-27)*
- Bech32Test update *(2022-12-27)*
- AESUtilTest: remove print statements *(2022-12-27)*
- update license *(2022-12-27)*
- update .gitignore *(2022-12-27)*
- OBPP05 *(2022-12-27)*

## 0.0.28
### 

- fix BackendWsApi.onMessage() *(2022-01-04)*
- remove unused wipe() *(2022-01-03)*

## 0.0.26-3
### 

- adapt for bip47 *(2022-01-04)*

## 0.0.26-2
### 

- remove ambigous AddressFactoryGeneric.wipe() & HD_Wallet.wipe() *(2022-12-30)*

## 0.0.26-1
### 

- add AddressFactoryGeneric.reset() *(2022-12-29)*
- add AddressFactoryGeneric.reset() *(2022-12-29)*

## 0.0.27
### 

- add AddressFactoryGeneric.reset() *(2022-12-29)*
- allow non-zero counterparty accounts in stowaways (Sparrow) *(2021-12-15)*
- backport Sparrow changes for Cahoots integration *(2021-12-12)*
- update dependencies *(2021-12-10)*
- Bech32m test vectors *(2021-12-03)*
- add PayloadUtilGeneric + BackupPayload *(2021-11-10)*

## 0.0.26
### 

- add IIndexHandler.set(int value, boolean allowDecrement) *(2021-10-22)*

## 0.0.25
### 

- add HttpException(String message, String responseBody) *(2021-10-21)*
- add HD_WalletFactoryGeneric.newWallet() *(2021-10-18)*

## 0.0.24
### 

- fix hdWallet.getXPUBs() *(2021-10-15)*
- fix hdWallet.getXPUBs() *(2021-10-15)*

## 0.0.22
### 

- modify pom.xml *(2021-10-14)*
- Bech32Test: add P2TR address provided by Sipa in bitcoin-dev *(2021-10-12)*
- bech32m *(2021-10-09)*

## 0.0.21
### 

- add PushTxAddressReuseException.adressReuseOutputIndexs *(2021-10-13)*

## 0.0.20
### 

- refactor HD_Wallet.accounts to a map by accountIdx *(2021-10-06)*
- add AddressFactoryGeneric *(2021-10-06)*

## 0.0.19-dsk4
### 

- add UTXO.toUnspentOutputs() *(2021-09-16)*
- Post-mix like type change *(2021-09-13)*

## 0.0.19-dsk3
### 

- adapt for Sparrow *(2021-08-22)*
- move BIP39/en.txt to Bip39English + remove HD_WalletFactoryJava + adapt for Sparrow *(2021-08-20)*

## 0.0.19-dsk2
### 

- add HD_Wallet.getSeed() *(2021-08-16)*
- add pushTx strictMode *(2021-08-13)*

## 0.0.19-dsk1
### 

- fix BackendWsApi auto-reconnect *(2021-07-11)*
- add backendApi.fetchTx() *(2021-07-06)*
- add SegwitAddress.segWitRedeemScriptToString() *(2021-07-05)*
- add BackendWsApi *(2021-06-25)*
- add BipWallet + IIndexHandler *(2021-06-18)*
- add SpendBuilder *(2021-06-05)*
- add hdAddress.getPathString(addressType) *(2021-05-18)*
- create branch dsk *(2021-05-07)*

## 0.0.18-fetchWallet
### 

- fix "Buffering capacity exceeded" on large fetchWallet() responses *(2021-04-18)*

## 0.0.17-fetchWallet
### 

- use new soroban endpoints *(2021-03-08)*
- add FormatsUtilGeneric.xlatXpub() *(2021-03-07)*
- add FormatUtilGeneric.isValidXpubBip84() *(2021-03-03)*
- update CryptoUtil: validate IV with HMAC *(2021-03-02)*
- add RandomUtil *(2021-02-28)*
- AESUtil.encrypt(): always use UTF-8 for String encoding *(2021-02-08)*
- fix CryptoUtilTest *(2021-02-08)*
- update jackson-databind 2.9.10.7 *(2020-12-14)*

## 0.0.16-aes
### 

- v 0.0.16-aes *(2021-01-03)*
- v 0.0.15-aes *(2021-01-03)*

## 0.0.15-soroban-aes
### 

- Use bouncy castle to derive secret key *(2021-01-03)*
- v 0.0.15-soroban-aes *(2021-01-03)*
- Added BouncyCastle provider *(2021-01-03)*
- Bump jackson-databind from 2.9.10.1 to 2.9.10.4 *(2020-04-23)*
 * Bumps [jackson-databind](https://github.com/FasterXML/jackson) from 2.9.10.1 to 2.9.10.4.
 * - [Release notes](https://github.com/FasterXML/jackson/releases)
 * - [Commits](https://github.com/FasterXML/jackson/commits)
 * Signed-off-by: dependabot[bot] &lt;support@github.com&gt;

## 0.0.14-aes-soroban-4
### 

- Updated AESUtil test cases *(2020-12-01)*
- Mark old encryption methods as deprecated *(2020-12-01)*
- Added new encryption methods to AESUtil *(2020-12-01)*
- Encrypting/decrypting data in a manner compliant with OpenSSL *(2020-12-01)*
- Kotlin support *(2020-11-29)*

## 0.0.16-fetchWallet
### 

- add XPubUtil.getPath() *(2020-11-25)*
- add XPubUtil *(2020-11-16)*

## 0.0.14-soroban4
### 

- allow manual cahoots without cahootsContext *(2020-11-12)*
- add bech32Util.getAddressFromScript(TransactionOutput) *(2020-10-05)*

## 0.0.15-soroban
### 

- add backendApi.fetchWallet() *(2020-10-06)*

## 0.0.14-soroban3
### 

- fix PSBT NetParams reset during cahoots *(2020-10-02)*
- add verifiedSpendAmount *(2020-10-02)*
- add Cahoots security *(2020-10-01)*

## 0.0.14-soroban2
### 

- add ManualCahootsService *(2020-09-17)*

## 0.0.14-soroban
### 

- adapt CryptoUtil for android *(2020-09-11)*
- add CryptoUtil *(2020-09-11)*
- Adapt online cahoots for Android *(2020-09-10)*
- Adapt online cahoots for Android *(2020-09-10)*
- create branch "cahoots" *(2020-09-10)*
- adapt Cahoots for Android *(2020-09-05)*
- fix secretPointFactory for Android *(2020-09-05)*
- add IHttpClient *(2020-08-26)*
- use CahootsUtxo *(2020-08-22)*
- add Cahoots to extlibj *(2020-08-18)*
- add Cahoots to extlibj *(2020-08-12)*

## 0.0.13
### 

- add BackendApi.fetchTxs() *(2020-07-06)*

## 0.0.12
### 

- less logs on BackendApi *(2020-06-09)*
- less logs on FeeUtil *(2020-06-08)*
- add UnspentResponse.Xpub.m *(2020-06-08)*
- change groupId *(2020-05-12)*
- change groupId *(2020-05-04)*
- move to code.samourai.io *(2020-04-25)*

## 0.0.11
### 

- add ExplorerApi *(2020-01-23)*
- add BackendServer *(2020-01-23)*

## 0.0.10
### 

- BackendApi: add oAuthManager to constructor *(2020-01-16)*

## 0.0.9
### 

- Fix Dojo authentication *(2019-12-05)*

## 0.0.8
### 

- add BIP69InputComparatorGeneric + BIP69OutputComparatorGeneric *(2019-11-06)*
- add OAuth support for BackendApi + rename SamouraiFee -> MinerFee *(2019-11-06)*

## 0.0.7
### 

- upgrade jackson-databind 2.9.10 *(2019-09-29)*

## 0.0.6
### 

- add DOJO pairing *(2019-08-08)*

## 0.0.5
### 

- move BackendServer to whirlpool-client (too specific for extlibj) *(2019-07-12)*

## 0.0.4
### 

- add PairingVersion V2 *(2019-05-22)*
- add BackendApi *(2019-05-20)*
- add BackendApi *(2019-05-18)*
- add PairingPayload *(2019-05-18)*
- make PairingPayload serializable *(2019-05-18)*
- add PairingPayload *(2019-05-18)*

## 0.0.3
### 

- add AESUtil *(2019-04-25)*
- update maven-surfire-plugin *(2019-03-22)*

## 0.0.2
### 

- add XorUtil *(2019-03-22)*
- add project.scm.id *(2019-03-04)*

## 0.0.1
### 

- add scm.connexion *(2019-03-04)*
- add maven-release-plugin *(2019-03-04)*
- FormatsUtilGeneric: modify bech32 regex *(2019-01-26)*
- add FeeUtil + test *(2019-01-19)*
- FormatsUtilGeneric: modify regex BIP21 uri w/bech32 *(2019-01-18)*
- develop branch: use develop dependency *(2018-12-06)*
- master branch: use master dependency *(2018-12-06)*
- add build status *(2018-12-06)*
- enable travis ci *(2018-12-06)*
- downgrade client to Java 1.6 *(2018-12-05)*
- downgrade client to Java 1.6 *(2018-12-05)*
- downgrade client to Java 1.6 *(2018-12-05)*
- don't use MethodHandles.lookup(), class not found on Android 5, 6, 7 *(2018-12-03)*
- don't use MethodHandles.lookup(), class not found on Android 5, 6, 7 *(2018-12-03)*
- don't use MethodHandles.lookup(), class not found on Android 5, 6, 7 *(2018-12-03)*
- remove comments *(2018-11-30)*
- PSBT: add helper functions *(2018-11-29)*
- add mentions to README *(2018-11-25)*
- add TxUtil.verifySignInput() *(2018-11-25)*
- add CryptoTestUtil *(2018-11-25)*
- basic PSBT parser & serialiser *(2018-11-25)*
- add log *(2018-11-24)*
- add TxUtil.findInputPubkey() *(2018-11-22)*
- add MessageSignUtilGeneric.verifySignedMessage() compatibility for bech32 + test *(2018-11-20)*
- add MessageSignUtilGeneric *(2018-11-20)*
- add Bip47UtilJava *(2018-11-20)*
- add HD_WalletFactoryGeneric + HD_WalletFactoryJava *(2018-11-20)*
- add PaymentCode.xorMask() *(2018-11-20)*
- FormatsUtilGeneric: refactor regexes, add PSBT test *(2018-11-18)*
- add PSBT magic value *(2018-11-18)*
- Z85: refactor regex *(2018-11-18)*
- FormatsUtilGeneric: refactor valid XPUB test *(2018-11-18)*
- FormatsUtilGeneric: get fingerprint of XPUB *(2018-11-18)*
- Licenses *(2018-11-11)*
- working release *(2018-11-09)*
- add Bech32UtilGeneric.toBech32() *(2018-11-05)*
- add Bech32UtilGeneric.toBech32() *(2018-11-05)*
- add TxUtil.signInputSegwit() + TxUtil.findInputIndex() *(2018-11-04)*
- getTransactionOutput: throw exception on fail *(2018-11-03)*
- update artifact to com.github.Samourai-Wallet:extlibj *(2018-10-29)*
- update groupId to com.github.Samourai-Wallet.bitcoinj *(2018-10-29)*
- add Z85Test *(2018-10-29)*
- move Z85 to com.samourai.wallet.util *(2018-10-29)*
- Z85 util *(2018-10-24)*
- make Bip47UtilGeneric abstract *(2018-09-13)*
- rename FormatsUtil -> FormatsUtilGeneric *(2018-09-13)*
- update test for recent change in HD_Wallet.computeRootKey() *(2018-09-13)*
- add ISecretPoint and ISecretPointFactory to abstract from bouncycastle (java) / spongycastle (android) *(2018-09-13)*
- sync ExtLibJ with samourai-wallet-android *(2018-09-13)*
- BIP69InputComparator: add generic compare function *(2018-09-13)*
- rename Bech32Util -> Bech32UtilGeneric *(2018-09-13)*
- rename Bip47Util -> bip47UtilGeneric to avoid conflicts with samourai-wallet-android *(2018-09-13)*
- add maven-source-plugin *(2018-09-11)*
- update bitcoinj: 001-whirlpool *(2018-09-11)*
- get bitcoinj dependency from maven repository *(2018-09-06)*
- BIP47Util: allow accounts other than account 0 *(2018-08-14)*
 * BIP47Util: allow accounts other than account 0
- BIP47Util: allow accounts other than account 0 *(2018-08-14)*
- add BIP69InputComparator and BIP69OutputComparator *(2018-05-10)*
- add FormatsUtil.createMasterPubKeyFromXPub() *(2018-05-10)*
- BIP47Util: use PaymentAddress to obtain pubkey *(2018-05-08)*
- BIP47Util: get pubkey for send address *(2018-05-08)*
- BIP47Util: get pubkey for receive address *(2018-05-08)*
- BIP47Util: get Samourai 'feature' payment code *(2018-05-07)*
- rename variable *(2018-05-07)*
- Samourai 'feature' byte defined *(2018-05-07)*
- update pom.xml *(2018-05-05)*
- use Samourai BitcoinJ *(2018-05-05)*
- fix regex bech32 lower case *(2018-04-25)*
- base code for BIP39,44,49,84,bech32 *(2018-04-25)*
- basic .gitignore *(2018-04-25)*
- Initial commit *(2018-04-24)*

