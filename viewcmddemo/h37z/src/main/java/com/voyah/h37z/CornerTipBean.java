package com.voyah.h37z;


import java.util.List;

public class CornerTipBean extends CommonTipBean{

    public int position;
    public boolean showCorner;

    public CornerTipBean() {
    }

    public CornerTipBean(String image, List<String> tips, String title) {
        super(image, tips, title);
    }
}
