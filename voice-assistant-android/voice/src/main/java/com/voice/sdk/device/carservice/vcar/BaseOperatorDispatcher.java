package com.voice.sdk.device.carservice.vcar;

import java.util.HashMap;
import java.util.Map;


//TODO 抽到通用的module中，不适合放在H37的module
public abstract class BaseOperatorDispatcher implements IOperatorDispatcher {
    private static final String TAG = "BaseOperatorDispatcher";
    protected Map<String, IPropertyOperator> propertyOperatorMap = new HashMap<>();

    protected abstract void init();

    public BaseOperatorDispatcher() {
        init();
    }

    @Override
    public IPropertyOperator getOperatorByDomain(String domain) {
        String lowerCase = domain.toLowerCase();
        if (propertyOperatorMap.containsKey(domain)) {
            return propertyOperatorMap.get(domain);
        } else if (propertyOperatorMap.containsKey(lowerCase)) {
            return propertyOperatorMap.get(lowerCase);
        } else {
            //各平台的base兜底
            return propertyOperatorMap.get("base");
        }
    }

    public Map<String, IPropertyOperator> getPropertyOperatorMap(){
        return propertyOperatorMap;
    }
}
