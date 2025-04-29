package com.voyah.h37z.fragment;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.vcos.common.bean.CustomTabEntity;
import com.vcos.common.bean.TabEntity;
import com.vcos.common.widgets.button.VcosTabHorizontal;
import com.voyah.h37z.R;
import com.voyah.h37z.databinding.FragmentConnectBinding;
import com.voyah.viewcmd.Response;
import com.voyah.viewcmd.VoiceViewCmdUtils;
import com.voyah.viewcmd.aspect.VoiceInterceptor;
import com.voyah.viewcmd.interceptor.SimpleInterceptor;

import java.util.ArrayList;
import java.util.Map;

public class ConnectFragment extends Fragment {

    private FragmentConnectBinding binding;
    private int i = 0;

    private SpannableStringBuilder spanStringBuilder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect, null, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
//        binding.tsHotspot.setSwitchListener((buttonView, isChecked) -> {
//            Log.d("xyj", "key tone switch :  " + isChecked);
//        });

        binding.vtTabWiper.setDataListWithTitles(R.string.low_speed,
                R.string.standard_speed, R.string.mid_speed, R.string.high_speed);
        binding.vtTabWiper.setSelect(0);

        ArrayList<CustomTabEntity> tabSmellList = new ArrayList<>();
        TabEntity tab1 = new TabEntity(getString(R.string.smell_type1), 0, 0);
        TabEntity tab2 = new TabEntity(getString(R.string.smell_type2), 0, 0);
        TabEntity tab3 = new TabEntity(getString(R.string.smell_type3), 0, 0);
        tabSmellList.add(tab1);
        tabSmellList.add(tab2);
        tabSmellList.add(tab3);
        binding.vtTabSmell.setTabData(tabSmellList);
        binding.vtTabSmell.setCurrentTab(0);

//        ArrayList<CustomTabEntity> tabAmbLightList = new ArrayList<>();
//        TabEntity tabLight1 = new TabEntity(getString(R.string.privacy_security), "", 0, 0);
//        TabEntity tabLight2 = new TabEntity(getString(R.string.information_hide), "", 0, 0);
//        tabAmbLightList.add(tabLight1);
//        tabAmbLightList.add(tabLight2);
//        binding.vtTabLight.setTabData(tabAmbLightList);
//        binding.vtTabLight.setCurrentTab(0);

        // 前照灯模式
        ArrayList<CustomTabEntity> headLampModeList = new ArrayList<>();
        headLampModeList.add(new TabEntity(getString(R.string.off_english), R.drawable.ic_car_light_off, R.drawable.ic_car_light_off));
        headLampModeList.add(new TabEntity("", R.drawable.ic_car_lights_low_beam, R.drawable.ic_car_lights_low_beam));
        headLampModeList.add(new TabEntity("", R.drawable.ic_car_lights_show_width, R.drawable.ic_car_lights_show_width));
        headLampModeList.add(new TabEntity(getString(R.string.auto), 0, 0));
        binding.vtTabLight.setTabData(headLampModeList);
        binding.vtTabLight.setOnTabSelectListener(new VcosTabHorizontal.OnVsOsTabSelectListener() {
            @Override
            public void onTabSelect(int i) {
                Log.d("xyj", "onTabSelect() called with: i = [" + i + "]");
                View vcosTabView = getVcosTabView(binding.vtTabLight, i);
                if (VoiceViewCmdUtils.isClickByViewCmd(vcosTabView)) {
                    Log.d("xyj", "可见触发");
                } else {
                    Log.d("xyj", "手动触发");
                }
            }
        });
        VoiceViewCmdUtils.setViewCmd(getVcosTabView(binding.vtTabLight, 0), "关闭");
        VoiceViewCmdUtils.setViewCmd(getVcosTabView(binding.vtTabLight, 1), "近光灯|前照灯");
        VoiceViewCmdUtils.setViewCmd(getVcosTabView(binding.vtTabLight, 2), "示宽灯");
        VoiceViewCmdUtils.setViewCmd(getVcosTabView(binding.vtTabLight, 3), "自动");

        binding.tvSkywindowValue.setOnClickListener(v -> binding.tvSkywindowValue.setText("加热" + (i++)));
        binding.ivDefrostFront.setOnClickListener(v -> binding.ivDefrostFront.setSelected(!binding.ivDefrostFront.isSelected()));

        binding.tsHotspot.setClickListener(v -> Log.d("xyj", "tsHotspot onClick() called with: v = [" + v + "]"));
        binding.tsHotspot.setItemEnabled(true);

        //设置开关的可见性
        binding.tsWifi.setBackground(com.vcos.common.widgets.R.color.transparent);
        binding.tsWifi.setIconVisible(false);
        binding.tsWifi.setTitle("隐私保护");
        binding.tsWifi.setSwitchVisible(true);

//        binding.tsWifi.setSwitchVisible(true);
//        binding.tsWifi.setTitle(R.string.privacy_protect);
//        //设置左侧副文本信息可见性(左下文本)
//        binding.tsWifi.setSubTitle(R.string.privacy_protect_tips);
//        binding.tsWifi.getMSubTitle().setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                getResources().getDimension(R.dimen.sp_20));
//        //设置图标可见性
//        binding.tsWifi.setIconVisible(false);
//        //设置图标可见性
//        binding.tsWifi.setBadgeIconVisible(false);
//        binding.tsWifi.setOnSwitchItemViewClickListener(new VcosSwitchItemView.OnSwitchItemViewClickListener() {
//            @Override
//            public void onItemViewClick() {
//                Log.d("xyj", "onItemViewClick() called");
//            }
//
//            @Override
//            public void onCheckedChanged(VcosSwitch vcosSwitch, boolean b, boolean b1) {
//                Log.d("xyj", "onCheckedChanged() called with: vcosSwitch = [" + vcosSwitch + "], b = [" + b + "], b1 = [" + b1 + "]");
//                if (VoiceViewCmdUtils.isClickByViewCmd(vcosSwitch)) {
//                    Log.d("xyj", "可见触发");
//                } else {
//                    Log.d("xyj", "手动触发");
//                }
//            }
//        });

