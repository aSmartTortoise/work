package com.voyah.ai.basecar.phone;

import static com.voice.sdk.constant.PhoneConstants.PKG_BT_PHONE;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.forbidden.ForbiddenConstants;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.device.phone.bean.CallLogInfo;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UiInterface;
import com.voice.sdk.device.ui.listener.UICardListener;
import com.voyah.ai.basecar.manager.FeedbackManager;
import com.voyah.ai.basecar.phone.manager.BluetoothPhoneManager;
import com.voyah.ai.basecar.phone.manager.InnerServiceConnection;
import com.voyah.ai.basecar.system.MegaForegroundUtils;
import com.voyah.ai.common.ParamsGather;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.PinyinUtils;
import com.voyah.ai.voice.platform.soa.api.parameter.EnvDataConfig;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;
import com.voyah.cockpit.btphone.IVoiceCallBack;
import com.voyah.cockpit.btphone.bean.ContactPhoneInfo;
import com.voyah.cockpit.window.model.ViewType;
import com.voyah.ds.common.entity.domains.call.ContactInfo;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy 电话SDK接口实现
 * @data:2024/1/20
 **/
//todo:添加搜索匹配处理
public class PhoneInterfaceImpl implements PhoneInterface {
    private static final String TAG = PhoneInterfaceImpl.class.getSimpleName();

    private static final PhoneInterfaceImpl phoneInterfaceImpl = new PhoneInterfaceImpl();
    //来电
    private static final String INCOMING_CALL = "State.INCOMING_CALL";

    //去电
    private static final String OUTGOING_CALL = "State.OUTGOING_CALL";

    private static final int UPLOAD_CONTACTS_CODE = 0;
    private static final int UNLOAD_CONTACTS_CODE = 1;
    private static final int GET_CONTACT_INFO_LIST = 2;


    private BluetoothPhoneManager bluetoothPhoneManager;
    private static UiInterface uiInterface;

    private VoiceImpl mVoiceImpl;

    private List<ContactInfo> mContactInfoList = new ArrayList<>();

    private List<CallLogInfo> callLogInfos = new ArrayList<>(); //最近通话

    private Map<String, Object> sceneParams = new HashMap<>(); //上报场景附带参数

    private List<String> asrContactInfoList = new ArrayList<>(); //asr辅助识别使用

    private boolean isSyncContacting = true;//语音是否正在同步通讯录中

    private Handler handler;

    private HandlerThread handlerThread;

    private Context mContext;

    private List<ContactNumberInfo> currentInfoList = new ArrayList<>();

    private List<com.voyah.cockpit.btphone.bean.ContactInfo> contactInfoList = new ArrayList<>();
    private int currentBatchIndex = 0;
    private int batchSize = 100;

    private static final List<String> phoneInCallScene = Arrays.asList("接听", "挂断");
    private static final List<String> phoneOutCallScene = Arrays.asList("挂断");

    private PhoneInterfaceImpl() {

    }

    public static PhoneInterfaceImpl getInstance() {
        return phoneInterfaceImpl;
    }

    @Override
    public void init() {
        this.mContext = Utils.getApp();
        initHandler();
        bluetoothPhoneManager = BluetoothPhoneManager.getInstance();
        bluetoothPhoneManager.registerListener(phoneCallBack);
        bluetoothPhoneManager.bindService(mContext, connection);
        mVoiceImpl = VoiceImpl.getInstance();
        uiInterface = DeviceHolder.INS().getDevices().getUiCardInterface();
        LogUtils.d(TAG, "init uiInterface:" + (uiInterface == null));
    }

