package com.voice.sdk.buriedpoint.bean;

public class VadTime {
        private long vadStartTime;
        private long vadEndTime;

        public long getVadStartTime() {
            return vadStartTime;
        }

        public void setVadStartTime(long vadStartTime) {
            this.vadStartTime = vadStartTime;
        }

        public long getVadEndTime() {
            return vadEndTime;
        }

        public void setVadEndTime(long vadEndTime) {
            this.vadEndTime = vadEndTime;
        }

        @Override
        public String toString() {
            return "VadTime{" +
                    "vadStartTime=" + vadStartTime +
                    ", vadEndTime=" + vadEndTime +
                    '}';
        }
    }