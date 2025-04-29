package com.voyah.network.api

import com.voyah.common.model.DrawingTimesInfo
import com.voyah.common.model.SchedulePlanInfo
import com.voyah.common.model.User
import com.voyah.network.response.BaseResponse
import com.voyah.network.response.DrawingResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author jackie wong
 * @date   2023/2/27 19:07
 * @desc   API接口类
 */
interface ApiInterface {

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<User>

    @POST("/schedule_plan/chat")
    suspend fun getSchedulePlanning(@Body body: RequestBody): BaseResponse<SchedulePlanInfo>

    @GET("/ds/v1/api/drawing/times")
    suspend fun getRemainingDrawingTimes(@Query("vin") vinCode: String): DrawingResponse<DrawingTimesInfo>









}