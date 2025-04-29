package com.voyah.viewcmd.aspect;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.voyah.viewcmd.AntiShake;
import com.voyah.viewcmd.VaLog;
import com.voyah.viewcmd.VoiceViewCmdManager;
import com.voyah.viewcmd.VoiceViewCmdUtils;
import com.voyah.viewcmd.interceptor.IInterceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;

@Aspect
public class ViewCmdAspectProcessor {

    private static final String TAG = ViewCmdAspectProcessor.class.getSimpleName();
    private final Object lockData = new Object();
    private final Object lockScan = new Object();
    private final Object lockDf = new Object();

    @Around("execution(* android.app.Application.onCreate(..))")
    public void onApplicationCreateAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        VaLog.v(TAG, "onApplicationCreateAround, AOP: " + key);
        boolean isCompatibleMode = false;
        boolean isCustomResponse = false;
        int windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        String processName = "";
        VoiceViewCmdInit annotation = joinPoint.getTarget().getClass().getAnnotation(VoiceViewCmdInit.class);
        if (annotation != null) {
            isCompatibleMode = annotation.isCompatibleMode();
            isCustomResponse = annotation.isCustomResponse();
            windowType = annotation.windowType();
            processName = annotation.processName();
        }
        VoiceViewCmdUtils.init((Application) joinPoint.getTarget(), isCompatibleMode, isCustomResponse, windowType, processName);