        binding.tvItemContent.setText("我是广东话");
        binding.tvItemContent.setSelected(false);
        binding.tvItemContent.setOnClickListener(v -> {
            Log.d("ConnectFragment", "onClick() called, selected:" + binding.tvItemContent.isSelected());
            binding.tvItemContent.setSelected(!binding.tvItemContent.isSelected());
        });

        binding.vcosBtn.setOnClickListener(v -> {
            if (state == 0) {
                binding.vcosBtn.setText("暂停");
                state = 1;
            } else {
                binding.vcosBtn.setText("开启");
                state = 0;
            }
            if (VoiceViewCmdUtils.isClickByViewCmd(v)) {
                Log.d("xyj", "可见触发");
            } else {
                Log.d("xyj", "手动触发");
            }
        });


        String str = "阅读完整的《用户服务协议》和《隐私政策》了解详细内容";
        spanStringBuilder = new SpannableStringBuilder();
        spanStringBuilder.append(str);
        //第一个出现的位置
        final int start = str.indexOf("《");
        spanStringBuilder.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View view) {
                //用户服务协议点击事件
                Toast.makeText(view.getContext(), "用户服务协议 被点击了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文件颜色
                ds.setColor(getResources().getColor(R.color.red_461));
                // 去掉下划线
                ds.setUnderlineText(false);
            }

        }, start, start + 8, 0);
        //最后一个出现的位置
        final int end = str.lastIndexOf("《");
        spanStringBuilder.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View view) {
                //隐私协议点击事件
                Log.d("xyj", "onClick() called with: view = [" + view + "]");
                Toast.makeText(view.getContext(), "隐私协议被点击了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文件颜色
                ds.setColor(getResources().getColor(R.color.red_461));
                // 去掉下划线
                ds.setUnderlineText(false);
            }

        }, end, end + 6, 0);
        binding.tvSkywindow.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvSkywindow.setText(spanStringBuilder, TextView.BufferType.SPANNABLE);

    }

    int state = 0;

    @VoiceInterceptor
    private final SimpleInterceptor interceptor = new SimpleInterceptor() {

        @Override
        public Map<String, Integer> bind() {
            ArrayMap<String, Integer> map = new ArrayMap<>();
            map.put("用户服务协议", 1);
            map.put("隐私协议", 2);
            return map;
        }

        @Override
        public boolean onTriggered(String text, int resId) {
            if (resId == 1 || resId == 2) {
                ClickableSpan[] foundSpans = spanStringBuilder.getSpans(0, spanStringBuilder.length(), ClickableSpan.class);
                if ("用户服务协议".equals(text) && foundSpans.length == 2) {
                    foundSpans[0].onClick(binding.tvSkywindow);
                    return true;
                } else if ("隐私协议".equals(text) && foundSpans.length == 2) {
                    foundSpans[1].onClick(binding.tvSkywindow);
                    return true;
                }
            }
            return false;
        }

        @Override
        public Map<String, View> bind(View view, String text) {
            ArrayMap<String, View> map = new ArrayMap<>();
            switch (view.getId()) {
                case R.id.tv_title:
                    if (VoiceViewCmdUtils.isChildViewOf(view, binding.vtTabWiper)) {
                        map.put("驾驶模式切到" + text, view);
                    }
                    break;
                case R.id.tv_tab_title:
                    if (VoiceViewCmdUtils.isChildViewOf(view, binding.vtTabSmell)) {
                        map.put("香氛设置切到" + text, view);
                    }
                    break;
                case R.id.radioButton1:
                    map.put("灯光切到选项1", view);
                    break;
            }
            if (text != null && VoiceViewCmdUtils.isChildViewOf(view, binding.vtTabLight)) {
                switch (text) {
                    case "关闭":
                        map.put("车外灯光" + text, view);
                    case "OFF":
                    case "近光灯":
                    case "前照灯":
                    case "示宽灯":
                    case "AUTO":
                    case "自动":
                        map.put("车外灯光切到" + text, view);
                        break;
                }
            }
            return map;
        }

        @Override
        public Response onTriggered(View view, String text, int resId) {
            return null;
        }

        @Override
        public Response onGlobalTriggered(String text) {
            Log.d("xyj", "onGlobalTriggered() called with: text = [" + text + "]");
            if (!TextUtils.isEmpty(text)) {
                return Response.response("我是" + text);
            }
            return null;
        }
    };

    private boolean isParkLevel() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    private View getVcosTabView(VcosTabHorizontal vcosTab, int index) {
        LinearLayout container = (LinearLayout) vcosTab.getChildAt(0);
        if (container.getChildCount() > index) {
            return ((ViewGroup) container.getChildAt(index)).getChildAt(0);
        } else {
            return vcosTab;
        }
    }
}
