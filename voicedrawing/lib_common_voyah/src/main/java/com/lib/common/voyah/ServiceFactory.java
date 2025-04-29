package com.lib.common.voyah;

import com.lib.common.voyah.service.IVoiceService;

/**
 * author : jie wang
 * date : 2025/3/18 11:23
 * description :
 */
public class ServiceFactory {

    private IVoiceService voiceService;

    private ServiceFactory() {

    }

    private static class Holder {
        private static ServiceFactory INSTANCE = new ServiceFactory();
    }

    public static ServiceFactory getInstance() {
        return Holder.INSTANCE;
    }

    public IVoiceService getVoiceService() {
        return voiceService;
    }

    public void setVoiceService(IVoiceService voiceService) {
        this.voiceService = voiceService;
    }
}
