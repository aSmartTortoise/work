package com.voyah.vcos.ttsservices;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.GsonUtils;
import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.vcos.ttsservices.copymicrosoft.CopyMcTtsResolver;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.info.TtsBean;
import com.voyah.vcos.ttsservices.info.TtsPriority;
import com.voyah.vcos.ttsservices.manager.TTSManager;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private String[] streamTest = new String[]{"中国的行动，有力提振", "了全球生物多样性保护的信", "心；中国保护生物多样", "性的理念和经验，获得越来越多的世界赞誉，"
            , "不断助力全球描绘生物多样性治", "理蓝图；中国与世界携手同行，通", "过双多边合作加强生物多样性保护，践行共建美", "好家园的中国诺言。"};
    private TTSManager ttsManager = TTSManager.getInstance();

    CopyMcTtsResolver[] copyMcTtsResolve = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startVRService();
//        testInitTTS(16);
//        initView();
        finish();
    }

    public String extractTextBetweenTags(String originalText, String startTag, String endTag) {
        String patternString = Pattern.quote(startTag) + "(.*?)" + Pattern.quote(endTag);
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(originalText);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private void testInitTTS(int usage) {
        //语音通道
        ttsManager.initTts(AppContext.instant, usage);
    }

    private void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyMcTtsResolve = new CopyMcTtsResolver[]{new CopyMcTtsResolver(false, false, 20)};
                copyMcTtsResolve[0].init(20);
            }
        }).start();

        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TtsBean ttsBean = new TtsBean("先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。然侍卫之臣不懈于内，忠志之士忘身于外者，盖追先帝之殊遇，欲报之于陛下也。诚宜开张圣听，以光先帝遗德，恢弘志士之气，不宜妄自菲薄，引喻失义，以塞忠谏之路也。\n" +
                        "\n" +
                        "宫中府中，俱为一体，陟罚臧否，不宜异同。若有作奸犯科及为忠善者，宜付有司论其刑赏，以昭陛下平明之理，不宜偏私，使内外异法也。\n" +
                        "\n" +
                        "侍中、侍郎郭攸之、费祎、董允等，此皆良实，志虑忠纯，是以先帝简拔以遗陛下。愚以为宫中之事，事无大小，悉以咨之，然后施行，必能裨补阙漏，有所广益。\n" +
                        "\n" +
                        "将军向宠，性行淑均，晓畅军事，试用于昔日，先帝称之曰能，是以众议举宠为督。愚以为营中之事，悉以咨之，必能使行阵和睦，优劣得所。\n" +
                        "\n" +
                        "亲贤臣，远小人，此先汉所以兴隆也；亲小人，远贤臣，此后汉所以倾颓也。先帝在时，每与臣论此事，未尝不叹息痛恨于桓、灵也。侍中、尚书、长史、参军，此悉贞良死节之臣，愿陛下亲之信之，则汉室之隆，可计日而待也。\n" +
                        "\n" +
                        "臣本布衣，躬耕于南阳，苟全性命于乱世，不求闻达于诸侯。先帝不以臣卑鄙，猥自枉屈，三顾臣于草庐之中，咨臣以当世之事，由是感激，遂许先帝以驱驰。后值倾覆，受任于败军之际，奉命于危难之间，尔来二十有一年矣。\n" +
                        "\n" +
                        "先帝知臣谨慎，故临崩寄臣以大事也。受命以来，夙夜忧叹，恐托付不效，以伤先帝之明，故五月渡泸，深入不毛。今南方已定，兵甲已足，当奖率三军，北定中原，庶竭驽钝，攘除奸凶，兴复汉室，还于旧都。此臣所以报先帝而忠陛下之职分也。至于斟酌损益，进尽忠言，则攸之、祎、允之任也。\n" +
                        "\n" +
                        "愿陛下托臣以讨贼兴复之效，不效，则治臣之罪，以告先帝之灵。若无兴德之言，则责攸之、祎、允等之慢，以彰其咎；陛下亦宜自谋，以咨诹善道，察纳雅言，深追先帝遗诏，臣不胜受恩感激。\n" +
                        "\n" +
                        "今当远离，临表涕零，不知所言。", "com.voyah.ai.voice");
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setTtsPriority(TtsPriority.P2);
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            }
        });

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TtsBean ttsBean = new TtsBean("先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。然侍卫之臣不懈于内，忠志之士忘身于外者，盖追先帝之殊遇，欲报之于陛下也。诚宜开张圣听，以光先帝遗德，恢弘志士之气，不宜妄自菲薄，引喻失义，以塞忠谏之路也。\n" +
                        "\n" +
                        "宫中府中，俱为一体，陟罚臧否，不宜异同。若有作奸犯科及为忠善者，宜付有司论其刑赏，以昭陛下平明之理，不宜偏私，使内外异法也。\n" +
                        "\n" +
                        "侍中、侍郎郭攸之、费祎、董允等，此皆良实，志虑忠纯，是以先帝简拔以遗陛下。愚以为宫中之事，事无大小，悉以咨之，然后施行，必能裨补阙漏，有所广益。\n" +
                        "\n" +
                        "将军向宠，性行淑均，晓畅军事，试用于昔日，先帝称之曰能，是以众议举宠为督。愚以为营中之事，悉以咨之，必能使行阵和睦，优劣得所。\n" +
                        "\n" +
                        "亲贤臣，远小人，此先汉所以兴隆也；亲小人，远贤臣，此后汉所以倾颓也。先帝在时，每与臣论此事，未尝不叹息痛恨于桓、灵也。侍中、尚书、长史、参军，此悉贞良死节之臣，愿陛下亲之信之，则汉室之隆，可计日而待也。\n" +
                        "\n" +
                        "臣本布衣，躬耕于南阳，苟全性命于乱世，不求闻达于诸侯。先帝不以臣卑鄙，猥自枉屈，三顾臣于草庐之中，咨臣以当世之事，由是感激，遂许先帝以驱驰。后值倾覆，受任于败军之际，奉命于危难之间，尔来二十有一年矣。\n" +
                        "\n" +
                        "先帝知臣谨慎，故临崩寄臣以大事也。受命以来，夙夜忧叹，恐托付不效，以伤先帝之明，故五月渡泸，深入不毛。今南方已定，兵甲已足，当奖率三军，北定中原，庶竭驽钝，攘除奸凶，兴复汉室，还于旧都。此臣所以报先帝而忠陛下之职分也。至于斟酌损益，进尽忠言，则攸之、祎、允之任也。\n" +
                        "\n" +
                        "愿陛下托臣以讨贼兴复之效，不效，则治臣之罪，以告先帝之灵。若无兴德之言，则责攸之、祎、允等之慢，以彰其咎；陛下亦宜自谋，以咨诹善道，察纳雅言，深追先帝遗诏，臣不胜受恩感激。\n" +
                        "\n" +
                        "今当远离，临表涕零，不知所言。", "com.voyah.ai.voice");
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setTtsPriority(TtsPriority.P1);
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            }
        });

        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TtsBean ttsBean = new TtsBean("TTS合成通路测试-P2", "com.voyah.ai.voice");
                ttsBean.setTtsPriority(TtsPriority.P2);
                ttsBean.setTtsId(Util.generateRandom(9));
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            }
        });

        findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TtsBean ttsBean = new TtsBean("TTS合成通路测试--", "com.voyah.ai.voice");
                ttsBean.setTtsPriority(TtsPriority.P2);
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsManager.playTts(-1, ttsBean);
            }
        });

        findViewById(R.id.bt5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TtsBean ttsBean = new TtsBean("TTS合成通路测试--", "com.voyah.ai.voice");
                ttsBean.setTtsPriority(TtsPriority.P1);
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            }
        });

        findViewById(R.id.bt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TtsBean ttsBean = null;
                        for (int i = 0; i < streamTest.length; i++) {
                            if (i == 0)
                                ttsBean = new TtsBean(streamTest[i], "com.voyah.ai.voice", 0);
                            if (i > 0 && i < streamTest.length - 1)
                                ttsBean = new TtsBean(streamTest[i], "com.voyah.ai.voice", 1);
                            if (i == streamTest.length - 1)
                                ttsBean = new TtsBean(streamTest[i], "com.voyah.ai.voice", 2);
                            ttsBean.setTtsPriority(TtsPriority.P1);
                            LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                            try {
                                Thread.sleep(i * 100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            ttsManager.playStreamTts(16, ttsBean);
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.bt7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TtsBean ttsBean = new TtsBean("普通播报-打断流式", "com.voyah.ai.voice");
                ttsBean.setTtsPriority(TtsPriority.P1);
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            }
        });

        findViewById(R.id.bt8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String str : streamTest) {
                    TtsBean ttsBean = new TtsBean(str, "com.voyah.ai.voice");
                    if (TextUtils.equals(str, streamTest[2]))
                        ttsBean.setTtsPriority(TtsPriority.P3);
                    else
                        ttsBean.setTtsPriority(TtsPriority.P2);
                    ttsBean.setTtsId(Util.generateRandom(9));
                    ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        findViewById(R.id.copySyc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (null == copyMcTtsResolve[0]) {
                            copyMcTtsResolve[0] = new CopyMcTtsResolver(false, false, 20);
                            copyMcTtsResolve[0].init(20);
                        }
                        PlayTTSBean playTTSBean = new PlayTTSBean();
                        playTTSBean.setTtsId("23143554545");
                        playTTSBean.setTts("声音复刻合成测试");
                        copyMcTtsResolve[0].startSynthesis(playTTSBean);
                    }
                }).start();
            }
        });

    }

    private ITtsCallback iTtsCallback = new ITtsCallback.Stub() {
        @Override
        public void onTtsBeginning(String text) throws RemoteException {
            LogUtils.i(TAG, "onTtsBeginning text is " + text);
        }

        @Override
        public void onTtsEnd(String text, int reason) throws RemoteException {
            LogUtils.i(TAG, "onTtsEnd text is " + text + " ,reason " + reason);
        }

        @Override
        public void onTtsError(String text, int errCode) throws RemoteException {
            LogUtils.i(TAG, "onTtsError text is " + text + " ,errCode " + errCode);
        }
    };

    private void startVRService() {
        //todo:语音服务启动临时放在这里
        TTSService.startService(this);
//        finish();
    }
}