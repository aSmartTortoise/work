package com.voyah.viewcmd;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.voyah.viewcmd.Response.ErrCode;

public class GestureUtils {

    private static final String TAG = GestureUtils.class.getSimpleName();
    /**
     * 手势常量
     */
    public static final int RES_ID_GESTURE_MIN = 100;

    public static final int RES_ID_GESTURE_UP = RES_ID_GESTURE_MIN + 1;
    public static final int RES_ID_GESTURE_DOWN = RES_ID_GESTURE_MIN + 2;
    public static final int RES_ID_GESTURE_LEFT = RES_ID_GESTURE_MIN + 3;
    public static final int RES_ID_GESTURE_RIGHT = RES_ID_GESTURE_MIN + 4;
    public static final int RES_ID_GESTURE_PAGE_UP = RES_ID_GESTURE_MIN + 5;
    public static final int RES_ID_GESTURE_PAGE_DOWN = RES_ID_GESTURE_MIN + 6;
    public static final int RES_ID_GESTURE_UP_BEGIN = RES_ID_GESTURE_MIN + 7;
    public static final int RES_ID_GESTURE_DOWN_END = RES_ID_GESTURE_MIN + 8;
    public static final int RES_ID_GESTURE_LEFT_BEGIN = RES_ID_GESTURE_MIN + 9;
    public static final int RES_ID_GESTURE_RIGHT_END = RES_ID_GESTURE_MIN + 10;
    public static final int RES_ID_GESTURE_PAGE_UP_BEGIN = RES_ID_GESTURE_MIN + 11;
    public static final int RES_ID_GESTURE_PAGE_DOWN_END = RES_ID_GESTURE_MIN + 12;

    public static final int RES_ID_GESTURE_MAX = 120;

    /**
     * 移动的系数
     */
    private static final float X_SLOT = 0.3f;

    /**
     * orientation与gesture值映射，以方便使用
     */
    protected static Map<String, Integer> ORIENTATION_MAP = new HashMap<>();
    protected static final int BIT_MASK = 7;
    protected static final int BIT_VERTICAL = 1;
    protected static final int BIT_HORIZONTAL = 2;
    protected static final int BIT_PAGE = 4;

    static {
        // 1 vertical, 2 horizontal, 4, page
        ORIENTATION_MAP.put(VoiceViewCmdUtils.mCtx.getString(R.string.attr_gesture_value_vertical), 1);
        ORIENTATION_MAP.put(VoiceViewCmdUtils.mCtx.getString(R.string.attr_gesture_value_horizontal), 2);
        ORIENTATION_MAP.put(VoiceViewCmdUtils.mCtx.getString(R.string.attr_gesture_value_vertical_horizontal), (1 | 2));
        ORIENTATION_MAP.put(VoiceViewCmdUtils.mCtx.getString(R.string.attr_gesture_value_vertical_page), (1 | 4));
        ORIENTATION_MAP.put(VoiceViewCmdUtils.mCtx.getString(R.string.attr_gesture_value_horizontal_page), (2 | 4));
        ORIENTATION_MAP.put(VoiceViewCmdUtils.mCtx.getString(R.string.attr_gesture_value_all), (1 | 2 | 4));
    }

    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    public static int performGesture(View view, int gesture) {
        if (view instanceof ScrollView) {
            return scrollScrollView((ScrollView) view, gesture);
        } else if (view instanceof NestedScrollView) {
            return scrollNestedScrollView((NestedScrollView) view, gesture);
        } else if (view instanceof HorizontalScrollView) {
            return scrollHorizontalScrollView((HorizontalScrollView) view, gesture);
        } else if (view instanceof RecyclerView) {
            return scrollRecyclerView((RecyclerView) view, gesture);
        } else if (view instanceof ViewPager) {
            return scrollViewPager((ViewPager) view, gesture);
        } else if (view instanceof ViewPager2) {
            return scrollViewPager2((ViewPager2) view, gesture);
        } else if (view instanceof WebView) {
            return scrollWebView((WebView) view, gesture);
        }
        return ErrCode.EC_UNKNOWN;
    }


