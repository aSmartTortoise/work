package com.voyah.ai.basecar.llm;

import com.voice.sdk.constant.UiConstant;
import com.voice.sdk.device.llm.LLMTextInterface;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.util.MapUtil;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.window.model.CardInfo;
import com.voyah.cockpit.window.model.ChatMessage;
import com.voyah.cockpit.window.model.DomainType;
import com.voyah.cockpit.window.model.ViewType;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.status.StreamMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * author : jie wang
 * date : 2025/3/6 17:34
 * description :
 */
public class LLMTextInterfaceImpl implements LLMTextInterface {

    private static final String TAG = "LLMTextInterfaceImpl";

    private CardInfo mCardInfo;

    private LLMTextInterfaceImpl() {
    }

    public static LLMTextInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void constructCardInfo(String content, String topicType, String requestId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setStreamMode(StreamMode.NOT_STREAM_MODE);

        LogUtils.d(TAG, "getCardInfoNotStream content:" + content);
        chatMessage.setContent(content);
        chatMessage.setTopicType(topicType);
        chatMessage.setItemType(ViewType.CHAT_TYPE_MAIN);

        mCardInfo = new CardInfo();
        String topicTypeLower = topicType.toLowerCase(Locale.getDefault());
        mCardInfo.setDomainType(topicTypeLower.startsWith("faq") ?
                DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM :
                DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM);

        mCardInfo.setFromGPTFlag(false);
        mCardInfo.setSessionId(requestId);
        mCardInfo.setRequestId(requestId);

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(chatMessage);
        mCardInfo.setChatMessages(chatMessages);
    }

    @Override
    public void constructStreamCardInfo(Map<String, Object> map) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setStreamMode(MapUtil.getInt(map, FlowContextKey.FC_STREAM_MODE));
        chatMessage.setContent(MapUtil.getStr(map, FlowContextKey.FC_NO_TASK_TEXT));
        chatMessage.setTotalLen(MapUtil.getInt(map, "totalLen"));
        chatMessage.setTopicType(MapUtil.getStr(map, FlowContextKey.FC_QUERY_CLASS_LABEL));
        chatMessage.setModelDeepFirstReason(MapUtil.getBool(map, FlowContextKey.FC_MODEL_DEEP_FIRST_REASON));
        chatMessage.setNoTaskReasonText(MapUtil.getStr(map, FlowContextKey.FC_NO_TASK_REASON_TEXT));
        LogUtils.d(TAG, "stream data: FirstReason = " + chatMessage.isModelDeepFirstReason()
            + "noTaskReasonText = " + chatMessage.getNoTaskReasonText());

        chatMessage.setItemType(ViewType.CHAT_TYPE_MAIN);
        mCardInfo = new CardInfo();
        String topicType = MapUtil.getStr(map, FlowContextKey.FC_QUERY_CLASS_LABEL);
        if (topicType != null) {
            switch (topicType) {
                case "faq":
                    mCardInfo.setDomainType(DomainType.DOMAIN_TYPE_FAQ);
                    break;
                case "chat":
                    mCardInfo.setDomainType(DomainType.DOMAIN_TYPE_GOSSIP);
                    break;
                case "carbook":
                    mCardInfo.setDomainType(DomainType.DOMAIN_TYPE_CAR_ENCYCLOPEDIA);
                    break;
                default:
                    if (topicType.contains("faq")) {
                        mCardInfo.setDomainType(DomainType.DOMAIN_TYPE_FAQ);
                    } else {
                        mCardInfo.setDomainType(DomainType.DOMAIN_TYPE_ENCYCLOPEDIA);
                    }
                    break;
            }
        }

        mCardInfo.setFromGPTFlag(MapUtil.getBool(map, FlowContextKey.FC_IS_CLASS_LABEL_FROM_GPT));
        mCardInfo.setSessionId(MapUtil.getStr(map, FlowContextKey.FC_REQ_ID));
        mCardInfo.setRequestId(MapUtil.getStr(map, FlowContextKey.FC_REQ_ID));
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(chatMessage);
        mCardInfo.setChatMessages(chatMessages);
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
                    UiConstant.CardType.LLM_CARD, mCardInfo, mCardInfo.getSessionId(), business, location);
            mCardInfo = null;
        }
    }

    private static class Holder {
        private static final LLMTextInterfaceImpl INSTANCE = new LLMTextInterfaceImpl();
    }
}
