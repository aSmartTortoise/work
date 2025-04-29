package com.voyah.ai.voice.agent.phone;

import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.call.ContactInfo;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/3/7
 **/
public class PhoneFlowContextUtils {

    public static Map<String, Object> setFlowContextParams(Map<String, Object> flowContext, List<ContactInfo> contactsInfoList,
                                                           List<ContactNumberInfo> numberInfoList, String number, String name, String operation_type) {
//        if (!contactsInfoList.isEmpty())
//        flowContext.put(Constant.PARAMS_CONTACT_LIST, listTailor(contactsInfoList));
//        if (!numberInfoList.isEmpty())
        flowContext.put(Constant.PARAMS_NUMBER_LIST, listTailor(numberInfoList));
//        if (!StringUtils.isEmpty(number))
        flowContext.put(Constant.PARAMS_NUMBER, number);
//        if (!StringUtils.isEmpty(name))
        flowContext.put(Constant.PARAMS_NAME, name);
//        if (!StringUtils.isEmpty(operation_type))
        flowContext.put(Constant.PARAMS_OPERATION_TYPE, operation_type);
        return flowContext;
    }

    public static Map<String, Object> setFlowContextParams(Map<String, Object> flowContext, List<ContactInfo> contactsInfoList,
                                                           List<ContactNumberInfo> numberInfoList, String number, String name, String operation_type, String search_type) {
//        if (!contactsInfoList.isEmpty())
//        flowContext.put(Constant.PARAMS_CONTACT_LIST, listTailor(contactsInfoList));
//        if (!numberInfoList.isEmpty())
        flowContext.put(Constant.PARAMS_NUMBER_LIST, listTailor(numberInfoList));
//        if (!StringUtils.isEmpty(number))
        flowContext.put(Constant.PARAMS_NUMBER, number);
//        if (!StringUtils.isEmpty(name))
        flowContext.put(Constant.PARAMS_NAME, name);
//        if (!StringUtils.isEmpty(operation_type))
        flowContext.put(Constant.PARAMS_OPERATION_TYPE, operation_type);

        flowContext.put(Constant.PARAMS_SEARCH_TYPE, search_type);
        return flowContext;
    }

    public static void setFlowContextCardList(List<ContactNumberInfo> list, Map<String, Object> flowContext) {
        if (null != list && null != flowContext && !list.isEmpty())
            flowContext.put(FlowContextKey.SC_CALL_PHONE_NUMBER_LIST, list);
    }

    private static List<ContactNumberInfo> listTailor(List<ContactNumberInfo> contactsInfoList) {
        if (null == contactsInfoList)
            return null;
        List<ContactNumberInfo> trimmedList;
        // 检查List的大小并处理
        if (contactsInfoList.size() > 50) {
            trimmedList = contactsInfoList.subList(0, 50);
            List<ContactNumberInfo> list = new ArrayList<>();
            list.addAll(trimmedList);
            return list;
        } else {
            return contactsInfoList;
        }
    }

}