    private void initHandler() {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case UPLOAD_CONTACTS_CODE:
                        uploadContactInfo();
                        break;
                    case UNLOAD_CONTACTS_CODE:
                        unloadContactInfo();
                        break;
                    case GET_CONTACT_INFO_LIST:
                        getPhoneContactInfoListBath();
                }
            }
        };
    }


    private final InnerServiceConnection connection = new InnerServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, boolean connectState) {
            LogUtils.i(TAG, "onServiceConnected componentName is " + componentName + " connectState is " + connectState);
            //避免调用同步后，蓝牙电话不回调
            getContactListHandler();
            ParamsGather.isBtConnect = getBluetoothConnectState() == 2;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtils.i(TAG, "onServiceDisconnected componentName is " + componentName);
            //todo:清除本地通讯录数据
            mContactInfoList.clear();
//            setSyncContactStatus(1);
            sendEmptyHandler(UNLOAD_CONTACTS_CODE);
            ParamsGather.isBtConnect = false;
        }

        @Override
        public void onBindingDied(ComponentName componentName) {
            LogUtils.i(TAG, "onBindingDied");
        }

        @Override
        public void onNullBinding(ComponentName componentName) {
            LogUtils.i(TAG, "onNullBinding");
        }
    };

    private final IVoiceCallBack phoneCallBack = new IVoiceCallBack.Stub() {
        @Override
        public void onBluetoothState(int state) throws RemoteException {
            // 0:蓝牙连接断开 1:蓝牙正在连接 2:已连接
            LogUtils.i(TAG, "onBluetoothState state is " + state);
            ParamsGather.isBtConnect = state == 2;
            if (state == 2) {
                getContactListHandler();
            } else if (state == 0) {
                //todo:清除本地通讯录数据
                mContactInfoList.clear();
                ParamsGather.isBtConnect = false;
                sendEmptyHandler(UNLOAD_CONTACTS_CODE);
            }
        }

        @Override
        public void onPhoneCallStateChanged(String number, String name, int state) throws RemoteException {
            //1：呼出中  2：来电  4：通话中(接听)  7：挂断 9：预呼出 10：呼出-手动挂断(貌似是ftp协议断开吧)
            LogUtils.i(TAG, "onPhoneCallStateChanged name is " + name + " ,number is " + number + " ,state is " + state);
            sceneParams.clear();
            switch (state) {
                case 1:
                case 9:
                    //去电
                    forceExitAll();
                    mVoiceImpl.addAndRemoveSceneWord(phoneOutCallScene);
                case 2:
                    //来电
                    forceExitAll();
                    mVoiceImpl.addAndRemoveSceneWord(phoneInCallScene);

//                    mVoiceImpl.exDialog();
//                    mVoiceImpl.enableWakeup(false);
//                    VoiceImpl.getInstance().sceneReport(OUTGOING_CALL, "", sceneParams);
                    break;
//                case 2:
//                    mVoiceImpl.exDialog();
//                    mVoiceImpl.enableWakeup(false);
//                    VoiceImpl.getInstance().sceneReport(INCOMING_CALL, "", sceneParams);
//                    break;
                case 4:
                    mVoiceImpl.addAndRemoveSceneWord(new ArrayList<>());

                    DeviceHolder.INS().getDevices().getVoiceCarSignal().deviceForbiddenStatus(TAG, ForbiddenConstants.UNABLE_RECORD);
                    break;
                case 7:
                case 10:
                    int bluetoothCallState = getBluetoothCallState();
                    LogUtils.d(TAG, "bluetoothCallState:" + bluetoothCallState);
//                    mVoiceImpl.enableWakeup(true);
                    if (-1 == bluetoothCallState) {
                        mVoiceImpl.addAndRemoveSceneWord(new ArrayList<>());
                        DeviceHolder.INS().getDevices().getVoiceCarSignal().deviceForbiddenStatus(TAG, ForbiddenConstants.ENABLE_RECORD);
                    }
                    break;

            }
        }

        @Override
        public void syncContactFinish() throws RemoteException {
            //蓝牙电话通讯录同步完成
            //todo:获取通讯录数据
            isSyncContacting = false;
            mContactInfoList.clear();
            getContactListHandler();
//            Gson gson = new Gson();
//            String jsonString = gson.toJson(mContactInfoList);
//            LogUtils.i(TAG, "jsonString\n" + jsonSt
//            ring);
        }

        @Override
        public void syncContactFail() throws RemoteException {
            LogUtils.i(TAG, "syncContactFail");
            //todo:获取通讯录失败-添加重试?
            mContactInfoList.clear();
            isSyncContacting = false;
            sendEmptyHandler(UNLOAD_CONTACTS_CODE);
        }

        @Override
        public void syncingContact() throws RemoteException {
            isSyncContacting = true;
            LogUtils.d(TAG, "syncingContact");
        }
    };

    private void getPhoneContactInfoList() {
        List<com.voyah.cockpit.btphone.bean.ContactInfo> btContact = bluetoothPhoneManager.getContactInfoList();
        if (btContact != null && !btContact.isEmpty()) {
            syncVoiceContact(btContact);
        } else {
            LogUtils.d(TAG, "getPhoneContactInfoList contact info list is empty");
        }
        isSyncContacting = false;
    }

    private void getContactListHandler() {
        int contactSize = bluetoothPhoneManager.getContactsSize();
        LogUtils.d(TAG, "contactSize:" + contactSize);
        if (contactSize > 200) {
            if (null != handler) {
                handler.removeMessages(GET_CONTACT_INFO_LIST);
                handler.sendEmptyMessage(GET_CONTACT_INFO_LIST);
            }
        } else {
            getPhoneContactInfoList();
        }
    }

    //分批次获取(避免超出binder最大容量)
    private void getPhoneContactInfoListBath() {
        int contactsSiz = getContactsSize();
        int start = (currentBatchIndex * batchSize) > 0 ? currentBatchIndex * batchSize - 1 : 0;
        int end = Math.min(start + batchSize, contactsSiz);
        LogUtils.d(TAG, "getPhoneContactInfoList start:" + start + " ,end:" + end + " ,currentBatchIndex:" + currentBatchIndex + " ,contactsSiz:" + contactsSiz);
        if (start == 0) {
            contactInfoList.clear();
        }
        List<com.voyah.cockpit.btphone.bean.ContactInfo> btContact = bluetoothPhoneManager.getContactInfoList(start, end);
        if (btContact != null && !btContact.isEmpty()) {
            LogUtils.d(TAG, "btContact.size:" + btContact.size());
            contactInfoList.addAll(btContact);
            currentBatchIndex++;
        }
        if (end == contactsSiz) {
            syncVoiceContact(contactInfoList);
            isSyncContacting = false;
            currentBatchIndex = 0;
        } else {
            getContactListHandler();
        }

    }

    private int getContactsSize() {
        int contactSize = bluetoothPhoneManager.getContactsSize();
        LogUtils.d(TAG, "getContactsSize:" + contactSize);
        return contactSize;
    }

