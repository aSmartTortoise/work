package com.voyah.vcos.asraudiorecord;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.voyah.vcos.asraudiorecord.databinding.ActivityMainBinding;
import com.voyah.vcos.asraudiorecord.util.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initPermission();
        initView();
    }

    private void initView() {
        binding.spinnerSex.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"男(1)", "女(2)"}));
        String[] age = new String[100];
        for (int i = 0; i < age.length; i++) {
            age[i] = String.valueOf(i + 1);
        }
        binding.spinnerAge.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, age));
        binding.spinnerMic.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"6", "8"}));
        binding.spinnerCaiyang.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"16000", "48000"}));
        String[] cj = new String[50];
        for (int i = 0; i < cj.length; i++) {
            cj[i] = String.valueOf(i + 1);
        }
        binding.spinnerChangjing.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, cj));
        binding.spinnerShengyuan.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"主驾(1)", "副驾(2)", "二排左(3)", "二排右(4)", "二排中(5)", "三排左(6)", "三排右(7)", "三排中(8)"}));
        binding.spinnerChesu.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"静止(1)", "城市道路(2)", "高架(3)", "高速公路(4)"}));
        binding.spinnerCar.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"H37", "H97C", "H56B", "H56C", "H53A", "H77", "H37B", "H56D"}));
        binding.spinnerLeixing.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"唤醒(1)", "ASR(2)", "可见即可说(3)", "唤醒+ASR(4)"}));
        binding.spinnerYuzhong.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"普通话(1)", "粤语(2)", "四川话(3)"}));
        binding.spinnerCankao.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"0通道", "1通道", "2通道"}));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        binding.txtDate.setText(simpleDateFormat.format(new Date()));
        binding.txtConfig.setText("");
        binding.btnStart.setOnClickListener(this);
        int count = binding.getRoot().getChildCount();
        for (int i = 0; i < count; i++) {
            View view = binding.getRoot().getChildAt(i);
            if (view instanceof Spinner) {
                ((Spinner) view).setOnItemSelectedListener(this);
            }
            if (view instanceof EditText) {
                ((EditText) view).addTextChangedListener(this);
            }
        }
    }


    private void initPermission() {
        LogUtils.i(TAG, "request permission");
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        LogUtils.i(TAG, "toApplyList.size is " + toApplyList);
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        } else {
            LogUtils.i(TAG, "init audio recorder");
        }
    }

    //批次_语种_类型_车类型_车速状态_声源位置_场景_麦克风通道数_参考通道数_录音日期_性别_年龄_录音人编号_文本ID.pcm

    private String getSpinnerSelect(Spinner spinner) {
        String str = (String) spinner.getSelectedItem();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return "0";
    }

    private void updateConfig() {
        String pc = binding.editPici.getText().toString();
        String bianhao = binding.editBianhao.getText().toString();
        if (TextUtils.isEmpty(pc) || TextUtils.isEmpty(bianhao)) {
            binding.txtConfig.setText("");
            return;
        }
        String date = binding.txtDate.getText().toString();
        String config = pc + "_" + getSpinnerSelect(binding.spinnerYuzhong) + "_" + getSpinnerSelect(binding.spinnerLeixing) + "_" + (binding.spinnerCar.getSelectedItemId() + 1)
                + "_" + getSpinnerSelect(binding.spinnerChesu) + "_" + getSpinnerSelect(binding.spinnerShengyuan) + "_" + getSpinnerSelect(binding.spinnerChangjing)
                + "_" + getSpinnerSelect(binding.spinnerMic) + "_" + getSpinnerSelect(binding.spinnerCankao) + "_" + getSpinnerSelect(binding.spinnerCaiyang) + "_" + date + "_" + getSpinnerSelect(binding.spinnerSex)
                + "_" + getSpinnerSelect(binding.spinnerAge) + "_" + bianhao;
        binding.txtConfig.setText(config);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtils.i(TAG, "requestCode is " + requestCode + "init audio recorder");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateConfig();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateConfig();
    }

    @Override
    public void onClick(View v) {
        updateConfig();
        String folderName = binding.txtConfig.getText().toString();
        if (TextUtils.isEmpty(folderName)) {
            Toast.makeText(this, "请先输入必要参数", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = this.getApplication().getExternalFilesDir("audio");
        String path = file.getAbsolutePath() + File.separator + folderName;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        LogUtils.i(TAG, "path:" + path);
        Bundle bundle = new Bundle();
        bundle.putInt("channel", Integer.parseInt(getSpinnerSelect(binding.spinnerMic)));
        bundle.putInt("sampleRate", Integer.parseInt(getSpinnerSelect(binding.spinnerCaiyang)));
        bundle.putString("path", fileDir.getAbsolutePath());
        bundle.putString("name", folderName);
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}