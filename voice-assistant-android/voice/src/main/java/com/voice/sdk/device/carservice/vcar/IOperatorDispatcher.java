package com.voice.sdk.device.carservice.vcar;

import java.util.Map;

/**
 * @Date 2024/7/17 11:37
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface IOperatorDispatcher {
    IPropertyOperator getOperatorByDomain(String domain);

    Map<String, IPropertyOperator> getPropertyOperatorMap();


}