        joinPoint.proceed();
    }

    @Around("execution(* android.app.Activity.onResume(..))")
    public void onActivityResumeAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        String key = joinPoint.getSignature().toString();
        if (key.contains("androidx.fragment.app.FragmentActivity.onResume()")) {
            Field[] declaredFields = joinPoint.getTarget().getClass().getDeclaredFields();
            IInterceptor<?> interceptor = null;
            for (Field declaredField : declaredFields) {
                VoiceInterceptor annotation = declaredField.getAnnotation(VoiceInterceptor.class);
                if (annotation != null) {
                    declaredField.setAccessible(true);
                    interceptor = (IInterceptor<?>) declaredField.get(joinPoint.getTarget());
                    break;
                }
            }
            VaLog.v(TAG, "onActivityResumeAround, AOP: " + key + ",interceptor:" + interceptor);
            VoiceViewCmdManager.getInstance().register((Activity) joinPoint.getTarget(), interceptor);
        }
    }

    @Around("execution(* android.app.Activity.onPause(..))")
    public void onActivityPauseAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        String key = joinPoint.getSignature().toString();
        if (key.contains("androidx.fragment.app.FragmentActivity.onPause()")) {
            VaLog.v(TAG, "onActivityPauseAround, AOP: " + key);
            VoiceViewCmdManager.getInstance().unregister((Activity) joinPoint.getTarget());
        }
    }

    @Around("execution(* androidx.fragment.app.Fragment.onResume(..))")
    public void onFragmentResumeAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        String key = joinPoint.getSignature().toString();
        if (key.contains("androidx.fragment.app.Fragment.onResume()")) {
            Field[] declaredFields = joinPoint.getTarget().getClass().getDeclaredFields();
            IInterceptor<?> interceptor = null;
            for (Field declaredField : declaredFields) {
                VoiceInterceptor annotation = declaredField.getAnnotation(VoiceInterceptor.class);
                if (annotation != null) {
                    declaredField.setAccessible(true);
                    interceptor = (IInterceptor<?>) declaredField.get(joinPoint.getTarget());
                    break;
                }
            }
            VaLog.v(TAG, "onFragmentResumeAround, AOP: " + key + ", interceptor:" + interceptor);
            VoiceViewCmdManager.getInstance().register((Fragment) joinPoint.getTarget(), interceptor);
        }
    }

    @Around("execution(* com.vcos.common.widgets.base.BaseDialog.onStart(..))")
    public void onVcosOptionDialogStartAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        String key = joinPoint.getSignature().toString();
        if (joinPoint.getTarget() instanceof Dialog) {
            VaLog.d(TAG, "onBaseDialogStartAround, AOP: " + key + ", target:" + ((Dialog) joinPoint.getTarget()).getClass().getSimpleName());
            // 给iv_dismiss增加通用的可见话术
            VoiceViewCmdManager.getInstance().register((Dialog) joinPoint.getTarget(), false, null);
            Window window = ((Dialog) joinPoint.getTarget()).getWindow();
            if (window != null) {
                View view = window.getDecorView();
                Resources resources = view.getContext().getResources();
                String pkgName = view.getContext().getPackageName();
                int ivDismissId = resources.getIdentifier("iv_dismiss", "id", pkgName);
                View ivDismiss = view.findViewById(ivDismissId);
                if (ivDismiss != null && ivDismiss.getVisibility() == View.VISIBLE) {
                    String[] viewCmdAttr = VoiceViewCmdUtils.getViewCmdAttr(ivDismiss);
                    if (viewCmdAttr == null) {
                        VoiceViewCmdUtils.setViewCmd(ivDismiss, "关闭");
                    }
                }
            }
        } else {
            VaLog.e(TAG, "onBaseDialogStartAround, target is not dialog");
        }
    }

    @Around("execution(* androidx.fragment.app.Fragment.onPause(..))")
    public void onFragmentPauseAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        String key = joinPoint.getSignature().toString();
        if (key.contains("androidx.fragment.app.Fragment.onPause()")) {
            VaLog.v(TAG, "onFragmentPauseAround, AOP: " + key);
            VoiceViewCmdManager.getInstance().unregister((Fragment) joinPoint.getTarget());
        }
    }

    @Around("execution(* androidx.fragment.app.DialogFragment.onCreateDialog(..))||execution(* android.app.DialogFragment.onCreateDialog(..))")
    public Dialog onDialogFragmentCreateAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = joinPoint.proceed();

        String key = joinPoint.getSignature().toString();
        if (proceed instanceof Dialog) {
            if (AntiShake.check(lockDf, 50)) {
                return (Dialog) proceed;
            }

            VaLog.v(TAG, "onDialogFragmentCreateAround, AOP:" + key + ",target:" + joinPoint.getTarget().getClass().getSimpleName());
            try {
                View view = ((Dialog) proceed).getWindow().getDecorView().getRootView();
                IInterceptor<?> interceptor = null;
                Field[] declaredFields = joinPoint.getTarget().getClass().getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    VoiceInterceptor annotation = declaredField.getAnnotation(VoiceInterceptor.class);
                    if (annotation != null) {
                        declaredField.setAccessible(true);
                        interceptor = (IInterceptor<?>) declaredField.get(joinPoint.getTarget());
                        break;
                    }
                }
                VoiceViewCmdManager.getInstance().register(view, false, false, interceptor);
                return (Dialog) proceed;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Around("execution(@com.voyah.viewcmd.aspect.VoiceRegisterView * *(..)) && @annotation(registerViewAnnotation)")
    public Object aspectRegisterAnnotation(ProceedingJoinPoint joinPoint, VoiceRegisterView registerViewAnnotation) throws Throwable {
        Object result = joinPoint.proceed();

        String key = joinPoint.getSignature().toString();
        Object target = joinPoint.getTarget();
        Field[] declaredFields = target.getClass().getDeclaredFields();
        IInterceptor<?> interceptor = null;
        for (Field declaredField : declaredFields) {
            VoiceInterceptor annotation = declaredField.getAnnotation(VoiceInterceptor.class);
            if (annotation != null) {
                declaredField.setAccessible(true);
                interceptor = (IInterceptor<?>) declaredField.get(target);
                break;
            }
        }
        if (target instanceof Dialog) {
            VaLog.v(TAG, "aspectRegisterAnnotation, AOP: " + key + ", interceptor:" + interceptor + ", dialog:" + ((Dialog) target).getClass().getSimpleName());
            VoiceViewCmdManager.getInstance().register((Dialog) target, registerViewAnnotation.isSticky(), registerViewAnnotation.isTopCoverView(), interceptor);
        } else if (target instanceof PopupWindow) {
            VaLog.d(TAG, "aspectRegisterAnnotation, AOP: " + key + ", interceptor:" + interceptor + ", popupWindow:" + ((PopupWindow) target).getClass().getSimpleName());
            VoiceViewCmdManager.getInstance().register(((PopupWindow) target), registerViewAnnotation.isSticky(), interceptor);
        } else if (target instanceof DialogFragment) {
            VaLog.d(TAG, "aspectRegisterAnnotation, AOP: " + key + ", interceptor:" + interceptor + ", dialogFragment:" + ((DialogFragment) target).getClass().getSimpleName());
            VoiceViewCmdManager.getInstance().register((DialogFragment) target, registerViewAnnotation.isSticky(), interceptor);
        } else if (target instanceof View) {
            VaLog.v(TAG, "aspectRegisterAnnotation, AOP: " + key + ", interceptor:" + interceptor);
            VoiceViewCmdManager.getInstance().register((View) target, registerViewAnnotation.isSticky(), registerViewAnnotation.isTopCoverView(), interceptor);
        } else {
            View view = null;
            Object[] args = joinPoint.getArgs();
            for (Object object : args) {
                if (object instanceof View) {
                    view = (View) object;
                    break;
                }
            }
            if (view != null) {
                VaLog.v(TAG, "aspectRegisterAnnotation, AOP: " + key + ", interceptor:" + interceptor);
                VoiceViewCmdManager.getInstance().register(view, registerViewAnnotation.isSticky(), registerViewAnnotation.isTopCoverView(), interceptor);
            }
        }
        return result;
    }

    @Around("execution(@com.voyah.viewcmd.aspect.VoiceAutoRefresh * *(..)) && @annotation(autoRefreshAnnotation)")
    public Object aspectAutoRefreshAnnotation(ProceedingJoinPoint joinPoint, VoiceAutoRefresh autoRefreshAnnotation) throws Throwable {
        Object result = joinPoint.proceed();

        if (AntiShake.check(lockScan, 200)) {
            return result;
        }
        String key = joinPoint.getSignature().toString();
        if (autoRefreshAnnotation.isAutoRefresh()) {
            VaLog.d(TAG, "aspectAutoRefreshAnnotation, AOP: " + key);
            VoiceViewCmdManager.getInstance().triggerNotifyUiChange(autoRefreshAnnotation.delay());
        } else {
            VaLog.e(TAG, "aspectAutoRefreshAnnotation, skipped triggerNotifyUiChange, AOP:" + key);
        }
        return result;
    }

    @Pointcut("execution(* androidx.recyclerview.widget.RecyclerView.Adapter.notifyDataSetChanged())")
    public void notifyDataSetChanged() {
    }

    @Pointcut("execution(* androidx.recyclerview.widget.RecyclerView.Adapter.notifyItem*(..))")
    public void notifyItemChanged() {
    }

    @Around("notifyDataSetChanged()||notifyItemChanged()")
    public void notifyRecyclerViewDataChangedAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        if (AntiShake.check(lockData, 500)) {
            return;
        }
        String key = joinPoint.getSignature().toString();
        VoiceAutoRefresh annotation = joinPoint.getTarget().getClass().getAnnotation(VoiceAutoRefresh.class);
        boolean isAutoRefresh = true;
        if (annotation != null) {
            isAutoRefresh = annotation.isAutoRefresh();
        }
        if (isAutoRefresh) {
            VaLog.d(TAG, "notifyRecyclerViewDataChangedAround, AOP: " + key);
            VoiceViewCmdManager.getInstance().triggerNotifyUiChange();
        }
    }

    @Around("execution(void onTabSelected(..))")
    public void tabSelectedAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        String key = joinPoint.getSignature().toString();
        VaLog.d(TAG, "tabSelectedAround, AOP: " + key);
        VoiceViewCmdManager.getInstance().triggerNotifyUiChange();
    }

    @Pointcut("execution(* android.view.View.OnClickListener.onClick(..))")
    public void viewOnClick() {
    }

    @Pointcut("execution(void *..*lambda*(.., android.view.View))")
    public void viewOnClickWithLambda() {
    }

    @Around("viewOnClick()||viewOnClickWithLambda()")
    public void viewOnClickAround(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        if (AntiShake.check(lockScan, 200)) {
            return;
        }
        String key = joinPoint.getSignature().toString();
        if (!key.contains("com.voyah.viewcmd")) {
            VaLog.d(TAG, "viewOnClickAround, AOP: " + key);
            VoiceViewCmdManager.getInstance().triggerNotifyUiChange();
        }
    }
}
