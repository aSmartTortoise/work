package com.voyah.ai.voice.agent.phone;

/**
 * @author:lcy
 * @data:2024/3/24
 **/
//public class KeyWordSelectUtil {
//    private static final String TAG = KeyWordSelectUtil.class.getSimpleName();
//
//    public static ClientAgentResponse keyWordSelect(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap, UiInterface uiInterface) {
//        String deviceScenarioState = (String) flowContext.get(Constant.PARAMS_DEVICE_SCENARIO_STATE);
//        List<ContactNumberInfo> flowNumberInfoList = (List<ContactNumberInfo>) flowContext.get(Constant.PARAMS_NUMBER_LIST);
//
//        String operation_type = (String) flowContext.get(Constant.PARAMS_OPERATION_TYPE);
//        boolean isDial = StringUtils.equals(operation_type, Constant.OperationType.DIAL);
//        String index = getParamKey(paramsMap, Constant.INDEX, 0);
//        String name = getParamKey(paramsMap, Constant.NAME, 0);
//        String number = getParamKey(paramsMap, Constant.NUMBER, 0);
//        String number_front = getParamKey(paramsMap, Constant.NUMBER_FRONT, 0);
//        String number_end = getParamKey(paramsMap, Constant.NUMBER_END, 0);
//        String search_Type = getFlowContextKey(Constant.PARAMS_SEARCH_TYPE, flowContext);
//        String preCallNumber;
//        int position = 0;
//        boolean isMinus = false;
//        LogUtils.i(TAG, "isDial is " + isDial + " ,index is " + index + " ,name is " + name + " ,number is " + number + " ,search_Type is " + search_Type
//                + " ,number_front is " + number_front + " ,number_end is " + number_end + " ,flowNumberInfoList " + ((null == flowNumberInfoList) ? "flowNumberInfoList is null" : flowNumberInfoList.size()));
//        if (!StringUtils.isBlank(deviceScenarioState)) {
//            if (!StringUtils.isBlank(number_front) && (number_front.length() < 3 || number_front.length() > 4)) {
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_CALL_NUMBER_FRONT);
//            } else if (!StringUtils.isBlank(number_end) && (number_end.length() < 3 || number_end.length() > 4)) {
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_CALL_NUMBER_END);
//            }
//            if (!StringUtils.isBlank(index)) {
//                position = Integer.parseInt(index);
//                isMinus = position < 0;
//                position = isMinus ? Math.abs(position) : position;
//            }
//            //1>.名字+号码+索引
//            if (!StringUtils.isBlank(name) && (!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))
//                    && (!StringUtils.isBlank(index))) {
//                if (StringUtils.equals(index, "0")) {
//                    //todo:TTS播报 超出选择范围 计数
//                    LogUtils.i(TAG, "TTS播报超出选择范围");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG);
//                }
//                //符合名字+号码组合的所有数据
//                List<ContactNumberInfo> selectContactNumberList = SearchContactUtils.selectByNameAndNumber(name, number_front, number_end, number, flowNumberInfoList);
//                if (selectContactNumberList.isEmpty()) {
//                    //todo:TTS播报未找到号码
//                    //todo:添加计数判断
//                    LogUtils.i(TAG, "TTS播报未找到号码");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_NUMBER_INVALID);
//                }
//
//                //多个号码
//                if (selectContactNumberList.size() > 1) {
//                    LogUtils.i(TAG, "TTS播报有多个号码");
//                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, selectContactNumberList, null, null, operation_type);
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, TTSAnsConstant.PHONE_FIND_MORE_NUMBER, "", TAG);
//                }
//                //单个联系人
//                //索引是否合法
//                if (position > selectContactNumberList.size()) {
//                    //todo:超出选择范围 计数
//                    LogUtils.i(TAG, "TTS播报 超出选择范围");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext,
//                            TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG, "");
//                }
//                //拨打
//                preCallNumber = SearchContactUtils.getNumberByIndex(isMinus, position, selectContactNumberList);
//                if (!StringUtils.isBlank(preCallNumber)) {
//                    //todo:TTS播报 即将呼叫+name or number
//                    LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//                }
//            } else if (!StringUtils.isBlank(name) && (!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))) {
//                //2>.名字+号码
//                List<ContactNumberInfo> selectContactNumberInfoList = SearchContactUtils.selectByNameAndNumber(name, number_front, number_end, number, flowNumberInfoList);
//                if (selectContactNumberInfoList.isEmpty()) {
//                    //todo:TTS播报未找到号码
//                    //todo:添加计数判断
//                    LogUtils.i(TAG, "TTS播报 未找到号码");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_NUMBER_INVALID);
//                }
//
//                if (selectContactNumberInfoList.size() > 1) {
//                    //todo:TTS播报：找到多个号码,你要打给第几个
//                    LogUtils.i(TAG, "TTS播报 找到多个号码,你要打给第几个 showUi");
//                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, selectContactNumberInfoList, null, null, operation_type);
//                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, TTSAnsConstant.PHONE_FIND_MORE_NUMBER, "", TAG);
//                    return clientAgentResponse;
//                }
//
//                //todo:TTS播报 即将呼叫+name or number
//                preCallNumber = selectContactNumberInfoList.get(0).getNumber();
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//            } else if (!StringUtils.isBlank(name) && !StringUtils.isBlank(index)) {
//                //3>.名字+索引
//                List<ContactNumberInfo> selectContactNumberList = SearchContactUtils.selectByNameAndNumber(name, number_front, number_end, number, flowNumberInfoList);
//
//                if (StringUtils.equals(index, "0") || position > selectContactNumberList.size()) {
//                    //todo:TTS播报 超出选择范围 计数
//                    LogUtils.i(TAG, "TTS播报 超出选择范围");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_OUT_OF_SELECT_RANG);
//                }
//
//                if (selectContactNumberList.isEmpty()) {
//                    //todo:添加计数判断
//                    LogUtils.i(TAG, "TTS播报 联系人不存在,请重新选择");
////                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_CONTACT_INVALID);
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_CONTACT_INVALID);
//                }
//
//                preCallNumber = SearchContactUtils.getNumberByIndex(isMinus, position, selectContactNumberList);
//                //todo:TTS播报 即将呼叫+name or number
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//
//            } else if ((!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))
//                    && (!StringUtils.isBlank(index))) {
//                //4>.号码+索引
//                if (StringUtils.equals(index, "0")) {
//                    //todo:TTS播报 超出选择范围 计数
//                    LogUtils.i(TAG, "TTS播报 不存在该号码");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_NUMBER_INVALID);
//                }
//                List<ContactNumberInfo> selectContactNumberInfoList = SearchContactUtils.selectByNumber(number_front, number_end, number, flowNumberInfoList);
//                if (selectContactNumberInfoList.isEmpty()) {
//                    //todo:TTS播报未找到号码
//                    //todo:添加计数判断
//                    LogUtils.i(TAG, "TTS播报 未找到号码");
////                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_FIND_FAIL_NUMBER);
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_FIND_FAIL_NUMBER);
//                }
//
//                if (position > selectContactNumberInfoList.size()) {
//                    //todo:TTS播报 超出选择范围 计数
//                    LogUtils.i(TAG, "TTS播报 超出范围,请在1到" + flowNumberInfoList.size() + "间选择");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_OUT_OF_RANG.replace("@position", selectContactNumberInfoList.size() + ""));
//                }
//                preCallNumber = SearchContactUtils.getNumberByIndex(isMinus, position, selectContactNumberInfoList);
//                //todo:TTS播报 即将呼叫+name or number
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//            } else if (!StringUtils.isBlank(name)) {
//                //5>.名字
//                List<ContactNumberInfo> selectContactNumberInfoList = SearchContactUtils.selectByName(name, flowNumberInfoList);
//                if (selectContactNumberInfoList.isEmpty()) {
//                    //todo:TTS播报 未找到联系人
//                    LogUtils.i(TAG, "TTS播报 未找到联系人");
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_CONTACT_INVALID);
//                }
//
//                if (selectContactNumberInfoList.size() > 1) {
//                    //todo:TTS播报：找到多个号码,你要打给第几个
//                    LogUtils.i(TAG, "TTS播报 找到多个号码,你要打给第几个");
//                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, selectContactNumberInfoList, null, null, operation_type);
//                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, TTSAnsConstant.PHONE_FIND_MORE_NUMBER, "", TAG);
//                    return clientAgentResponse;
//                }
//
//                //todo:TTS播报 即将呼叫+name or number
//                preCallNumber = selectContactNumberInfoList.get(0).getNumber();
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                uiInterface.closeCard();
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//            } else if ((!StringUtils.isBlank(number) || !StringUtils.isBlank(number_front) || !StringUtils.isBlank(number_end))) {
//                //6>.号码
//                List<ContactNumberInfo> selectContactNumberInfoList = SearchContactUtils.selectByNumber(number_front, number_end, number, flowNumberInfoList);
//                if (selectContactNumberInfoList.isEmpty()) {
//                    //todo:TTS播报 未找到指定号码
//                    LogUtils.i(TAG, "TTS播报 未找到号码");
////                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_FIND_FAIL_NUMBER);
//                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.KEEP_SCENE, flowContext, TTSAnsConstant.PHONE_FIND_FAIL_NUMBER);
//                }
//                if (selectContactNumberInfoList.size() > 1) {
//                    //todo:TTS 找到多个联系人,你要打给第几个
//                    LogUtils.i(TAG, "TTS播报 找到多个号码,你要打给第几个");
//                    PhoneFlowContextUtils.setFlowContextParams(flowContext, null, selectContactNumberInfoList, null, null, operation_type);
//                    ClientAgentResponse clientAgentResponse = new ClientAgentResponse(Constant.PhoneAgentResponseCode.MULTIPLE_NUMBER, flowContext, TTSAnsConstant.PHONE_FIND_MORE_NUMBER, "", TAG);
//                    return clientAgentResponse;
//                }
//
//                //todo:TTS播报 即将呼叫+name or number
//                preCallNumber = selectContactNumberInfoList.get(0).getNumber();
//                LogUtils.i(TAG, "TTS播报 即将呼叫" + preCallNumber);
//                uiInterface.closeCard();
//                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, TTSAnsConstant.PHONE_REDIAL + preCallNumber, TTSAnsConstant.PHONE_REDIAL);
//            }
//        } else {
//            //todo:场景为空，返回错误、打印日志
//            LogUtils.e(TAG, "scenario is null....");
//            uiInterface.closeCard();
//            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext);
//        }
//
//        return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext);
//    }
//}
