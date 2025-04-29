package com.voyah.h37z;

import java.io.Serializable;

public class FragmentTabInfo implements Serializable {

    private static final long serialVersionUID = -9136496764665795064L;

    public int tabId;

    public int unSelectedIcon;

    public int selectedIcon;

    public String tabTitle;

    /**
     * 选择和未选中icon.
     */
    public FragmentTabInfo(int tabId, int unSelectedIcon, int selectedIcon, String tabTitle) {
        this.tabId = tabId;
        this.unSelectedIcon = unSelectedIcon;
        this.selectedIcon = selectedIcon;
        this.tabTitle = tabTitle;
    }
}