    /**
     * 判断RecyclerView的滑动方向
     */
    public static boolean isVertical(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return linearLayoutManager.getOrientation() != LinearLayoutManager.HORIZONTAL;
        } else {
            return true;
        }
    }

    private static int scrollWebView(WebView webView, int gesture) {
        int visibleHeight = webView.getHeight();
        int contentHeight = getWebViewContentHeight(webView);
        int scrollDistance = (int) (visibleHeight * X_SLOT);

        if (gesture == RES_ID_GESTURE_DOWN) {
            if (webView.getScrollY() + visibleHeight >= contentHeight) {
                return ErrCode.EC_GESTURE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollBy(0, scrollDistance);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_UP) {
            if (webView.getScrollY() == 0) {
                return ErrCode.EC_GESTURE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollBy(0, -scrollDistance);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP) {
            if (webView.getScrollY() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollBy(0, -visibleHeight);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN) {
            if (webView.getScrollY() + visibleHeight >= contentHeight) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollBy(0, visibleHeight);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_UP_BEGIN) {
            if (webView.getScrollY() == 0) {
                return ErrCode.EC_GESTURE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollTo(0, 0);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_DOWN_END) {
            if (webView.getScrollY() + visibleHeight >= contentHeight) {
                return ErrCode.EC_GESTURE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollTo(0, contentHeight - visibleHeight);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) {
            if (webView.getScrollY() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollTo(0, 0);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
            if (webView.getScrollY() + visibleHeight >= contentHeight) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    webView.stopNestedScroll();
                    webView.scrollTo(0, contentHeight - visibleHeight);
                });
                return ErrCode.EC_NORMAL;
            }
        }
        return ErrCode.EC_UNKNOWN;
    }

    private static int scrollViewPager(ViewPager viewPager, int gesture) {
        int currentItem = viewPager.getCurrentItem();
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            int totalCount = adapter.getCount();
            if (gesture == RES_ID_GESTURE_LEFT || gesture == RES_ID_GESTURE_RIGHT) {
                int nextPage = (gesture == RES_ID_GESTURE_RIGHT) ? currentItem + 1 : currentItem - 1;
                if (nextPage < 0) {
                    return ErrCode.EC_GESTURE_LEFT_MAX;
                } else if (nextPage >= totalCount) {
                    return ErrCode.EC_GESTURE_RIGHT_MAX;
                } else {
                    uiHandler.post(() -> viewPager.setCurrentItem(nextPage, true));
                    return ErrCode.EC_NORMAL;
                }
            } else if (gesture == RES_ID_GESTURE_LEFT_BEGIN || gesture == RES_ID_GESTURE_RIGHT_END) {
                int nextPage = (gesture == RES_ID_GESTURE_RIGHT_END) ? currentItem + 1 : currentItem - 1;
                if (nextPage < 0) {
                    return ErrCode.EC_GESTURE_LEFT_MAX;
                } else if (nextPage >= totalCount) {
                    return ErrCode.EC_GESTURE_RIGHT_MAX;
                } else {
                    int newNextPage = (gesture == RES_ID_GESTURE_RIGHT_END) ? totalCount - 1 : 0;
                    uiHandler.post(() -> viewPager.setCurrentItem(newNextPage, true));
                    return ErrCode.EC_NORMAL;
                }
            } else if (gesture == RES_ID_GESTURE_PAGE_DOWN || gesture == RES_ID_GESTURE_PAGE_UP) {
                int nextPage = (gesture == RES_ID_GESTURE_PAGE_DOWN) ? currentItem + 1 : currentItem - 1;
                if (nextPage < 0) {
                    return ErrCode.EC_GESTURE_LEFT_MAX;
                } else if (nextPage >= totalCount) {
                    return ErrCode.EC_GESTURE_RIGHT_MAX;
                } else {
                    uiHandler.post(() -> viewPager.setCurrentItem(nextPage, true));
                    return ErrCode.EC_NORMAL;
                }
            } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN || gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
                int nextPage = (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) ? currentItem + 1 : currentItem - 1;
                if (nextPage < 0) {
                    return ErrCode.EC_GESTURE_LEFT_MAX;
                } else if (nextPage >= totalCount) {
                    return ErrCode.EC_GESTURE_RIGHT_MAX;
                } else {
                    int newNextPage = (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) ? totalCount - 1 : 0;
                    uiHandler.post(() -> viewPager.setCurrentItem(newNextPage, true));
                    return ErrCode.EC_NORMAL;
                }
            }
        }
        return ErrCode.EC_UNKNOWN;
    }

    private static int scrollViewPager2(ViewPager2 viewPager2, int gesture) {
        int currentItem = viewPager2.getCurrentItem();
        RecyclerView.Adapter<?> adapter = viewPager2.getAdapter();
        if (adapter != null) {
            int totalCount = adapter.getItemCount();
            int orientation = viewPager2.getOrientation();
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (gesture == RES_ID_GESTURE_LEFT || gesture == RES_ID_GESTURE_RIGHT) {
                    int nextPage = (gesture == RES_ID_GESTURE_RIGHT) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_LEFT_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_RIGHT_MAX;
                    } else {
                        uiHandler.post(() -> viewPager2.setCurrentItem(nextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                } else if (gesture == RES_ID_GESTURE_LEFT_BEGIN || gesture == RES_ID_GESTURE_RIGHT_END) {
                    int nextPage = (gesture == RES_ID_GESTURE_RIGHT_END) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_LEFT_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_RIGHT_MAX;
                    } else {
                        int newNextPage = (gesture == RES_ID_GESTURE_RIGHT_END) ? totalCount - 1 : 0;
                        uiHandler.post(() -> viewPager2.setCurrentItem(newNextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                } else if (gesture == RES_ID_GESTURE_PAGE_DOWN || gesture == RES_ID_GESTURE_PAGE_UP) {
                    int nextPage = (gesture == RES_ID_GESTURE_PAGE_DOWN) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_LEFT_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_RIGHT_MAX;
                    } else {
                        uiHandler.post(() -> viewPager2.setCurrentItem(nextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN || gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
                    int nextPage = (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_LEFT_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_RIGHT_MAX;
                    } else {
                        int newNextPage = (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) ? totalCount - 1 : 0;
                        uiHandler.post(() -> viewPager2.setCurrentItem(newNextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                }
            } else {
                if (gesture == RES_ID_GESTURE_UP || gesture == RES_ID_GESTURE_DOWN) {
                    int nextPage = (gesture == RES_ID_GESTURE_DOWN) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_UP_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_DOWN_MAX;
                    } else {
                        uiHandler.post(() -> viewPager2.setCurrentItem(nextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                } else if (gesture == RES_ID_GESTURE_UP_BEGIN || gesture == RES_ID_GESTURE_DOWN_END) {
                    int nextPage = (gesture == RES_ID_GESTURE_DOWN_END) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_UP_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_DOWN_MAX;
                    } else {
                        int newNextPage = (gesture == RES_ID_GESTURE_DOWN_END) ? totalCount - 1 : 0;
                        uiHandler.post(() -> viewPager2.setCurrentItem(newNextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                } else if (gesture == RES_ID_GESTURE_PAGE_DOWN || gesture == RES_ID_GESTURE_PAGE_UP) {
                    int nextPage = (gesture == RES_ID_GESTURE_PAGE_DOWN) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_UP_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_DOWN_MAX;
                    } else {
                        uiHandler.post(() -> viewPager2.setCurrentItem(nextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN || gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
                    int nextPage = (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) ? currentItem + 1 : currentItem - 1;
                    if (nextPage < 0) {
                        return ErrCode.EC_GESTURE_UP_MAX;
                    } else if (nextPage >= totalCount) {
                        return ErrCode.EC_GESTURE_DOWN_MAX;
                    } else {
                        int newNextPage = (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) ? totalCount - 1 : 0;
                        uiHandler.post(() -> viewPager2.setCurrentItem(newNextPage, true));
                        return ErrCode.EC_NORMAL;
                    }
                }
            }
        }
        return ErrCode.EC_UNKNOWN;
    }

    private static int scrollHorizontalScrollView(HorizontalScrollView horizontalScrollView, int gesture) {
        View childAt = horizontalScrollView.getChildAt(0);
        int width = Math.min(childAt.getMeasuredWidth(), getScreenWidth());
        int scrollAmount = (int) (width * X_SLOT);
        if (gesture == RES_ID_GESTURE_LEFT) {
            if (horizontalScrollView.getScrollX() == 0) {
                return ErrCode.EC_GESTURE_LEFT_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.smoothScrollBy(-scrollAmount, 0);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_RIGHT) {
            int maxScroll = childAt.getMeasuredWidth() - horizontalScrollView.getMeasuredWidth();
            if (horizontalScrollView.getScrollX() >= maxScroll) {
                return ErrCode.EC_GESTURE_RIGHT_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.smoothScrollBy(scrollAmount, 0);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_LEFT_BEGIN) {
            if (horizontalScrollView.getScrollX() == 0) {
                return ErrCode.EC_GESTURE_LEFT_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_RIGHT_END) {
            int maxScroll = childAt.getMeasuredWidth() - horizontalScrollView.getMeasuredWidth();
            if (horizontalScrollView.getScrollX() >= maxScroll) {
                return ErrCode.EC_GESTURE_RIGHT_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP) {
            if (horizontalScrollView.getScrollX() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.smoothScrollBy(-width, 0);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN) {
            int maxScroll = childAt.getMeasuredWidth() - horizontalScrollView.getMeasuredWidth();
            if (horizontalScrollView.getScrollX() >= maxScroll) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.smoothScrollBy(width, 0);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) {
            if (horizontalScrollView.getScrollX() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
            int maxScroll = childAt.getMeasuredWidth() - horizontalScrollView.getMeasuredWidth();
            if (horizontalScrollView.getScrollX() >= maxScroll) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    horizontalScrollView.stopNestedScroll();
                    horizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                });
                return ErrCode.EC_NORMAL;
            }
        }
        return ErrCode.EC_UNKNOWN;
    }

    private static int scrollScrollView(ScrollView scrollView, int gesture) {
        View childAt = scrollView.getChildAt(0);
        int height = Math.min(childAt.getMeasuredHeight(), getScreenHeight());
        int scrollAmount = (int) (height * X_SLOT);
        if (gesture == RES_ID_GESTURE_UP) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_GESTURE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, -scrollAmount);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_DOWN) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_GESTURE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, scrollAmount);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_UP_BEGIN) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_GESTURE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_DOWN_END) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_GESTURE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, -height);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, height);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                });
                return ErrCode.EC_NORMAL;
            }
        }
        return ErrCode.EC_UNKNOWN;
    }

    private static int scrollNestedScrollView(NestedScrollView scrollView, int gesture) {
        View childAt = scrollView.getChildAt(0);
        int height = Math.min(childAt.getMeasuredHeight(), getScreenHeight());
        int scrollAmount = (int) (height * X_SLOT);
        if (gesture == RES_ID_GESTURE_UP) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_GESTURE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, -scrollAmount);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_DOWN) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_GESTURE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, scrollAmount);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_UP_BEGIN) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_GESTURE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_DOWN_END) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_GESTURE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, -height);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.smoothScrollBy(0, height);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) {
            if (scrollView.getScrollY() == 0) {
                return ErrCode.EC_PAGE_UP_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                });
                return ErrCode.EC_NORMAL;
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
            int maxScroll = childAt.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (scrollView.getScrollY() >= maxScroll) {
                return ErrCode.EC_PAGE_DOWN_MAX;
            } else {
                uiHandler.post(() -> {
                    scrollView.stopNestedScroll();
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                });
                return ErrCode.EC_NORMAL;
            }
        }
        return ErrCode.EC_UNKNOWN;
    }

    private static int scrollRecyclerView(RecyclerView recyclerView, int gesture) {
        if (gesture == RES_ID_GESTURE_UP || gesture == RES_ID_GESTURE_DOWN) {
            if (isVertical(recyclerView)) {
                int scrollAmount = (int) (recyclerView.getMeasuredHeight() * X_SLOT);
                Log.v(TAG, "scrollRecyclerView scrollAmount: " + scrollAmount);
                int offset = recyclerView.computeVerticalScrollOffset();
                if (gesture == RES_ID_GESTURE_UP) {
                    if (offset == 0) {
                        return ErrCode.EC_GESTURE_UP_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(0, -scrollAmount);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                } else {
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();
                    if (offset + extent >= range) {
                        return ErrCode.EC_GESTURE_DOWN_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(0, scrollAmount);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                }
            }
        } else if (gesture == RES_ID_GESTURE_UP_BEGIN || gesture == RES_ID_GESTURE_DOWN_END) {
            if (isVertical(recyclerView)) {
                int offset = recyclerView.computeVerticalScrollOffset();
                if (gesture == RES_ID_GESTURE_UP_BEGIN) {
                    if (offset == 0) {
                        return ErrCode.EC_GESTURE_UP_MAX;
                    } else if (recyclerView.getAdapter() != null) {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollToPosition(0);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                } else {
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();
                    if (offset + extent >= range) {
                        return ErrCode.EC_GESTURE_DOWN_MAX;
                    } else if (recyclerView.getAdapter() != null) {
                        int itemCount = recyclerView.getAdapter().getItemCount();
                        if (itemCount > 0) {
                            uiHandler.post(() -> {
                                recyclerView.stopScroll();
                                recyclerView.smoothScrollToPosition(itemCount - 1);
                            });
                            return ErrCode.EC_NORMAL;
                        }
                    }
                }
            }
        } else if (gesture == RES_ID_GESTURE_LEFT || gesture == RES_ID_GESTURE_RIGHT) {
            if (!isVertical(recyclerView)) {
                int scrollAmount = (int) (recyclerView.getMeasuredWidth() * X_SLOT);
                int offset = recyclerView.computeHorizontalScrollOffset();
                if (gesture == RES_ID_GESTURE_LEFT) {
                    if (offset == 0) {
                        return ErrCode.EC_GESTURE_LEFT_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(-scrollAmount, 0);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                } else {
                    int extent = recyclerView.computeHorizontalScrollExtent();
                    int range = recyclerView.computeHorizontalScrollRange();
                    if (offset + extent >= range) {
                        return ErrCode.EC_GESTURE_RIGHT_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(scrollAmount, 0);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                }
            }
        } else if (gesture == RES_ID_GESTURE_LEFT_BEGIN || gesture == RES_ID_GESTURE_RIGHT_END) {
            if (!isVertical(recyclerView)) {
                int offset = recyclerView.computeHorizontalScrollOffset();
                if (gesture == RES_ID_GESTURE_LEFT_BEGIN) {
                    if (offset == 0) {
                        return ErrCode.EC_GESTURE_LEFT_MAX;
                    } else if (recyclerView.getAdapter() != null) {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollToPosition(0);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                } else {
                    int extent = recyclerView.computeHorizontalScrollExtent();
                    int range = recyclerView.computeHorizontalScrollRange();
                    if (offset + extent >= range) {
                        return ErrCode.EC_GESTURE_RIGHT_MAX;
                    } else if (recyclerView.getAdapter() != null) {
                        int itemCount = recyclerView.getAdapter().getItemCount();
                        if (itemCount > 0) {
                            uiHandler.post(() -> {
                                recyclerView.stopScroll();
                                recyclerView.smoothScrollToPosition(itemCount - 1);
                            });
                            return ErrCode.EC_NORMAL;
                        }
                    }
                }
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP || gesture == RES_ID_GESTURE_PAGE_DOWN) {
            if (isVertical(recyclerView)) {
                int offset = recyclerView.computeVerticalScrollOffset();
                if (gesture == RES_ID_GESTURE_PAGE_UP) {
                    if (offset == 0) {
                        return ErrCode.EC_PAGE_UP_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(0, -recyclerView.getMeasuredHeight());
                        });
                        return ErrCode.EC_NORMAL;
                    }
                } else {
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();
                    if (offset + extent >= range) {
                        return ErrCode.EC_PAGE_DOWN_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(0, recyclerView.getMeasuredHeight());
                        });
                        return ErrCode.EC_NORMAL;
                    }
                }
            } else {
                int offset = recyclerView.computeHorizontalScrollOffset();
                if (gesture == RES_ID_GESTURE_PAGE_UP) {
                    if (offset == 0) {
                        return ErrCode.EC_PAGE_UP_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(-recyclerView.getMeasuredWidth(), 0);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                } else {
                    int extent = recyclerView.computeHorizontalScrollExtent();
                    int range = recyclerView.computeHorizontalScrollRange();
                    if (offset + extent >= range) {
                        return ErrCode.EC_PAGE_DOWN_MAX;
                    } else {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollBy(recyclerView.getMeasuredWidth(), 0);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                }
            }
        } else if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN || gesture == RES_ID_GESTURE_PAGE_DOWN_END) {
            int offset = recyclerView.computeVerticalScrollOffset();
            int extent = recyclerView.computeVerticalScrollExtent();
            int range = recyclerView.computeVerticalScrollRange();
            if (!isVertical(recyclerView)) {
                offset = recyclerView.computeHorizontalScrollOffset();
                extent = recyclerView.computeHorizontalScrollExtent();
                range = recyclerView.computeHorizontalScrollRange();
            }
            if (gesture == RES_ID_GESTURE_PAGE_UP_BEGIN) {
                if (offset == 0) {
                    return ErrCode.EC_PAGE_UP_MAX;
                } else {
                    uiHandler.post(() -> {
                        recyclerView.stopScroll();
                        recyclerView.smoothScrollToPosition(0);
                    });
                    return ErrCode.EC_NORMAL;
                }
            } else {
                if (offset + extent >= range) {
                    return ErrCode.EC_PAGE_DOWN_MAX;
                } else if (recyclerView.getAdapter() != null) {
                    int itemCount = recyclerView.getAdapter().getItemCount();
                    if (itemCount > 0) {
                        uiHandler.post(() -> {
                            recyclerView.stopScroll();
                            recyclerView.smoothScrollToPosition(itemCount - 1);
                        });
                        return ErrCode.EC_NORMAL;
                    }
                }
            }
        }
        return ErrCode.EC_UNKNOWN;
    }

    private static int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((android.view.WindowManager) VoiceViewCmdUtils.mCtx.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private static int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((android.view.WindowManager) VoiceViewCmdUtils.mCtx.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private static int getWebViewContentHeight(WebView webView) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final int[] desktopType = new int[1];
        desktopType[0] = getScreenHeight();
        uiHandler.post(() -> {
            desktopType[0] = (int) (webView.getContentHeight() * webView.getScale());
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        VaLog.v(TAG, "getWebViewContentHeight: " + desktopType[0]);
        return desktopType[0];
    }

}