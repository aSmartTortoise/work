package com.voyah.ai.engineer.fragment;

import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RadioGroup;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.audio.IPcmAudioCallback;
import com.voyah.ai.engineer.R;
import com.voyah.ai.engineer.databinding.FragmentMicCheckBinding;
import com.voyah.ai.engineer.utils.Pcm2Wav;
import com.voyah.ai.engineer.utils.SamplePlayer;
import com.voyah.ai.engineer.utils.SoundFile;
import com.voyah.ai.engineer.utils.ToastUtil;
import com.voyah.ai.engineer.view.WaveCanvas;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MicCheckFragment extends BaseFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private WaveCanvas waveCanvas;
    private final int UPDATE_WAV = 100;
    private SamplePlayer samplePlayer;
    private SoundFile mSoundFile;
    private int selectedChannel = 0;
    private FragmentMicCheckBinding binding;
    private Handler drawHandler, saveHandler;
    private HandlerThread drawThread, saveThread;
    private Pcm2Wav p2w;
    private String savePcmPath, saveWavPath;  //保存文件路径

    @Override
    protected int setLayout() {
        return R.layout.fragment_mic_check;
    }

    @Override
    protected void init() {
        binding = DataBindingUtil.bind(mView);
        assert binding != null;
        binding.wavesfv.setLineOff(42);
        //解决surfaceView黑色闪动效果
        binding.wavesfv.setZOrderOnTop(true);
        binding.wavesfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        binding.waveview.setLineOffset(42);

        binding.wavesfv.setVisibility(View.VISIBLE);
        binding.waveview.setVisibility(View.INVISIBLE);

        binding.switchbtn.setOnClickListener(this);
        binding.playbtn.setOnClickListener(this);

        binding.channelGroup.setOnCheckedChangeListener(this);
        binding.channelGroup.check(R.id.ch0rb);

        binding.playbtn.setEnabled(false);
        File externalCacheDir = Utils.getApp().getExternalCacheDir();
        if (externalCacheDir == null) {
            savePcmPath = Utils.getApp().getCacheDir() + "/test.pcm";
            saveWavPath = Utils.getApp().getCacheDir() + "/test.wav";
        } else {
            savePcmPath = externalCacheDir.getAbsolutePath() + "/test.pcm";
            saveWavPath = externalCacheDir.getAbsolutePath() + "/test.wav";
        }
        p2w = new Pcm2Wav();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.switchbtn) {
            if (samplePlayer != null && samplePlayer.isPlaying()) {
                LogUtils.d("currently playing, stop");
                samplePlayer.stop();
            }

            if (waveCanvas == null || !waveCanvas.isRecording) {
                binding.statustv.setText("录音中...");
                binding.switchbtn.setText("停止录音");
                binding.wavesfv.setVisibility(View.VISIBLE);
                binding.waveview.setVisibility(View.INVISIBLE);
                binding.playbtn.setEnabled(false);
                startAudio();
            } else {
                binding.statustv.setText("停止录音");
                binding.switchbtn.setText("开始录音");
                binding.playbtn.setEnabled(true);
                stopAudio();
                loadWaveView();
            }
        } else if (view == binding.playbtn) {
            play();
        }
    }

    /**
     * 开始录音
     */
    private void startAudio() {
        LogUtils.d("startAudio() called");
        if (waveCanvas == null) {
            waveCanvas = new WaveCanvas();
        }
        drawThread = new HandlerThread("t_mic_draw");
        drawThread.start();
        drawHandler = new Handler(drawThread.getLooper());

        saveThread = new HandlerThread("t_mic_save");
        saveThread.start();
        saveHandler = new Handler(saveThread.getLooper());

        waveCanvas.start(binding.wavesfv);
        DeviceHolder.INS().getDevices().getAudioRecorder().addPcmAudioCallback(audioCallback);
        FileUtils.delete(savePcmPath);
        FileUtils.delete(saveWavPath);
    }

    private void stopAudio() {
        LogUtils.d("stopAudio() called");
        if (drawThread != null) {
            drawThread.quit();
        }
        if (saveThread != null) {
            saveThread.quit();
        }
        DeviceHolder.INS().getDevices().getAudioRecorder().removePcmAudioCallback(audioCallback);
        if (waveCanvas != null && waveCanvas.isRecording) {
            waveCanvas.stop();
            waveCanvas = null;
        }
    }

    /**
     * 10路音频指定通道的音频
     */
    private byte[] splitOneChannel(byte[] data, int channel) {
        byte[] buf1Input = new byte[data.length / 10];
        for (int j = 0; j < data.length / 20; j++) {
            buf1Input[(j * 2) + 0] = data[(j * 20) + channel * 2 + 0];
            buf1Input[(j * 2) + 1] = data[(j * 20) + channel * 2 + 1];
        }
        return buf1Input;
    }

    private final Handler updateTime = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_WAV) {
                updateDisplay();
                Message message = Message.obtain();
                message.what = UPDATE_WAV;
                updateTime.sendMessageDelayed(message, 10);
            }
        }
    };

    private void loadWaveView() {
        ThreadUtils.executeByCachedWithDelay(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() throws Throwable {
                LogUtils.d("loadWaveView doInBackground() called");
                try {
                    mSoundFile = SoundFile.create(saveWavPath, null);
                    if (mSoundFile == null) {
                        return null;
                    }
                    samplePlayer = new SamplePlayer(mSoundFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return null;
            }

            @Override
            public void onSuccess(Void result) {
                LogUtils.d("loadWaveView onSuccess() called with: result = [" + result + "]");
                if (mSoundFile != null) {
                    binding.waveview.setSoundFile(mSoundFile);
                    binding.waveview.recomputeHeights();
                    binding.wavesfv.setVisibility(View.INVISIBLE);
                    binding.waveview.setVisibility(View.VISIBLE);
                } else {
                    LogUtils.e("mSoundFile = null, exception");
                }
            }
        }, 600, TimeUnit.MILLISECONDS);
    }

    /**
     * 更新waveView的播放进度
     */
    private void updateDisplay() {
        if (samplePlayer != null) {
            int now = samplePlayer.getCurrentPosition();
            int frames = binding.waveview.millisToPixels(now);
            binding.waveview.setPlayback(frames);//通过这个更新当前播放的位置
            if (now >= binding.waveview.pixelsToMillsTotal()) {
                binding.waveview.setPlayFinish(1);
                if (samplePlayer.isPlaying()) {
                    samplePlayer.pause();
                    updateTime.removeMessages(UPDATE_WAV);
                }
            } else {
                binding.waveview.setPlayFinish(0);
            }
            binding.waveview.invalidate();//刷新视图
        }
    }

    /**
     * 播放音频
     */
    private synchronized void play() {
        if (samplePlayer == null) {
            return;
        }
        if (samplePlayer.isPlaying()) {
            samplePlayer.pause();
            updateTime.removeMessages(UPDATE_WAV);
        }
        int playStartMsc = binding.waveview.pixelsToMillis(0);
        samplePlayer.setOnCompletionListener(() -> {
            binding.waveview.setPlayback(-1);
            updateDisplay();
            updateTime.removeMessages(UPDATE_WAV);
            ToastUtil.showToast("播放完成");
        });
        samplePlayer.seekTo(playStartMsc);
        samplePlayer.start();
        Message.obtain(updateTime, UPDATE_WAV).sendToTarget();
    }

    @Override
    protected void unInit() {
        updateTime.removeCallbacksAndMessages(null);
        stopAudio();
        if (samplePlayer != null && samplePlayer.isPlaying()) {
            samplePlayer.stop();
            samplePlayer.release();
            samplePlayer = null;
        }
        if (saveHandler != null) {
            saveHandler.postDelayed(() -> {
                FileUtils.delete(savePcmPath);
                FileUtils.delete(saveWavPath);
            }, 500);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.ch0rb) {
            selectedChannel = 0;
        } else if (id == R.id.ch1rb) {
            selectedChannel = 1;
        } else if (id == R.id.ch2rb) {
            selectedChannel = 2;
        } else if (id == R.id.ch3rb) {
            selectedChannel = 3;
        } else if (id == R.id.ch4rb) {
            selectedChannel = 4;
        } else if (id == R.id.ch5rb) {
            selectedChannel = 5;
        } else if (id == R.id.ch6rb) {
            selectedChannel = 6;
        } else if (id == R.id.ch7rb) {
            selectedChannel = 7;
        } else if (id == R.id.ch8rb) {
            selectedChannel = 8;
        } else if (id == R.id.ch9rb) {
            selectedChannel = 9;
        }
        if (waveCanvas != null) {
            waveCanvas.clear();
        }
    }

    private final IPcmAudioCallback audioCallback = new IPcmAudioCallback() {

        @Override
        public void onPcmInAudio(byte[] audio) {
            byte[] bytes = splitOneChannel(audio, selectedChannel);

            drawHandler.post(() -> {
                if (waveCanvas != null) {
                    waveCanvas.processData(bytes, bytes.length);
                }
            });

            saveHandler.post(() -> {
                FileIOUtils.writeFileFromBytesByStream(savePcmPath, bytes, true);
                try {
                    p2w.convertAudioFiles(savePcmPath, saveWavPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    };

}
