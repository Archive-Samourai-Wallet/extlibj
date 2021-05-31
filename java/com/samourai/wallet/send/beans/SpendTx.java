package com.samourai.wallet.send.beans;

import com.samourai.wallet.hd.AddressType;
import com.samourai.wallet.send.MyTransactionOutPoint;
import com.samourai.wallet.send.SendFactoryGeneric;
import com.samourai.wallet.send.UtxoProvider;
import com.samourai.wallet.send.spend.SpendSelection;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;

import java.util.List;
import java.util.Map;

public class SpendTx {
    private WhirlpoolAccount account;
    private AddressType changeType;
    private long amount;
    private long fee;
    private long change;
    private SpendSelection spendSelection;
    private Map<String, Long> receivers;
    private boolean rbfOptIn;

    public SpendTx(WhirlpoolAccount account, AddressType changeType, long amount, long fee, long change, SpendSelection spendSelection, Map<String, Long> receivers, boolean rbfOptIn) {
        this.account = account;
        this.changeType = changeType;
        this.amount = amount;
        this.fee = fee;
        this.spendSelection = spendSelection;
        this.receivers = receivers;
        this.change = change;
        this.rbfOptIn = rbfOptIn;
    }

    public Transaction sign(NetworkParameters params, UtxoProvider utxoProvider) throws Exception {
        // make tx
        final Transaction tx = SendFactoryGeneric.getInstance().makeTransaction(getSpendTo(), getSpendFrom(), rbfOptIn, params);
        if (tx == null) {
            throw new Exception("makeTransaction failed");
        }

        /*final RBFSpend rbf;
        if (rbfOptIn) {
            rbf = new RBFSpend();
            for (TransactionInput input : tx.getInputs()) {
                String _addr = TxUtil.getInstance().getToAddress(input.getConnectedOutput());
                AddressType addressType = AddressType.findByAddress(_addr, params);
                String path = APIFactory.getInstance(TxAnimUIActivity.this).getUnspentPaths().get(_addr);
                if (path != null) {
                    if (addressType == AddressType.SEGWIT_NATIVE || addressType == AddressType.SEGWIT_COMPAT) {
                        path += "/"+addressType.getPurpose();
                    }
                    rbf.addKey(input.getOutpoint().toString(), path);
                } else {
                    // TODO zeroleak paymentcodes
                    /*String pcode = BIP47Meta.getInstance().getPCode4Addr(_addr);
                    int idx = BIP47Meta.getInstance().getIdx4Addr(_addr);
                    rbf.addKey(input.getOutpoint().toString(), pcode + "/" + idx);*//*
                }
            }
        } else {
            rbf = null;
        }

        // TODO zeroleak strict mode
        /*
        final List<Integer> strictModeVouts = new ArrayList<Integer>();
        if (SendParams.getInstance().getDestAddress() != null && SendParams.getInstance().getDestAddress().compareTo("") != 0 &&
                PrefsUtil.getInstance(TxAnimUIActivity.this).getValue(PrefsUtil.STRICT_OUTPUTS, true) == true) {
            List<Integer> idxs = SendParams.getInstance().getSpendOutputIndex(tx);
            for(int i = 0; i < tx.getOutputs().size(); i++)   {
                if(!idxs.contains(i))   {
                    strictModeVouts.add(i);
                }
            }
        }*/

        // sign
        return SendFactoryGeneric.getInstance().signTransaction(tx, account, utxoProvider);
    }

    public AddressType getChangeType() {
        return changeType;
    }

    public long getAmount() {
        return amount;
    }

    public Long getFee() {
        return fee;
    }

    public long getChange() {
        return change;
    }

    public Map<String, Long> getSpendTo() {
        return receivers;
    }

    public List<MyTransactionOutPoint> getSpendFrom() {
        return spendSelection.getSpendFrom();
    }

    public SpendType getSpendType() {
        return spendSelection.getSpendType();
    }
}
