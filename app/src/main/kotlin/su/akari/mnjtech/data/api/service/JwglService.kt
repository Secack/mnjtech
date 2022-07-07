package su.akari.mnjtech.data.api.service

import su.akari.mnjtech.data.model.jwgl.*
import okhttp3.FormBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface JwglService {
    @FormUrlEncoded
    @POST("kbcx/xskbcx_cxXsgrkb.html")
    suspend fun getCurriculum(
        @Query("gnmkdm") gnmkdm: String,
        @Query("su") su: String,
        @Field("xnm") xnm: Int,
        @Field("xqm") xqm: Int?,
        @Field("kzlx") kzlx: String
    ): Curriculum

    @FormUrlEncoded
    @POST("cjcx/cjcx_cxXsgrcj.html")
    suspend fun getScoreList(
        @Query("doType") doType: String,
        @Query("gnmkdm") gnmkdm: String,
        @Query("su") su: String,
        @FieldMap yearTerm: Map<String, String>,
        @Field("_search") _search: Boolean,
        @Field("nd") nd: Long,
        @Field("queryModel.showCount") showCount: Int,
        @Field("queryModel.currentPage") currentPage: Int,
        @Field("queryModel.sortName") sortName: String,
        @Field("queryModel.sortOrder") sortOrder: String,
        @Field("time") time: Int
    ): ScoreListPage

    @FormUrlEncoded
    @POST("xspjgl/xspj_cxXspjIndex.html")
    suspend fun getEvaluationIndex(
        @Query("doType") doType: String,
        @Query("gnmkdm") gnmkdm: String,
        @Query("su") su: String,
        @Field("_search") _search: Boolean,
        @Field("nd") nd: Long,
        @Field("queryModel.showCount") showCount: Int,
        @Field("queryModel.currentPage") currentPage: Int,
        @Field("queryModel.sortName") sortName: String,
        @Field("queryModel.sortOrder") sortOrder: String,
        @Field("time") time: Int
    ): EvaluationListPage

    @FormUrlEncoded
    @POST("xspjgl/xspj_cxXspjDisplay.html")
    suspend fun getEvaluationDisplay(
        @Query("gnmkdm") gnmkdm: String,
        @Query("su") su: String,
        @Field("jxb_id") jxb_id: String,
        @Field("kch_id") kch_id: String,
        @Field("jgh_id") jgh_id: String,
        @Field("xsdm") xsdm: String,
        @Field("tjzt") tjzt: String,
        @Field("sfcjlrjs") sfcjlrjs: String,
        @Field("pjmbmcb_id") pjmbmcb_id: String
    ): ResponseBody

    @POST("xspjgl/xspj_tjXspj.html")
    suspend fun doEvaluation(
        @Query("gnmkdm") gnmkdm: String,
        @Query("su") su: String,
        @Body payload: FormBody
    ): ResponseBody

    @GET("cdjy/cdjy_cxXqjc.html")
    suspend fun getSectionList(
        @Query("gnmkdm") gnmkdm: String,
        @Query("xqh_id") xqh_id: Int,
        @Query("xnm") xnm: Int,
        @Query("xqm") xqm: Int
    ): SectionList

    @FormUrlEncoded
    @POST("cdjy/cdjy_cxKxcdlb.html")
    suspend fun getRoomList(
        @Query("doType") doType: String,
        @Query("gnmkdm") gnmkdm: String,
        @Field("fwzt") fwzt: String,
        @Field("xqh_id") xqh_id: Int,
        @Field("xnm") xnm: Int,
        @Field("xqm") xqm: Int,
        @Field("zcd") zcd: Long,
        @Field("xqj") xqj: Int,
        @Field("lh") lh: String,
        @Field("jcd") jcd: Long,
        @Field("jyfs") jyfs: Int,
        @Field("_search") _search: Boolean,
        @Field("nd") nd: Long,
        @Field("queryModel.showCount") showCount: Int,
        @Field("queryModel.currentPage") currentPage: Int,
        @Field("queryModel.sortName") sortName: String,
        @Field("queryModel.sortOrder") sortOrder: String,
        @Field("time") time: Int
    ): RoomListPage
}