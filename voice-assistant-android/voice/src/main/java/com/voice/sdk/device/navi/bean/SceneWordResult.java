package com.voice.sdk.device.navi.bean;

import java.io.Serializable;
import java.util.List;
@SuppressWarnings("unused")
public class SceneWordResult implements Serializable {
    private int resultCode;
    private List<SceneWord> sceneWords;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public List<SceneWord> getSceneWords() {
        return sceneWords;
    }

    public void setSceneWords(List<SceneWord> sceneWords) {
        this.sceneWords = sceneWords;
    }

    public static class SceneWord {
        private int item;
        private String operation;
        private String word;

        public int getItem() {
            return item;
        }

        public void setItem(int item) {
            this.item = item;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
