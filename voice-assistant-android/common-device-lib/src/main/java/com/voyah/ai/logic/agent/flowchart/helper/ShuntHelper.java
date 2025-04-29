package com.voyah.ai.logic.agent.flowchart.helper;

import java.util.HashSet;
import java.util.Set;

/**
 * 通用函数分流帮助类。
 */
public class ShuntHelper {
    public Set<String> set = new HashSet<>();
    public ShuntHelper(){
        //添加对应的函数
        set.add("");
    }

    public boolean isCommonFunction(String nlu) {
        return false;
    }
}
