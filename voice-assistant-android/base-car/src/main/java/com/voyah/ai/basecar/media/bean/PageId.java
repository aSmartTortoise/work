package com.voyah.ai.basecar.media.bean;

public enum PageId {
    //  0：关闭 1:前台  2：后台
    main(0),
    QQ_main(1),
    wy_main(2),
    xm_main(3),
    yt_main(4),
    bt_main(5),
    usb_main(6),

    QQ_login(1001),
    wy_login(1002),
    xm_login(1003),
    yt_login(1004),

    QQ_list(1101),
    wy_list(1102),
    xm_list(1103),
    yt_list(1104),
    usb_list(1105),

    QQ_lyric(1201),
    wy_lyric(1202),
    bt_lyric(1203),
    usb_lyric(1204),

    QQ_history(1301),
    wy_history(1302),
    xm_history(1303),
    yt_history(1304),

    QQ_collect(1401),
    wy_collect(1402),
    xm_collect(1403),
    yt_collect(1404),

    yt_broadcast(1504),

    main_play_page(1600),
    QQ_play_page(1601),
    wy_play_page(1602),
    xm_play_page(1603),
    yt_play_page(1604),
    bt_play_page(1605),
    usb_play_page(1606);

    private final int id;

    PageId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
