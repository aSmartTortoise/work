package com.voice.sdk.device.navi;


import com.voice.sdk.device.navi.bean.NaviResponse;

@SuppressWarnings("unused")
public interface NaviTeamInterface {

    NaviResponse<Integer> getTeamInfo();

    void createTeam();

    void joinTeam();

    void viewTeam();

    void disbandTeam();

    void exitTeam();

}
