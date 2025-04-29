package com.voyah.viewcmd;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ViewCmdBean {

    /**
     * 控件属性，用作泛化，控件属性由控件类型、控件状态组成，由:连接
     * 控件类型、控件状态均为产品定义。prompt可传空，如果为空语音则不进行泛化
     * 在语义回调时nlu需要将控件类型返回
     */
    public String prompt;
    /**
     * 泛化前的原始文本，不能为空，在语音侧将根据prompt对text进行泛化
     * 在语义回调时nlu需要将text返回
     */
    public String text;

    /**
     * 可见类型标识：Normal/Global/KWS
     */
    public String type;

    public ViewCmdBean(String prompt, String text, String type) {
        this.prompt = prompt;
        this.text = text;
        this.type = type;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("prompt", prompt);
            object.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewCmdBean bean = (ViewCmdBean) o;
        return Objects.equals(prompt, bean.prompt) &&
                Objects.equals(text, bean.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prompt, text);
    }
}
