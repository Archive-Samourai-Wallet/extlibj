package com.samourai.wallet.send;

import com.samourai.wallet.SamouraiWalletConst;
import com.samourai.wallet.bipFormat.BipFormat;
import com.samourai.wallet.bipFormat.BipFormatSupplier;
import com.samourai.wallet.send.provider.UtxoProvider;
import com.samourai.wallet.send.spend.SpendBuilder;
import com.samourai.wallet.util.FeeUtil;
import com.samourai.whirlpool.client.wallet.beans.WhirlpoolAccount;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class StonewallUtil {
    private static final Logger log = LoggerFactory.getLogger(StonewallUtil.class);

    private static StonewallUtil instance = null;

    private StonewallUtil() {
        super();
    }

    public static StonewallUtil getInstance() {
        if (instance == null) {
            instance = new StonewallUtil();
        }
        return instance;
    }

    // this will increment change index
    public Pair<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>> stonewall(List<UTXO> utxos, List<UTXO> utxosBis, BigInteger spendAmount, String address, WhirlpoolAccount account, UtxoProvider utxoProvider, BipFormat forcedChangeFormat, NetworkParameters params, BigInteger feePerKb) {

        Triple<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>, ArrayList<UTXO>> set0 = stonewallSet(utxos, spendAmount, address, null, account, null, utxoProvider, forcedChangeFormat, params, feePerKb);
        if(set0 == null)    {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("set0 utxo returned:" + set0.getRight().toString());
        }

        long set0Value = 0L;
        for(UTXO u : set0.getRight())   {
            set0Value += u.getValue();
        }

        long utxosBisValue = 0L;
        if(utxosBis != null)    {
            for(UTXO u : utxosBis)   {
                utxosBisValue += u.getValue();
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("set0 value:" + set0Value);
            log.debug("utxosBis value:" + utxosBisValue);
        }

        List<UTXO> _utxo = null;
        if(set0.getRight() != null && set0.getRight().size() > 0 && set0Value > spendAmount.longValue())    {
            if (log.isDebugEnabled()) {
                log.debug("set0 selected for 2nd pass");
            }
            _utxo = set0.getRight();
        }
        else if(utxosBis != null && utxosBisValue > spendAmount.longValue())   {
            if (log.isDebugEnabled()) {
                log.debug("utxosBis selected for 2nd pass");
            }
            _utxo = utxosBis;
        }
        else    {
            return null;
        }
        Triple<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>, ArrayList<UTXO>> set1 = stonewallSet(_utxo, spendAmount, address, set0.getLeft(), account, set0.getMiddle(), utxoProvider, forcedChangeFormat, params, feePerKb);
        if(set1 == null)    {
            return null;
        }

        Pair<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>> ret = Pair.of(new ArrayList<MyTransactionOutPoint>(), new ArrayList<TransactionOutput>());

        ret.getLeft().addAll(set0.getLeft());
        ret.getLeft().addAll(set1.getLeft());
//        ret.getRight().addAll(set0.getMiddle());
        ret.getRight().addAll(set1.getMiddle());

        return ret;
    }

    // this will increment change index
    private Triple<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>, ArrayList<UTXO>> stonewallSet(List<UTXO> utxos, BigInteger spendAmount, String address, List<MyTransactionOutPoint> firstPassOutpoints, WhirlpoolAccount account, List<TransactionOutput> outputs0, UtxoProvider utxoProvider, BipFormat forcedChangeFormat, NetworkParameters params, BigInteger feePerKb) {

        if(utxos == null || utxos.size() == 0)    {
            return null;
        }

        List<String> seenPreviousSetHash = null;
        if(firstPassOutpoints != null)    {
            seenPreviousSetHash = new ArrayList<String>();

            for(MyTransactionOutPoint outpoint : firstPassOutpoints)   {
                seenPreviousSetHash.add(outpoint.getHash().toString());
            }
        }

        Triple<Integer,Integer,Integer> firstPassOutpointTypes = null;
        if(firstPassOutpoints != null)    {
            firstPassOutpointTypes = FeeUtil.getInstance().getOutpointCount(new Vector<MyTransactionOutPoint>(firstPassOutpoints), params);
        }
        else    {
            firstPassOutpointTypes = Triple.of(0, 0, 0);
        }

        long totalOutpointsAmount = UTXO.sumValue(utxos);
        if (log.isDebugEnabled()) {
            log.debug("total outputs amount:" + totalOutpointsAmount);
            log.debug("spend amount:" + spendAmount.toString());
            log.debug("utxos:" + utxos.size());
        }

        if(totalOutpointsAmount <= spendAmount.longValue())    {
            if (log.isDebugEnabled()) {
                log.debug("spend amount must be > total amount available");
            }
            return null;
        }




        List<MyTransactionOutPoint> selectedOutpoints = new ArrayList<MyTransactionOutPoint>();
        BigInteger selectedValue = BigInteger.ZERO;
        BigInteger biFee = BigInteger.ZERO;
        List<TransactionOutput> txOutputs = new ArrayList<TransactionOutput>();
        TransactionOutput txSpendOutput = null;
        TransactionOutput txChangeOutput = null;
        HashMap<String,MyTransactionOutPoint> seenOutpoints = new HashMap<String,MyTransactionOutPoint>();
        List<MyTransactionOutPoint> recycleOutPoints = new ArrayList<MyTransactionOutPoint>();
        List<UTXO> recycleUTXOs = new ArrayList<UTXO>();

        BigInteger bDust = firstPassOutpoints == null ? BigInteger.ZERO : SamouraiWalletConst.bDust;

        // select utxos until > spendAmount * 2
        // create additional change output(s)
        int idx = 0;
        for (int i = 0; i < utxos.size(); i++) {

            UTXO utxo = utxos.get(i);

            boolean utxoIsSelected = false;

            recycleOutPoints.clear();

            for(MyTransactionOutPoint op : utxo.getOutpoints())   {
                String hash = op.getHash().toString();
                if(seenPreviousSetHash != null && seenPreviousSetHash.contains(hash))    {
                    ;
                }
                else if(!seenOutpoints.containsKey(hash))    {
                    seenOutpoints.put(hash,op);
                    selectedValue = selectedValue.add(BigInteger.valueOf(op.getValue().longValue()));
                    if (log.isDebugEnabled()) {
                        log.debug("selected:" + i + "," + op.getHash().toString() + "," + op.getValue().longValue());
                    }
                    utxoIsSelected = true;
                }
                else if(op.getValue().longValue() > seenOutpoints.get(hash).getValue().longValue()) {
                    recycleOutPoints.add(seenOutpoints.get(hash));
                    seenOutpoints.put(hash,op);
                    selectedValue = selectedValue.subtract(BigInteger.valueOf(seenOutpoints.get(hash).getValue().longValue()));
                    selectedValue = selectedValue.add(BigInteger.valueOf(op.getValue().longValue()));
                    if (log.isDebugEnabled()) {
                        log.debug("selected (replace):" + i + "," + op.getHash().toString() + "," + op.getValue().longValue());
                    }
                    utxoIsSelected = true;
                }
                else    {
                    ;
                }

                selectedOutpoints.clear();
                selectedOutpoints.addAll(seenOutpoints.values());
            }

            if(recycleOutPoints.size() > 0)    {
                UTXO recycleUTXO = new UTXO();
                recycleUTXO.setOutpoints(recycleOutPoints);
                recycleUTXOs.add(recycleUTXO);
            }

            if(utxoIsSelected)    {
                idx++;
            }

            if(firstPassOutpoints != null)    {
                Triple<Integer,Integer,Integer> outputTypes = FeeUtil.getInstance().getOutpointCount(new Vector<MyTransactionOutPoint>(selectedOutpoints), params);
                biFee = FeeUtil.getInstance().estimatedFeeSegwit(firstPassOutpointTypes.getLeft() + outputTypes.getLeft(), firstPassOutpointTypes.getMiddle() + outputTypes.getMiddle(), firstPassOutpointTypes.getRight() + outputTypes.getRight(), 4, 0, feePerKb);
            }

            if(selectedValue.compareTo(spendAmount.add(biFee).add(bDust)) > 0)    {
                break;
            }

        }

        if(selectedValue.compareTo(spendAmount.add(biFee).add(bDust)) <= 0)    {
            return null;
        }

        List<MyTransactionOutPoint> _selectedOutpoints = new ArrayList<MyTransactionOutPoint>();
        Collections.sort(selectedOutpoints, new UTXO.OutpointComparator());
        long _value = 0L;
        for(MyTransactionOutPoint op : selectedOutpoints)   {
            _selectedOutpoints.add(op);
            _value += op.getValue().longValue();
            if(firstPassOutpoints != null)    {
                Triple<Integer,Integer,Integer> outputTypes = FeeUtil.getInstance().getOutpointCount(new Vector<MyTransactionOutPoint>(_selectedOutpoints), params);
                biFee = FeeUtil.getInstance().estimatedFeeSegwit(firstPassOutpointTypes.getLeft() + outputTypes.getLeft(), firstPassOutpointTypes.getMiddle() + outputTypes.getMiddle(), firstPassOutpointTypes.getRight() + outputTypes.getRight(), 4, 0, feePerKb);
            }
            if(_value > spendAmount.add(biFee).add(bDust).longValue())    {
                break;
            }
        }
        selectedValue = BigInteger.valueOf(_value);
        selectedOutpoints.clear();
        selectedOutpoints.addAll(_selectedOutpoints);

        if (log.isDebugEnabled()) {
            log.debug("utxos idx:" + idx);
        }

        List<UTXO> _utxos = new ArrayList<>(utxos.subList(idx, utxos.size()));
        if (log.isDebugEnabled()) {
            log.debug("utxos after selection:" + _utxos.size());
        }
        _utxos.addAll(recycleUTXOs);
        if (log.isDebugEnabled()) {
            log.debug("utxos after adding recycled:" + _utxos.size());
        }
        BigInteger changeDue = selectedValue.subtract(spendAmount);

        if(firstPassOutpoints != null)    {
            Triple<Integer,Integer,Integer> outputTypes = FeeUtil.getInstance().getOutpointCount(new Vector<MyTransactionOutPoint>(selectedOutpoints), params);
            biFee = FeeUtil.getInstance().estimatedFeeSegwit(firstPassOutpointTypes.getLeft() + outputTypes.getLeft(), firstPassOutpointTypes.getMiddle() + outputTypes.getMiddle(), firstPassOutpointTypes.getRight() + outputTypes.getRight(), 4, 0, feePerKb);
            if (log.isDebugEnabled()) {
                log.debug("biFee:" + biFee.toString());
            }
            if(biFee.mod(BigInteger.valueOf(2L)).compareTo(BigInteger.ZERO) != 0)    {
                biFee = biFee.add(BigInteger.ONE);
            }
            if (log.isDebugEnabled()) {
                log.debug("biFee pair:" + biFee.toString());
            }
        }

        if(changeDue.subtract(biFee.divide(BigInteger.valueOf(2L))).compareTo(SamouraiWalletConst.bDust) > 0)    {
            changeDue = changeDue.subtract(biFee.divide(BigInteger.valueOf(2L)));
            if (log.isDebugEnabled()) {
                log.debug("fee set1:" + biFee.divide(BigInteger.valueOf(2L)).toString());
            }
        }
        else    {
            return null;
        }

        if(outputs0 != null && outputs0.size() == 2)    {
            TransactionOutput changeOutput0 = outputs0.get(1);
            BigInteger changeDue0 = BigInteger.valueOf(changeOutput0.getValue().longValue());
            if(changeDue0.subtract(biFee.divide(BigInteger.valueOf(2L))).compareTo(SamouraiWalletConst.bDust) > 0)    {
                changeDue0 = changeDue0.subtract(biFee.divide(BigInteger.valueOf(2L)));
                if (log.isDebugEnabled()) {
                    log.debug("fee set0:" + biFee.divide(BigInteger.valueOf(2L)).toString());
                }
            }
            else    {
                return null;
            }
            changeOutput0.setValue(Coin.valueOf(changeDue0.longValue()));
            outputs0.set(1, changeOutput0);
        }

        try {
            BipFormatSupplier bipFormatSupplier = utxoProvider.getBipFormatSupplier();
            String _address = null;
            if(firstPassOutpoints == null)    {
                _address = address;
            }
            else    {
                //
                // type of address for 'mixed' amount must match type of address for destination
                //
                BipFormat mixedFormat = SpendBuilder.computeAddressFormat(forcedChangeFormat, address, bipFormatSupplier, params);
                _address = utxoProvider.getNextChangeAddress(account, mixedFormat, true);
            }
            txSpendOutput = bipFormatSupplier.getTransactionOutput(_address, spendAmount.longValue(), params);
            txOutputs.add(txSpendOutput);

            //
            // inputs are pre-grouped by type
            // type of address for change must match type of address for inputs
            //
            String utxoAddress = utxos.get(0).getOutpoints().get(0).getAddress();
            BipFormat changeFormat = SpendBuilder.computeAddressFormat(forcedChangeFormat, utxoAddress, bipFormatSupplier, params);
            String changeAddress = utxoProvider.getNextChangeAddress(account, changeFormat, true);
            txChangeOutput = bipFormatSupplier.getTransactionOutput(changeAddress, changeDue.longValue(), params);
            txOutputs.add(txChangeOutput);
        }
        catch(Exception e) {
            return null;
        }

        long inValue = 0L;
        for(MyTransactionOutPoint outpoint : selectedOutpoints)   {
            inValue += outpoint.getValue().longValue();
            if (log.isDebugEnabled()) {
                log.debug("input:" + outpoint.getHash().toString() + "-" + outpoint.getIndex() + "," + outpoint.getValue().longValue());
            }
        }
        long outValue = 0L;
        for(TransactionOutput tOut : txOutputs)   {
            outValue += tOut.getValue().longValue();
            if (log.isDebugEnabled()) {
                log.debug("output:" + tOut.toString() + "," + tOut.getValue().longValue());
            }
        }

        Triple<ArrayList<MyTransactionOutPoint>, ArrayList<TransactionOutput>, ArrayList<UTXO>> ret = Triple.of(new ArrayList<MyTransactionOutPoint>(), new ArrayList<TransactionOutput>(), new ArrayList<UTXO>());
        ret.getLeft().addAll(selectedOutpoints);
        ret.getMiddle().addAll(txOutputs);
        if(outputs0 != null)    {
            ret.getMiddle().addAll(outputs0);
        }
        ret.getRight().addAll(_utxos);

        outValue += biFee.longValue();

        if (log.isDebugEnabled()) {
            log.debug("inputs:" + inValue);
            log.debug("outputs:" + outValue);
        }

        return ret;

    }
}
