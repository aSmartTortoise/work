package com.voyah.vcos.asraudiorecord;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.voyah.vcos.asraudiorecord.recorder.IRecordListener;
import com.voyah.vcos.asraudiorecord.recorder.VoyahAudioRecord;
import com.voyah.vcos.asraudiorecord.util.FileUtils;
import com.voyah.vcos.asraudiorecord.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecordActivity extends AppCompatActivity implements IRecordListener, ITimeListener {
    private static final String TAG = "RecordActivity";
    private VoyahAudioRecord voyahAudioRecord = null;
    private final List<AsrBean> asrList = new ArrayList<>();

    private TextView textAsr;

    private TextView textNext;

    private TimeView timeView;

    private SeekBar seekBar;

    private boolean isPause = true;

    private String path;

    private String name;


    private String recordPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        String filePath = this.getExternalFilesDir("config") + File.separator + "asr.txt";
        List<String> asrConfig = FileUtils.readFileLineByLine(filePath);
        if (!asrConfig.isEmpty()) {
            for (int i = 0; i < asrConfig.size(); i += 2) {
                AsrBean asrBean = new AsrBean();
                asrBean.setId(asrConfig.get(i));
                asrBean.setText(asrConfig.get(i + 1));
                asrList.add(asrBean);
            }
        }
        if (asrList.isEmpty()) {
            Toast.makeText(this, "请先将asr.txt push到" + filePath, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        int channel = getIntent().getIntExtra("channel", 0);
        int sampleRate = getIntent().getIntExtra("sampleRate", 0);
        path = getIntent().getStringExtra("path");
        name = getIntent().getStringExtra("name");
        initView();
        LogUtils.i(TAG, "channel:" + channel + ",sampleRate:" + sampleRate);
        voyahAudioRecord = new VoyahAudioRecord(this);
        voyahAudioRecord.startRecord();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWorker();
    }

    private void createFile(int index) {
        LogUtils.i(TAG, "createFile:" + isPause);
        if (isPause) {
            return;
        }
        String fileName = name + "_" + asrList.get(index).getId() + ".pcm";
        fileName = FileUtils.formatFileName(fileName);
        recordPath = path + File.separator + fileName;
        String truthFile = path + File.separator + "truth.txt";
        FileUtils.appendTextToFile(truthFile, fileName + " " + asrList.get(index).getText());
        File file = new File(recordPath);
        if (file.exists()) {
            boolean result = file.delete();
            LogUtils.i(TAG, "delete:" + recordPath + ",result:" + result);
        }
        LogUtils.i(TAG, "recordPath:" + recordPath);
    }

    private void initView() {
        seekBar = findViewById(R.id.seek_bar);
        timeView = findViewById(R.id.text_time);
        timeView.setTimeListener(this);
        textAsr = findViewById(R.id.text_asr);
        textNext = findViewById(R.id.text_next);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LogUtils.i(TAG, "progress:" + progress + "fromUser:" + fromUser);
                TextView textProcess = findViewById(R.id.text_process);
                textProcess.setText((progress + 1) + "/" + (seekBar.getMax() + 1));
                textAsr.setText(asrList.get(progress).getText());
                if (progress + 1 <= seekBar.getMax()) {
                    textNext.setText(asrList.get(progress + 1).getText());
                } else {
                    textNext.setText("");
                }
                createFile(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setMax(asrList.size() - 1);
        seekBar.setProgress(0);
        Button bt_start = findViewById(R.id.bt_start);
        bt_start.setOnClickListener(v -> startWork());
        Button bt_back = findViewById(R.id.bt_back);
        bt_back.setOnClickListener(v -> finish());
        Button bt_del = findViewById(R.id.bt_del);
        bt_del.setOnClickListener(v ->
                FileUtils.deleteFile(this.getFilesDir().getPath(), "pcm"));
        Button bt_pause = findViewById(R.id.bt_pause);
        bt_pause.setOnClickListener(v -> {
            isPause = true;
            timeView.pause();
        });

        Button bt_prev = findViewById(R.id.bt_prev);

        bt_prev.setOnClickListener(v -> {
            int process = seekBar.getProgress();
            if (process > 0) {
                seekBar.setProgress(process - 1);
            }
        });
        Button bt_next = findViewById(R.id.bt_next);
        bt_next.setOnClickListener(v -> {
            int process = seekBar.getProgress();
            if (process < seekBar.getMax()) {
                seekBar.setProgress(process + 1);
            }
        });

    }

    private void startWork() {
        if (isPause) {
            isPause = false;
        }
        timeView.setTime(getTime(seekBar.getProgress()));
        createFile(seekBar.getProgress());

    }

    private void stopWorker() {
        if (voyahAudioRecord != null) {
            voyahAudioRecord.releaseRecord();
        }
    }


    @Override
    public void recordStart() {

    }

    @Override
    public void recordData(byte[] data, int len) {
        synchronized (this) {
            if (recordPath != null && !isPause) {
                FileUtils.writeDataToFile(recordPath, data, len);
            }
        }
    }


    @Override
    public void recordStop() {

    }


    public int getTime(int index) {
        EditText editText = findViewById(R.id.edit_coast_time);
        int time = 400;
        if (editText.getText() != null && editText.getText().length() > 0) {
            time = Integer.parseInt(editText.getText().toString());
        }
        String asr = asrList.get(index).getText();
        int total = asr.length() * time + 1500;
        if (total < 3000) {
            total = 3000;
        }
        return total;
    }

    @Override
    public void timeDone() {
        synchronized (this) {
            if (seekBar.getProgress() != seekBar.getMax()) {
                seekBar.setProgress(seekBar.getProgress() + 1);
                timeView.setTime(getTime(seekBar.getProgress()));
            } else {
                recordPath = null;
                Toast.makeText(RecordActivity.this, "录音完成", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
