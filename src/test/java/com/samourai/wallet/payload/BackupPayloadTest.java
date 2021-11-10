package com.samourai.wallet.payload;

import com.fasterxml.jackson.databind.JsonNode;
import com.samourai.wallet.api.pairing.PairingPayload;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.NetworkParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupPayloadTest {
    private static final Logger log = LoggerFactory.getLogger(BackupPayloadTest.class);

    private String BACKUP_PAYLOAD = "{\"wallet\":{\"testnet\":false,\"seed\":\"e598320ab2d5c42f94b108ec0dece582\",\"passphrase\":\"test\",\"fingerprint\":\"77275290\",\"accounts\":[{\"xpub\":\"xpub6C2STbGiq9qvjhgfTyXhvHK2HQg4HFgqWcPBVJM82stnyfcLfLTvRKc5M5GCn7t1a5LgsCvvB9bUhQPoLZYFQjN4Wu8yV6y3ZbHtF3KyRXe\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":0}],\"payment_code\":\"PM8TJbvQrAdpXquDjkh7MQ1ZbtTTXsMVri9ydDbHPWNwNFvuLJanbsqugjLU2CGG78ceHJgui3C94TH2xVBcm9itNvoFSFHV5jkF9z61UfsC5n59n2Xj\",\"payment_code_feature\":\"PM8TJbvQrAdpXquDjkh7MQ1ZbtTTXsMVri9ydDbHPWNwNFvuLJanbsqugjLU2CGG78ceHJgui3C94TH2xVBcm9itNvoFSFHV5jkF9z61UfsC5nDFt8o4\",\"bip49_accounts\":[{\"ypub\":\"ypub6WvxpidnzPZMy1S6udXxo6WT8dcd7MrQokJKeqE2HLQrbmVGfPzNoyyvtyJJFvxjxLtbFPQfVk8r5m872evxReLMJckqEg8SZum96pzzq7U\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":0}],\"bip84_accounts\":[{\"zpub\":\"zpub6rKtc12pZ41ZY66Bajg7dPE8G6Gov5qSADMZc5uznJjLwQBX3zpLXPvqMjoTSnh3U4217VnxG7X2jNYJCS4T5QDsYWrLN9yA1eNw1ofrTsz\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":0}],\"whirlpool_account\":[{\"zpub\":\"zpub6rKtc12xtiYXcPMowvBhoNV5XqXDdLAbBPWbTbPGTzzS1ehz94P2KFCKoyeHyUTMFhD5Bj4XevZwrRvBK39hG67SH4x3i5eauzZeQV1EwBw\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":2147483645},{\"zpub\":\"zpub6rKtc12xtiYXei7YcAfs88sEB5A4adcAR4gVNfqHMiWdd2hXJSpnVmy65rPjZJRn5bxAkmpSfRzw8Kp4buKygSQAkhmH2uRGrvDsuFLAyiM\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":2147483646},{\"zpub\":\"zpub6rKtc12xtiYXa9zjfimmeXHGeXektVaQDttRk5R3gzZwfeNaKTj3rCmnsMrJYsakr9rJ8fF2p6jbU7V3zxApyP6gc72Dh6ZLK9DjYfR3wsy\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":2147483644}]},\"meta\":{\"version_name\":\"0.99.97a\",\"android_release\":\"10\",\"device_manufacturer\":\"Unihertz\",\"device_model\":\"Jelly2\",\"device_product\":\"Jelly2_EEA\",\"prev_balance\":0,\"sent_tos\":[],\"sent_tos_from_bip47\":[],\"batch_send\":[],\"use_segwit\":true,\"use_like_typed_change\":true,\"spend_type\":0,\"rbf_opt_in\":false,\"use_ricochet\":false,\"ricochet_staggered_delivery\":false,\"bip47\":{\"pcodes\":[],\"incoming_notif_hashes\":[]},\"pin\":\"00000\",\"pin2\":\"\",\"ricochet\":{\"xpub\":\"zpub6rKtc12xtiYXhEMfFySB7D762WjSyHZSXnXuz1Q6cordkRu5uabzSpuA5EaB2b8Q7UmvgFCMnCFSBDcjoZ6GfZFodmLdM75MK1dbE1LCeem\",\"index\":0,\"queue\":[],\"staggered\":[]},\"cahoots\":[],\"rbfs\":[],\"tor\":{\"active\":false},\"blocked_utxos\":{\"blocked\":[],\"notDusted\":[],\"notDustedPostMix\":[],\"blockedPostMix\":[],\"blockedBadBank\":[]},\"utxo_tags\":[],\"utxo_notes\":[],\"utxo_scores\":[],\"whirlpool\":{},\"tx0_display\":[],\"trusted_no\":\"\",\"scramble_pin\":true,\"haptic_pin\":true,\"auto_backup\":true,\"remote\":false,\"use_trusted\":false,\"check_sim\":false,\"broadcast_tx\":true,\"strict_outputs\":true,\"xpubreg44\":true,\"xpubreg49\":true,\"xpubreg84\":true,\"xpubprereg\":false,\"xpubpostreg\":false,\"xpubricochetreg\":false,\"xpublock44\":false,\"xpublock49\":false,\"xpublock84\":false,\"xpubprelock\":false,\"xpubpostlock\":false,\"xpubbadbanklock\":false,\"xpubricochetlock\":false,\"paynym_claimed\":false,\"paynym_refused\":true,\"paynym_featured_v1\":false,\"user_offline\":false,\"is_sat\":false,\"localIndexes\":{\"local44idx\":0,\"local49idx\":0,\"local84idx\":0},\"xpubpostxreg\":false}}";

    @Test
    public void decryptBackupFile() throws Exception {
        BackupPayload backup = BackupPayload.parse(BACKUP_PAYLOAD);
        HD_Wallet hdw = backup.computeHdWallet();

        Assertions.assertFalse(backup.isWalletTestnet());
        NetworkParameters params = backup.computeNetworkParameters();
        Assertions.assertEquals(backup.isWalletTestnet(), FormatsUtilGeneric.getInstance().isTestNet(params));

        Assertions.assertEquals("e598320ab2d5c42f94b108ec0dece582", backup.getWalletSeed());
        Assertions.assertEquals(backup.getWalletSeed(), hdw.getSeedHex());

        Assertions.assertEquals("test", backup.getWalletPassphrase());
        Assertions.assertEquals(backup.getWalletPassphrase(), hdw.getPassphrase());

        JsonNode wallet = backup.getWallet();
        Assertions.assertEquals("{\"testnet\":false,\"seed\":\"e598320ab2d5c42f94b108ec0dece582\",\"passphrase\":\"test\",\"fingerprint\":\"77275290\",\"accounts\":[{\"xpub\":\"xpub6C2STbGiq9qvjhgfTyXhvHK2HQg4HFgqWcPBVJM82stnyfcLfLTvRKc5M5GCn7t1a5LgsCvvB9bUhQPoLZYFQjN4Wu8yV6y3ZbHtF3KyRXe\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":0}],\"payment_code\":\"PM8TJbvQrAdpXquDjkh7MQ1ZbtTTXsMVri9ydDbHPWNwNFvuLJanbsqugjLU2CGG78ceHJgui3C94TH2xVBcm9itNvoFSFHV5jkF9z61UfsC5n59n2Xj\",\"payment_code_feature\":\"PM8TJbvQrAdpXquDjkh7MQ1ZbtTTXsMVri9ydDbHPWNwNFvuLJanbsqugjLU2CGG78ceHJgui3C94TH2xVBcm9itNvoFSFHV5jkF9z61UfsC5nDFt8o4\",\"bip49_accounts\":[{\"ypub\":\"ypub6WvxpidnzPZMy1S6udXxo6WT8dcd7MrQokJKeqE2HLQrbmVGfPzNoyyvtyJJFvxjxLtbFPQfVk8r5m872evxReLMJckqEg8SZum96pzzq7U\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":0}],\"bip84_accounts\":[{\"zpub\":\"zpub6rKtc12pZ41ZY66Bajg7dPE8G6Gov5qSADMZc5uznJjLwQBX3zpLXPvqMjoTSnh3U4217VnxG7X2jNYJCS4T5QDsYWrLN9yA1eNw1ofrTsz\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":0}],\"whirlpool_account\":[{\"zpub\":\"zpub6rKtc12xtiYXcPMowvBhoNV5XqXDdLAbBPWbTbPGTzzS1ehz94P2KFCKoyeHyUTMFhD5Bj4XevZwrRvBK39hG67SH4x3i5eauzZeQV1EwBw\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":2147483645},{\"zpub\":\"zpub6rKtc12xtiYXei7YcAfs88sEB5A4adcAR4gVNfqHMiWdd2hXJSpnVmy65rPjZJRn5bxAkmpSfRzw8Kp4buKygSQAkhmH2uRGrvDsuFLAyiM\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":2147483646},{\"zpub\":\"zpub6rKtc12xtiYXa9zjfimmeXHGeXektVaQDttRk5R3gzZwfeNaKTj3rCmnsMrJYsakr9rJ8fF2p6jbU7V3zxApyP6gc72Dh6ZLK9DjYfR3wsy\",\"receiveIdx\":0,\"changeIdx\":0,\"id\":2147483644}]}", wallet.toString());

        JsonNode meta = backup.getMeta();
        Assertions.assertEquals("{\"version_name\":\"0.99.97a\",\"android_release\":\"10\",\"device_manufacturer\":\"Unihertz\",\"device_model\":\"Jelly2\",\"device_product\":\"Jelly2_EEA\",\"prev_balance\":0,\"sent_tos\":[],\"sent_tos_from_bip47\":[],\"batch_send\":[],\"use_segwit\":true,\"use_like_typed_change\":true,\"spend_type\":0,\"rbf_opt_in\":false,\"use_ricochet\":false,\"ricochet_staggered_delivery\":false,\"bip47\":{\"pcodes\":[],\"incoming_notif_hashes\":[]},\"pin\":\"00000\",\"pin2\":\"\",\"ricochet\":{\"xpub\":\"zpub6rKtc12xtiYXhEMfFySB7D762WjSyHZSXnXuz1Q6cordkRu5uabzSpuA5EaB2b8Q7UmvgFCMnCFSBDcjoZ6GfZFodmLdM75MK1dbE1LCeem\",\"index\":0,\"queue\":[],\"staggered\":[]},\"cahoots\":[],\"rbfs\":[],\"tor\":{\"active\":false},\"blocked_utxos\":{\"blocked\":[],\"notDusted\":[],\"notDustedPostMix\":[],\"blockedPostMix\":[],\"blockedBadBank\":[]},\"utxo_tags\":[],\"utxo_notes\":[],\"utxo_scores\":[],\"whirlpool\":{},\"tx0_display\":[],\"trusted_no\":\"\",\"scramble_pin\":true,\"haptic_pin\":true,\"auto_backup\":true,\"remote\":false,\"use_trusted\":false,\"check_sim\":false,\"broadcast_tx\":true,\"strict_outputs\":true,\"xpubreg44\":true,\"xpubreg49\":true,\"xpubreg84\":true,\"xpubprereg\":false,\"xpubpostreg\":false,\"xpubricochetreg\":false,\"xpublock44\":false,\"xpublock49\":false,\"xpublock84\":false,\"xpubprelock\":false,\"xpubpostlock\":false,\"xpubbadbanklock\":false,\"xpubricochetlock\":false,\"paynym_claimed\":false,\"paynym_refused\":true,\"paynym_featured_v1\":false,\"user_offline\":false,\"is_sat\":false,\"localIndexes\":{\"local44idx\":0,\"local49idx\":0,\"local84idx\":0},\"xpubpostxreg\":false}", meta.toString());

        PairingPayload.PairingDojo pairingDojo = backup.computePairingDojo();
        Assertions.assertNull(pairingDojo);
    }
}
