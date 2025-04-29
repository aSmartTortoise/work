package com.voyah.vcos.ttsservices.factory;

import com.voyah.vcos.ttsservices.manager.VoiceTTS;

/**
 * @author:lcy
 * @data:2024/1/30
 **/
public class TTSFactory {
    public static IVoiceTts createTTS(TTSType ttsType) {
        switch (ttsType) {
            case MICROSOFT:
                return new VoiceTTS();
        }
        return new VoiceTTS();
    }
}
