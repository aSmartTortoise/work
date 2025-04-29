package com.voyah.ai.basecar;

import com.voice.sdk.constant.UiConstant;
import com.voice.sdk.device.StockInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.window.model.CardInfo;
import com.voyah.cockpit.window.model.DomainType;
import com.voyah.cockpit.window.model.StockInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jie wang
 * date : 2025/3/6 9:40
 * description :
 */
public class StockInterfaceImpl implements StockInterface {

    private static final String TAG = "StockImpl";

    private CardInfo mCardInfo;

    private StockInterfaceImpl() {
    }

    public static StockInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void constructCardInfo(String dataInfo, String requestId) {
        LogUtils.d(TAG, "constructCardInfo stockJson:" + dataInfo);
        try {
            JSONObject stockJson = new JSONObject(dataInfo);
            if (stockJson.has("extraData")) {
                String extraDataStr = stockJson.getString("extraData");
                JSONObject extraDataJson = new JSONObject(extraDataStr);
                if (extraDataJson.has("stockInfo")) {
                    String stockInfoStr = extraDataJson.getString("stockInfo");
                    JSONObject stockInfoJson = new JSONObject(stockInfoStr);
                    LogUtils.d(TAG, "constructCardInfo stockInfoJson:" + stockInfoJson);
                    mCardInfo = new CardInfo();
                    StockInfo stockInfo = new StockInfo();
                    if (stockInfoJson.has("name")) {
                        String name = stockInfoJson.getString("name");
                        stockInfo.setName(name);
                    }

                    if (stockInfoJson.has("code")) {
                        String code = stockInfoJson.getString("code");
                        stockInfo.setCode(code);
                    }

                    if (stockInfoJson.has("update_time")) {
                        String updateTime = stockInfoJson.getString("update_time");
                        stockInfo.setDate(updateTime);
                    }

                    if (stockInfoJson.has("newestprice")) {
                        double newestPrice = stockInfoJson.getDouble("newestprice");
                        stockInfo.setPrice(newestPrice);
                    }

                    if (stockInfoJson.has("changeamount")) {
                        double changeAmount = stockInfoJson.getDouble("changeamount");
                        stockInfo.setPriceAmplitude(changeAmount);
                    }

                    if (stockInfoJson.has("changerate")) {
                        double changeRate = stockInfoJson.getDouble("changerate");
                        stockInfo.setAmplitudeRate(changeRate);
                    }

                    if (stockInfoJson.has("currency")) {
                        String currency = stockInfoJson.getString("currency");
                        stockInfo.setCurrency(currency);
                    }

                    mCardInfo.setDomainType(DomainType.DOMAIN_TYPE_STOCK);
                    mCardInfo.setSessionId(requestId);
                    mCardInfo.setRequestId(requestId);
                    List<StockInfo> stockInfos = new ArrayList<>();
                    stockInfos.add(stockInfo);
                    mCardInfo.setStockInfos(stockInfos);
                } else {
                    LogUtils.d(TAG, "constructCardInfo not have data ...");
                }
            }
        } catch (JSONException e) {
            LogUtils.w(TAG, "constructCardInfo e:" + e);
        }
    }

    @Override
    public boolean isCardInfoEmpty() {
        return mCardInfo == null;
    }

    @Override
    public void onShowUI(String business, int location) {
        LogUtils.i(TAG, "onShowUI business:" + business + " location:" + location);
        if (!isCardInfoEmpty()) {
            UIMgr.INSTANCE.showCard(
                    UiConstant.CardType.STOCK_CARD, mCardInfo, mCardInfo.getSessionId(), business, location);
            mCardInfo = null;
        }
    }

    private static class Holder {
        private static final StockInterfaceImpl INSTANCE = new StockInterfaceImpl();
    }
}
