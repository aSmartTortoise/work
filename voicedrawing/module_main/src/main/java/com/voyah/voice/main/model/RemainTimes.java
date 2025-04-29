package com.voyah.voice.main.model;

import com.google.gson.annotations.SerializedName;

public class RemainTimes {

    private String code;
    private String message;
    private ResultDTO result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultDTO getResult() {
        return result;
    }

    public void setResult(ResultDTO result) {
        this.result = result;
    }

    public static class ResultDTO {
        private String vin;
        private int freeTimes;
        private int freeRemainTimes;
        private int times;
        private int remainTimes;
        private Boolean free;
        private int totalTimes;
        private int totalRemainTimes;

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public int getFreeTimes() {
            return freeTimes;
        }

        public void setFreeTimes(int freeTimes) {
            this.freeTimes = freeTimes;
        }

        public int getFreeRemainTimes() {
            return freeRemainTimes;
        }

        public void setFreeRemainTimes(int freeRemainTimes) {
            this.freeRemainTimes = freeRemainTimes;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public int getRemainTimes() {
            return remainTimes;
        }

        public void setRemainTimes(int remainTimes) {
            this.remainTimes = remainTimes;
        }

        public Boolean getFree() {
            return free;
        }

        public void setFree(Boolean free) {
            this.free = free;
        }

        public int getTotalTimes() {
            return totalTimes;
        }

        public void setTotalTimes(int totalTimes) {
            this.totalTimes = totalTimes;
        }

        public int getTotalRemainTimes() {
            return totalRemainTimes;
        }

        public void setTotalRemainTimes(int totalRemainTimes) {
            this.totalRemainTimes = totalRemainTimes;
        }
    }
}
