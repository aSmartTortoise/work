package com.voyah.ai.common.helper;

public interface ParamsKey {

    //区分每个模块需要的日志key
    interface ModuleKey {
        //测试需要的日志，需要传入json格式
        String TEST_MODULE = "test_module";
    }

    interface TestKey {
        String TEST_KEY = "module_type";

        interface VALUE {
            String EXCUTE = "excute";
            String TTS = "tts";
        }
    }


    //功能矩阵的参数key
    interface FunctionMatrixParameterKey {
        String SWITCH_TYPE = "switch_type";
        String PARAMS_KEY = "params_key";
        String METHOD_NAME = "method_name";
        String LEVEL = "level";
        String POSITION = "position";
        String NUMBER_TEMP = "number_temp";
        String NUMBER_LEVEL = "number_level";
        String ADJUST_TYPE = "adjust_type";
        String SWITCH_MODE = "switch_mode";
    }

    //保存在上下文中二次确认或二次交互需要的参数key
    interface ConfirmationAndSecondaryInteractionParameterKey {

    }

}