//    private void setSyncContactStatus(int flag) {
//        // 0:同步中 1:同步结束
//        isSyncContacting = flag == 0;
//        LogUtils.d(TAG, "setSyncContactStatus isSyncContacting:" + isSyncContacting);
//    }

    private void syncVoiceContact(List<com.voyah.cockpit.btphone.bean.ContactInfo> contactInfoList) {
        mContactInfoList.clear();
        LogUtils.i(TAG, "syncContactFinish  " + " ,contactInfoList.size is " + contactInfoList.size());
        if (contactInfoList.isEmpty()) {
            LogUtils.i(TAG, "contactInfoList is empty...");
        }

        for (com.voyah.cockpit.btphone.bean.ContactInfo contactInfo : contactInfoList) {
            ContactInfo contactsInfo = new ContactInfo();
            contactsInfo.setPosition(contactInfo.getPosition());
            contactsInfo.setName(contactInfo.getName().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", ""));
            String namePinYin = PinyinUtils.pinyin(contactsInfo.getName());
            contactsInfo.setShowNumberIndex(contactInfo.getShowNumberIndex());
            contactsInfo.setNamePinYin(namePinYin);
//                contactsInfo.setHeadPhotoDB(contactInfo.getHeadPhotoDB());
//                contactsInfo.setPinyinFirst(contactInfo.getPinyinFirst());
//                contactsInfo.setMatchPin(contactInfo.getMatchPin());
//                contactsInfo.setMatchType(contactInfo.getMatchType());
//                contactsInfo.setHighlightedStart(contactInfo.getHighlightedStart());
//                contactsInfo.setHighlightedEnd(contactInfo.getHighlightedEnd());
            contactsInfo.setSource_type(1);
            List<ContactNumberInfo> contactNumberInfoList = new ArrayList<>();
            for (ContactPhoneInfo phoneInfo : contactInfo.getPhoneInfo()) {
                ContactNumberInfo contactNumberInfo = new ContactNumberInfo();
//                    contactNumberInfo.setHighlightedEnd(phoneInfo.getHighlightedEnd());
//                    contactNumberInfo.setHighlightedStart(phoneInfo.getHighlightedStart());
//                    contactNumberInfo.setNumberMatched(phoneInfo.isNumberMatched());
                contactNumberInfo.setLocation(phoneInfo.getLocation());
                contactNumberInfo.setName(contactInfo.getName().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", ""));
                contactNumberInfo.setNumber(phoneInfo.getNumber());
                contactNumberInfo.setOperator(phoneInfo.getOperator());
                contactNumberInfo.setType(phoneInfo.getType());
                contactNumberInfo.setSourceType(1);
                contactNumberInfo.setNamePinYin(namePinYin);
                contactNumberInfo.setPhotoId(0);//暂时默认为0，后续使用到再修改
                contactNumberInfoList.add(contactNumberInfo);
            }
            contactsInfo.setNumberInfoList(contactNumberInfoList);
            mContactInfoList.add(contactsInfo);
        }

//        Gson gson = new Gson();
//        LogUtils.i(TAG, "gson is " + gson.toJson(mContactInfoList));

        //上传通讯录
        sendEmptyHandler(UPLOAD_CONTACTS_CODE);
    }


    @Override
    public int openBtApk() {
        if (null == bluetoothPhoneManager)
            return -1;
        int code = bluetoothPhoneManager.openBtApk();
        LogUtils.i(TAG, "openBtApk code is " + code);
        return code;
    }

    @Override
    public int closeBtApk() {
        if (null == bluetoothPhoneManager)
            return -1;
        int code = bluetoothPhoneManager.closeBtApk();
        LogUtils.i(TAG, "closeBtApk code is " + code);
        return code;
    }

    @Override
    public int answerCall() {
        if (null == bluetoothPhoneManager)
            return -1;
        int code = bluetoothPhoneManager.answerCall();
        LogUtils.i(TAG, "answerCall code is " + code);
        return code;
    }

    @Override
    public int disconnectCall() {
        if (null == bluetoothPhoneManager)
            return -1;
        return bluetoothPhoneManager.disconnectCall();
    }

    @Override
    public int placeCall(String number) {
        if (null == bluetoothPhoneManager)
            return -1;
        UIMgr.INSTANCE.forceExitAll("phoneCall");
        mVoiceImpl.exDialog();
        return bluetoothPhoneManager.placeCall(number);
    }

    @Override
    public int getBluetoothConnectState() {
        if (null == bluetoothPhoneManager)
            return -1;
        //蓝牙连接状态 0：未连接 1：正在连接 2：已连接
        return bluetoothPhoneManager.getBluetoothConnectState();
    }

    public int getBluetoothCallState() {
        if (null == bluetoothPhoneManager)
            return -1;
        //未连接 -1  1：呼出中  2：来电  4：通话中(接听)  7：挂断
        return bluetoothPhoneManager.getBluetoothCallState();
    }

    @Override
    public int syncContact() {
        if (null == bluetoothPhoneManager)
            return -1;
        LogUtils.i(TAG, "syncContact isSyncContacting is " + isSyncContacting);
        isSyncContacting = true;
        return bluetoothPhoneManager.syncConcact();
    }

    @Override
    public void openBlueTooth() {
        LogUtils.d(TAG, "openBlueTooth");
        try {
            IFunctionManagerImpl.getInstance(Utils.getApp()).setBlueToothSwitch(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int openBluetoothSettings() {
        if (null == bluetoothPhoneManager)
            return -1;
        if (!isBtApkOpen())
            openBtApk();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int code = bluetoothPhoneManager.openBluetoothSettings();
        openBlueTooth();
        LogUtils.i(TAG, "openBluetoothSettings code is " + code);
        return code;
    }

    @Override
    public int setBluetoothPhoneTab(int tab) {
        if (null == bluetoothPhoneManager)
            return -1;
        //0：最近通话界面 1～：通讯录界面
        LogUtils.i(TAG, "setBluetoothPhoneTab tab is " + tab);
        return bluetoothPhoneManager.setBluetoothPhoneTab(tab);
    }

    @Override
    public int switchBluetoothPhoneTab() {
        if (null == bluetoothPhoneManager)
            return -1;
        return bluetoothPhoneManager.switchBluetoothPhoneTab();
    }

    @Override
    public String getLastIncomingNumber() {
        if (null == bluetoothPhoneManager)
            return "";
        String lastInComingNumber = bluetoothPhoneManager.getLastIncomingNumber();
        LogUtils.i(TAG, "getLastIncomingNumber lastInComingNumber is " + lastInComingNumber);
        return lastInComingNumber;
    }

    @Override
    public String getLastOutgoingNumber() {
        if (null == bluetoothPhoneManager)
            return "";
        String lastOutgoingNumber = bluetoothPhoneManager.getLastOutgoingNumber();
        LogUtils.i(TAG, "getLastIncomingNumber lastOutgoingNumber is " + lastOutgoingNumber);
        return lastOutgoingNumber;
    }

    @Override
    public List<ContactInfo> getContactInfoList() {
        //todo:通讯录数据+电话筛选都放在devices处理？
        //todo:测一下获取通讯录回调在哪个接口返回!!!!!!!!!!!!!!!
        return mContactInfoList;
    }

    @Override
    public List<CallLogInfo> getCallLogInfoList() {
        if (null == bluetoothPhoneManager)
            return null;
        callLogInfos.clear();
        List<com.voyah.cockpit.btphone.bean.CallLogInfo> list = bluetoothPhoneManager.getCallLogInfoList();
        for (com.voyah.cockpit.btphone.bean.CallLogInfo callLogInfo : list) {
            if (null != callLogInfo) {
                CallLogInfo callLogInfo1 = new CallLogInfo();
                callLogInfo1.setName(callLogInfo.getName());
                callLogInfo1.setType(callLogInfo.getType());
                callLogInfo1.headPhotoId = callLogInfo.headPhotoId;
                callLogInfo1.setNumber(callLogInfo.getNumber());
                callLogInfo1.setDate(callLogInfo.getDate());
                callLogInfo1.setTime(callLogInfo.getTime());
                callLogInfo1.setLongTime(callLogInfo.getLongTime());
                callLogInfo1.setCount(callLogInfo.getCount());
                callLogInfo1.setHighlightedStart(callLogInfo.getHighlightedStart());
                callLogInfo1.setHighlightedEnd(callLogInfo.getHighlightedEnd());
                callLogInfo1.setMatchPin(callLogInfo.getMatchPin());
                callLogInfo1.setNamePinYin(callLogInfo.getNamePinYin());
                callLogInfo1.setIndex(callLogInfo.getIndex());
                callLogInfo1.setMatchType(callLogInfo.getMatchType());
                callLogInfos.add(callLogInfo1);
            }
        }
        return callLogInfos;
    }

    @Override
    public boolean isSyncContacted() {
        LogUtils.i(TAG, "isSyncContacted mContactInfoList.size:" + mContactInfoList.size());
        return !mContactInfoList.isEmpty();
    }

    @Override
    public boolean isSyncContacting() {
        LogUtils.i(TAG, "isSyncContacting is " + isSyncContacting);
        return isSyncContacting;
    }

    @Override
    public boolean isBtApkOpen() {
        boolean isBtApkOpen = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(PKG_BT_PHONE, DeviceScreenType.fromValue(FuncConstants.VALUE_SCREEN_CENTRAL));
        LogUtils.i(TAG, "isBtApkOpen is " + isBtApkOpen);
        return isBtApkOpen;
    }

    @Override
    public boolean isBtConnect() {
        int btConnectState = getBluetoothConnectState();
        LogUtils.i(TAG, "btConnectState is " + btConnectState);
        return btConnectState == 2;
    }

    @Override
    public boolean isIncoming() {
        int callState = getBluetoothCallState();
        LogUtils.i(TAG, "isIncoming callState is " + callState);
        return callState == 2;
    }

    @Override
    public boolean isOutgoing() {
        int callState = getBluetoothCallState();
        LogUtils.i(TAG, "isOutgoing callState is " + callState);
        return callState == 1;
    }

    @Override
    public void uploadContact() {
        sendEmptyHandler(UPLOAD_CONTACTS_CODE);
    }


    private void sendEmptyHandler(int what) {
        if (null != handler) {
            handler.removeMessages(UPLOAD_CONTACTS_CODE);
            handler.removeMessages(UNLOAD_CONTACTS_CODE);
            handler.sendEmptyMessage(what);
        }
    }

    private void uploadContactInfo() {
        try {
            asrContactInfoList.clear();
            for (ContactInfo contactInfo : mContactInfoList) {
                asrContactInfoList.add(contactInfo.getName());
            }

            //todo:上传 上传id
//        mVoiceImpl.uploadContacts(asrContactInfoList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("function", "contacts");
            JSONArray jsonArray = new JSONArray(asrContactInfoList);
            jsonObject.put("data", jsonArray);
            LogUtils.i(TAG, "jsonObject:" + jsonObject.toString());
            FeedbackManager.get().uploadPersonalEntity("com.voyah.ai.voice", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void unloadContactInfo() {
        LogUtils.i(TAG, "unloadContactInfo ");
        asrContactInfoList.clear();
        //todo:上传 上传id
        mVoiceImpl.uploadPersonalEntity(EnvDataConfig.ENV_KEY_CONTACTS, asrContactInfoList);
    }

    @Override
    public void setCurrentInfoList(List<ContactNumberInfo> infos) {
        LogUtils.d(TAG, "setCurrentInfoList:" + (uiInterface == null) + " ,currentInfoList==null" + (null == currentInfoList));
        currentInfoList.clear();
        currentInfoList.addAll(infos);
        UIMgr.INSTANCE.addCardStateListener(uiCardListener);
    }

    @Override
    public boolean isYellowPageContainsName(List<ContactInfo> yellowPageList, String name) {
        return SearchContactUtils.isYellowPageContainsName(yellowPageList, name);
    }

    @Override
    public List<ContactInfo> searchListByName(String name, List<ContactInfo> list) {
        return SearchContactUtils.searchListByName(name, list);
    }

    @Override
    public List<ContactNumberInfo> selectByName(String name, List<ContactNumberInfo> list) {
        return SearchContactUtils.selectByName(name, list);
    }

    @Override
    public List<ContactNumberInfo> getContactToNumberList(List<ContactInfo> contactInfoList) {
        return SearchContactUtils.getContactToNumberList(contactInfoList);
    }

    @Override
    public String getNameByIndex(boolean isMinus, int index, List<ContactNumberInfo> numberInfoList) {
        return SearchContactUtils.getNameByIndex(isMinus, index, numberInfoList);
    }

    @Override
    public String getNumberByIndex(boolean isMinus, int index, List<ContactNumberInfo> numberInfoList) {
        return SearchContactUtils.getNumberByIndex(isMinus, index, numberInfoList);
    }

    @Override
    public List<ContactInfo> searchListByNumber(String number_front, String number_end, String number, List<ContactInfo> list) {
        return SearchContactUtils.searchListByNumber(number_front, number_end, number, list);
    }


    private void forceExitAll() {
        VoiceImpl.getInstance().exDialog();
        UIMgr.INSTANCE.forceExitAll("phone");
        DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
    }


    @Override
    public List<ContactInfo> searchListByNameAndNumber(String name, String number_front, String number_end, String number, List<ContactInfo> list) {
        return SearchContactUtils.searchListByNameAndNumber(name, number_front, number_end, number, list);
    }

    @Override
    public List<ContactNumberInfo> selectByNumber(String number_front, String number_end, String number, List<ContactNumberInfo> contactNumberInfoList) {
        return SearchContactUtils.selectByNumber(number_front, number_end, number, contactNumberInfoList);
    }

    @Override
    public List<ContactNumberInfo> selectByNameAndNumber(String name, String number_front, String number_end, String number, List<ContactNumberInfo> list) {
        return SearchContactUtils.selectByNameAndNumber(name, number_front, number_end, number, list);
    }

    public List<ContactNumberInfo> getCurrentInfoList() {
        return currentInfoList;
    }

    private static final UICardListener uiCardListener = new UICardListener() {
        @Override
        public void onCardItemClick(int position, int itemType, int screenType) {
            if (itemType == ViewType.BT_PHONE_TYPE) {
                LogUtils.d(TAG, "onCardItemClick position: " + position + " itemType: " + itemType);
                int currentPages = uiInterface.getCurrentPage(screenType);
                int index = (currentPages - 1) * 4 + position + 1;
                List<ContactNumberInfo> contactNumberInfoList = PhoneInterfaceImpl.getInstance().getCurrentInfoList();
                LogUtils.d(TAG, "onCardItemClick currentPages:" + currentPages + " ,index:" + index + " ,contactNumberInfoList.size:" + contactNumberInfoList.size());
                if (!contactNumberInfoList.isEmpty()) {
                    String preCallNumber = SearchContactUtils.getNumberByIndex(false, index, contactNumberInfoList);
                    String preCallName = SearchContactUtils.getNameByIndex(false, index, contactNumberInfoList);
                    LogUtils.d(TAG, "preCallNumber:" + preCallNumber + " ,preCallName:" + preCallName);
                    if (!StringUtils.isBlank(preCallNumber))
                        PhoneInterfaceImpl.getInstance().placeCall(preCallNumber);
                } else {
                    LogUtils.d(TAG, "contactNumberInfoList is empty");
                }
            }
        }

        @Override
        public void uiCardClose(String sessionId) {
            LogUtils.d(TAG, "uiCardClose");
            UIMgr.INSTANCE.removeCardStateListener(uiCardListener);
        }

        @Override
        public void uiModelCardClose(String requestId) {

        }

        @Override
        public void uiVpaClose() {
            LogUtils.d(TAG, "uiVpaClose");
        }
    };
}
