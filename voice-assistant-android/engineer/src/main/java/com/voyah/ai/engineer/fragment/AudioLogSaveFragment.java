package com.voyah.ai.engineer.fragment;

import static com.voyah.ai.engineer.utils.ToastUtil.showToast;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.IflytekUtils;
import com.voyah.ai.engineer.R;
import com.voyah.ai.engineer.databinding.FragmentAudioLogSaveBinding;
import com.voyah.ai.engineer.utils.StorageUtil;
import com.voyah.ai.engineer.window.RecordTipFloatWindow;
import com.voyah.ai.engineer.window.TraceFloatWindow;
import com.voyah.ai.sdk.listener.IVAReadyListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioLogSaveFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private String savedDir;
    private StorageUtil.UsbListener usbListener;
    private FragmentAudioLogSaveBinding binding;
    private static final int WHAT_DUMP_FINISH = 0;
    private static final int WHAT_CLEAR_FINISH = 1;
    private static final int WHAT_DUMP_ANDROID_LOG = 2;
    private static final int WHAT_DUMP_VA_LOG = 3;
    private static final int WHAT_DUMP_TTS_LOG = 4;
    private static final int WHAT_DUMP_VA_AUDIO = 5;
    private static final int WHAT_CLEAN_AUDIO_TIP = 6;
    private File externalFileDir;
    private final TraceFloatWindow traceFloatWindow = TraceFloatWindow.get();
    private final RecordTipFloatWindow recordTipFloatWindow = RecordTipFloatWindow.get();
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.US);
    private static final int MAX_ANDROID_LOG_FILES = 100;
    private final AtomicBoolean flagDumping = new AtomicBoolean(false);
    private int lastAudioPercent, lastLogPercent = 0;
    private static final float GB = 1024 * 1024 * 1024;

    @Override
    protected int setLayout() {
        return R.layout.fragment_audio_log_save;
    }

    @Override
    protected void init() {
        binding = DataBindingUtil.bind(mView);
        traceFloatWindow.setFragment(this);
        recordTipFloatWindow.setFragment(this);
        initView();
        initUsb();
        DialogueManager.get().registerReadyListener(readyListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ThreadUtils.executeByCachedAtFixRate(checkAudioTask, 60, TimeUnit.SECONDS);
        ThreadUtils.executeByCachedAtFixRate(refreshFolderTask, 20, TimeUnit.SECONDS);
    }


    @Override
    public void onPause() {
        super.onPause();
        ThreadUtils.cancel(checkAudioTask);
        ThreadUtils.cancel(refreshFolderTask);
    }

    private final IVAReadyListener readyListener = new IVAReadyListener() {
        @Override
        public void onSpeechReady() {
            ThreadUtils.runOnUiThread(() -> {
                boolean debugAudioDumpEnabled = SettingsManager.get().isDebugAudioDumpEnabled();
                boolean fadeWakeupDumpEnabled = SettingsManager.get().isFadeWakeupDumpEnabled();
                binding.layoutSettings.swVaAudio.setChecked(debugAudioDumpEnabled);
                binding.layoutSettings.swFailWakeup.setChecked(fadeWakeupDumpEnabled);
                if (debugAudioDumpEnabled || fadeWakeupDumpEnabled) {
                    recordTipFloatWindow.show();
                }
            });
        }
    };

    private final ThreadUtils.SimpleTask<Void> checkAudioTask = new ThreadUtils.SimpleTask<Void>() {
        @Override
        public Void doInBackground() throws Throwable {
            File audioDir = new File(externalFileDir.getAbsolutePath() + "/audio");
            long audioDirSize = StorageUtil.calculateDirectorySize(audioDir);
            if (audioDirSize > 1 * GB) {
                float sizeG = audioDirSize / GB;
                Message.obtain(handler, WHAT_CLEAN_AUDIO_TIP, sizeG).sendToTarget();
            }
            return null;
        }

        @Override
        public void onSuccess(Void result) {
        }
    };

    private final ThreadUtils.SimpleTask<Void> refreshFolderTask = new ThreadUtils.SimpleTask<Void>() {
        @Override
        public Void doInBackground() throws Throwable {
            return null;
        }

        @Override
        public void onSuccess(Void result) {
            refreshDumpSavePath();
        }
    };

    private void initView() {
        binding.btnDump.setOnClickListener(this);
        binding.btnClear.setOnClickListener(this);
        externalFileDir = Utils.getApp().getExternalFilesDir(null);
        refreshDumpSavePath();

        binding.layoutSettings.swTracePanel.setOnCheckedChangeListener(this);
        binding.layoutSettings.swTracePanel.setChecked(traceFloatWindow.isShowing());

        binding.layoutSettings.swFailWakeup.setOnCheckedChangeListener(this);
        binding.layoutSettings.swFailWakeup.setChecked(SettingsManager.get().isFadeWakeupDumpEnabled());

        binding.layoutSettings.swVaAudio.setOnCheckedChangeListener(this);
        binding.layoutSettings.swVaAudio.setChecked(SettingsManager.get().isDebugAudioDumpEnabled());

        binding.layoutSettings.swAllNetwork.setOnCheckedChangeListener(this);
        binding.layoutSettings.swAllNetwork.setChecked(SettingsManager.get().isAllNetworkEnabled());

        binding.layoutSettings.swTtsLog.setOnCheckedChangeListener(this);
        binding.layoutSettings.swTtsLog.setChecked(SettingsManager.get().isTtsLogDumpEnabled());

        binding.layoutSettings.swPreEnv.setOnCheckedChangeListener(this);
        binding.layoutSettings.swPreEnv.setChecked(SettingsManager.get().isPreEnvEnabled());
    }

    private void initUsb() {
        StorageUtil.registerUsbListener(usbListener = new StorageUtil.UsbListener() {

            @Override
            public void onUDiskMount(String path) {
                binding.tvExecuteResult.setTextColor(Color.RED);
                if (TextUtils.isEmpty(path)) {
                    binding.tvExecuteResult.setText("U盘未连接，请检查连接是否正常");
                } else {
                    String sb = "路径:" + path + File.separator + binding.etFolderName.getText();
                    binding.tvExecuteResult.setText(sb);
                }
            }

            @Override
            public void onUDiskRemove() {
                binding.tvExecuteResult.setTextColor(Color.RED);
                binding.tvExecuteResult.setText("U盘未连接，请检查连接是否正常");
            }
        });
        String uDiskPath = StorageUtil.getUDiskPath();
        if (uDiskPath == null) {
            usbListener.onUDiskRemove();
        } else {
            usbListener.onUDiskMount(uDiskPath);
        }
    }

    @Override
    protected void unInit() {
        if (usbListener != null) {
            StorageUtil.unregisterUsbListener(usbListener);
            usbListener = null;
        }
        DialogueManager.get().unregisterReadyListener(readyListener);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnDump) {
            String uDiskPath = StorageUtil.getUDiskPath();
//            String uDiskPath = "/sdcard";
            if (uDiskPath == null) {
                showToast("未检测到U盘，请重试");
                return;
            }
            boolean hasSelected = binding.cbDebugAudio.isChecked() || binding.cbLogcat.isChecked();
            if (!hasSelected) {
                showToast("你还没有选择要导出的音频或日志呢");
                return;
            }
            if (isCapturingAudio() && binding.cbDebugAudio.isChecked()) {
                showToast("正在音频采集中，请先停止再进行导出操作");
                return;
            }
            if (externalFileDir == null) {
                LogUtils.e("externalFileDir == null, dumpSavedLogs failed!!");
                showToast("应用私有目录访问出错，请重试");
                return;
            }

            binding.btnDump.setEnabled(false);
            binding.btnClear.setEnabled(false);
            binding.tvExecuteResult.setTextColor(Color.RED);
            savedDir = uDiskPath + File.separator + binding.etFolderName.getText();
            boolean ret = new File(savedDir).mkdirs();
            LogUtils.d("start dump, mkdir result:" + ret + ", dir:" + savedDir);
            lastAudioPercent = lastLogPercent = 0;
            dumpSavedLogs();

        } else if (v == binding.btnClear) {
            if (!binding.cbLogcat.isChecked() && !binding.cbDebugAudio.isChecked()) {
                showToast("你还没有勾选要清除的内容");
                return;
            }
            if (isCapturingAudio() && binding.cbDebugAudio.isChecked()) {
                showToast("正在音频采集中，请先停止再进行清空操作");
                return;
            }
            String tip = "确定要清空";
            if (binding.cbLogcat.isChecked()) {
                tip = tip + " \"安卓日志\"";
            }
            if (binding.cbDebugAudio.isChecked()) {
                tip = tip + " \"语音专用音频\"";
            }
            tip = tip + "吗?";
            ClearStorageDialog dialog = new ClearStorageDialog(requireActivity());
            dialog.setTip(tip)
                    .setConfirmListener(v1 -> clearSavedLogs())
                    .show();
        }
    }

    private void clearSavedLogs() {
        ThreadUtils.getCpuPool().execute(() -> {
            if (binding.cbLogcat.isChecked()) {
                File android = new File("/log/android");
                if (android.exists()) {
                    LogUtils.d("clear logcat dir");
                    clearDirectory(android);
                }
            }

            if (binding.cbDebugAudio.isChecked()) {
                File audioDir = new File(externalFileDir.getAbsolutePath() + "/audio");
                if (audioDir.exists()) {
                    LogUtils.d("clear audio dir");
                    clearDirectory(audioDir);
                }
            }
            handler.sendEmptyMessageDelayed(WHAT_CLEAR_FINISH, 500);
        });
    }

    private void dumpSavedLogs() {
        ThreadUtils.getCpuPool().execute(() -> {
            flagDumping.set(true);
            if (binding.cbVaLog.isChecked()) {
                handler.sendEmptyMessage(WHAT_DUMP_VA_LOG);
                String resPart2Path = IflytekUtils.getResPart2Path();
                File iflytek = new File(resPart2Path);
                if (iflytek.exists()) {
                    String path1 = savedDir + "/iflytek/ISSLog";
                    boolean ret = StorageUtil.copyDirectory(resPart2Path + "/ISSLog", path1, 0, 0, null);
                    LogUtils.d("copy /iflytek/ISSLog ret:" + ret);
                    String path2 = savedDir + "/iflytek/log";
                    ret = StorageUtil.copyDirectory(resPart2Path + "/log", path2, 0, 0, null);
                    LogUtils.d("copy /iflytek/log ret:" + ret);
                    Message.obtain(handler, WHAT_DUMP_VA_LOG, 100).sendToTarget();
                } else {
                    LogUtils.w("dumpSavedLogs iflytek log fail, file is not exist!");
                }
            }
            if (binding.cbTtsLog.isChecked()) {
                handler.sendEmptyMessage(WHAT_DUMP_TTS_LOG);
                String microsoftPath = "/sdcard/microsoft";
                File microsoft = new File(microsoftPath);
                if (microsoft.exists()) {
                    String path = savedDir + "/microsoft/log";
                    boolean ret = StorageUtil.copyDirectory(microsoftPath + "/log", path, 0, 0, null);
                    LogUtils.d("copy /microsoft/log ret:" + ret);
                    Message.obtain(handler, WHAT_DUMP_TTS_LOG, 100).sendToTarget();
                } else {
                    LogUtils.w("dumpSavedLogs /sdcard/microsoft fail, dir is not exist!");
                }
            }
            if (binding.cbLogcat.isChecked()) {
                handler.sendEmptyMessage(WHAT_DUMP_ANDROID_LOG);
                File android = new File("/log/android");
                if (android.exists()) {
                    String path = savedDir + "/logcat";
                    FileUtils.createOrExistsDir(path);
                    String logcat = "/log/android/logcat.log";
                    boolean ret = StorageUtil.copyFile(logcat, path + "/logcat.log");
                    LogUtils.d("dumpSavedLogs logcat ret:" + ret + ", filePath:" + logcat);

                    File[] files = android.listFiles((dir, name) -> name.startsWith("logcat.log"));
                    if (files != null && files.length > 0) {
                        LogUtils.d("accepted files size: " + files.length);
                        Arrays.sort(files, Comparator.comparing(File::getName));
                        int copyCount = Math.min(files.length, MAX_ANDROID_LOG_FILES);
                        for (int i = 0; i < copyCount; i++) {
                            File file = files[i];
                            ret = StorageUtil.copyFile(file.getAbsolutePath(), path + File.separator + file.getName());
                            LogUtils.d("dumpSavedLogs logcat ret:" + ret + ", filePath:" + file.getAbsolutePath());
                            int percent = Math.min(((i + 1) * 100 / copyCount), 100);
                            if (lastLogPercent != percent) {
                                Message.obtain(handler, WHAT_DUMP_ANDROID_LOG, percent).sendToTarget();
                                lastLogPercent = percent;
                            }
                        }
                    }
                } else {
                    LogUtils.w("dumpSavedLogs Logcat fail, /log/android is not exist!");
                }
            }
            if (binding.cbDebugAudio.isChecked()) {
                handler.sendEmptyMessage(WHAT_DUMP_VA_AUDIO);
                String debugAudioDir = externalFileDir.getAbsolutePath() + "/audio";
                File audioFile = new File(debugAudioDir);
                if (audioFile.exists()) {
                    String path = savedDir + "/audio";
                    long totalSize = StorageUtil.calculateDirectorySize(audioFile);
                    boolean ret = StorageUtil.copyDirectory(debugAudioDir, path, totalSize, 0, (copiedBytes, totalBytes) -> {
                        int percent = Math.min((int) (copiedBytes * 100 / totalBytes), 100);
                        if (lastAudioPercent != percent) {
                            Message.obtain(handler, WHAT_DUMP_VA_AUDIO, percent).sendToTarget();
                            lastAudioPercent = percent;
                        }
                    });
                    LogUtils.d("dumpSavedLogs debug audio ret:" + ret);
                } else {
                    LogUtils.w("dumpSavedLogs debug audio fail, ./audio is not exist!");
                }
            }
            flagDumping.set(false);
            handler.sendEmptyMessageDelayed(WHAT_DUMP_FINISH, 500);
        });
    }

    private final Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == WHAT_DUMP_FINISH) {
                showToast("音频和日志导出完成");
                binding.tvExecuteResult.setTextColor(Color.parseColor("#FF057C05"));
                binding.tvExecuteResult.setText("导出音频和日志完成，路径：" + savedDir);
                binding.btnDump.setEnabled(true);
                binding.btnClear.setEnabled(true);
            } else if (msg.what == WHAT_CLEAR_FINISH) {
                showToast("音频和日志清理完成");
                binding.tvCleanAudioTip.setVisibility(View.GONE);
            } else if (msg.what == WHAT_DUMP_VA_LOG) {
                if (msg.obj == null) {
                    binding.tvExecuteResult.setText("正在导出讯飞日志...");
                } else if (msg.obj instanceof Integer) {
                    binding.tvExecuteResult.setText(String.format(Locale.US, "正在导出讯飞日志: %d%%", msg.obj));
                }
            } else if (msg.what == WHAT_DUMP_TTS_LOG) {
                if (msg.obj == null) {
                    binding.tvExecuteResult.setText("正在导出微软tts日志...");
                } else if (msg.obj instanceof Integer) {
                    binding.tvExecuteResult.setText(String.format(Locale.US, "正在微软讯飞tts日志: %d%%", msg.obj));
                }
            } else if (msg.what == WHAT_DUMP_ANDROID_LOG) {
                if (msg.obj == null) {
                    binding.tvExecuteResult.setText("正在导出安卓日志...");
                } else if (msg.obj instanceof Integer) {
                    binding.tvExecuteResult.setText(String.format(Locale.US, "正在导出安卓日志: %d%%", msg.obj));
                }
            } else if (msg.what == WHAT_DUMP_VA_AUDIO) {
                if (msg.obj == null) {
                    binding.tvExecuteResult.setText("正在导出语音专用音频...");
                } else if (msg.obj instanceof Integer) {
                    binding.tvExecuteResult.setText(String.format(Locale.US, "正在导出语音专用音频: %d%%", msg.obj));
                }
            } else if (msg.what == WHAT_CLEAN_AUDIO_TIP) {
                if (msg.obj instanceof Float) {
                    binding.tvCleanAudioTip.setVisibility(View.VISIBLE);
                    binding.tvCleanAudioTip.setText(String.format(Locale.US, "此次开机已检测到存储了%.1fG音频文件，请注意清理...", msg.obj));
                }
            }
            return true;
        }
    });

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == binding.layoutSettings.swTracePanel) {
            if (isChecked) {
                traceFloatWindow.show();
            } else {
                traceFloatWindow.remove();
            }
        } else if (buttonView == binding.layoutSettings.swVaAudio || buttonView == binding.layoutSettings.swFailWakeup) {
            boolean ret;
            if (buttonView == binding.layoutSettings.swVaAudio) {
                ret = SettingsManager.get().enableDebugAudioDump(isChecked);
            } else {
                ret = SettingsManager.get().enableFadeWakeupDump(isChecked);
            }
            if (!ret) {
                buttonView.setChecked(!isChecked);
                showToast("语音还未初始化完成，请稍后重试!");
            }
            if (isChecked && DialogueManager.get().isReady()) {
                recordTipFloatWindow.show();
            } else {
                if (!SettingsManager.get().isDebugAudioDumpEnabled() && !SettingsManager.get().isFadeWakeupDumpEnabled()) {
                    recordTipFloatWindow.remove();
                }
            }
        } else if (buttonView == binding.layoutSettings.swAllNetwork) {
            boolean ret = SettingsManager.get().enableAllNetwork(isChecked);
            if (ret) {
                // 提示用户重启语音的3个进程
                String tip = "切换网络通道开关后不会即时生效，是否立即重启语音、TTSService和VoiceUi 3个应用?";
                ClearStorageDialog dialog = new ClearStorageDialog(requireActivity());
                dialog.setTip(tip)
                        .setConfirmListener(v -> {
                            CommonSystemUtils.forceStopPackage(Utils.getApp(), ApplicationConstant.PKG_VOICEUI);
                            CommonSystemUtils.forceStopPackage(Utils.getApp(), ApplicationConstant.PKG_TTS_SERVICE);
                            CommonSystemUtils.forceStopPackage(Utils.getApp(), Utils.getApp().getPackageName());
                        })
                        .show();
            }
        } else if (buttonView == binding.layoutSettings.swTtsLog) {
            boolean ret = SettingsManager.get().enableTtsLogDump(isChecked);
            if (ret) {
                // 提示用户重启TTS服务进程
                String tip = "切换微软TTS日志开关后需要重启TTSService应用，是否立即重启TTS服务?";
                ClearStorageDialog dialog = new ClearStorageDialog(requireActivity());
                dialog.setTip(tip)
                        .setConfirmListener(v -> {
                            CommonSystemUtils.forceStopPackage(Utils.getApp(), ApplicationConstant.PKG_TTS_SERVICE);
                        })
                        .show();
            }
        } else if (buttonView == binding.layoutSettings.swPreEnv) {
            boolean ret = SettingsManager.get().enablePreEnv(isChecked);
            if (ret) {
                String tip = "切换语音Pre环境开关后需要重启语音和TTSService应用，是否立即应用重启?";
                ClearStorageDialog dialog = new ClearStorageDialog(requireActivity());
                dialog.setTip(tip)
                        .setConfirmListener(v -> {
                            CommonSystemUtils.forceStopPackage(Utils.getApp(), ApplicationConstant.PKG_TTS_SERVICE);
                            CommonSystemUtils.forceStopPackage(Utils.getApp(), Utils.getApp().getPackageName());
                        })
                        .show();
            }
        }
    }

    public void onRecordTipWindowDismiss() {
        LogUtils.d("onRecordTipWindowDismiss() called");
        binding.layoutSettings.swVaAudio.setChecked(false);
        binding.layoutSettings.swFailWakeup.setChecked(false);
    }

    public void onTraceWindowDismiss() {
        LogUtils.d("onTraceWindowDismiss() called");
        binding.layoutSettings.swTracePanel.setChecked(false);
    }

    public void refreshDumpSavePath() {
        if (!flagDumping.get()) {
            String text = "VoiceLogAudio/VoiceLogAudio_" + TimeUtils.millis2String(System.currentTimeMillis(), DATE_FORMAT);
            LogUtils.d("refreshData:" + text);
            binding.etFolderName.setText(text);
        }
    }


    private void clearDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!"/log/android/logcat.log".equals(file.getPath())) {
                        file.delete();
                    }
                } else if (file.isDirectory()) {
                    StorageUtil.deleteDirectory(file);
                    file.delete();
                }
            }
        }
    }

    private boolean isCapturingAudio() {
        return binding.layoutSettings.swFailWakeup.isChecked()
                || binding.layoutSettings.swVaAudio.isChecked();
    }
}
