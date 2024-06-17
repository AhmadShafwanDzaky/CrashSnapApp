package com.capstone.crashsnap.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryDetailResponse(

	@field:SerializedName("data")
	val data: DataDetail,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class ResultItemDetail(

	@field:SerializedName("damageDetected")
	val damageDetected: List<String>,

	@field:SerializedName("costPredict")
	val costPredict: List<CostPredictItemDetail>,

	@field:SerializedName("imageUrl")
	val imageUrl: String
)

data class CostPredictItemDetail(

	@field:SerializedName("maxCost")
	val maxCost: String,

	@field:SerializedName("minCost")
	val minCost: String
)

data class DataDetail(

	@field:SerializedName("result")
	val result: List<ResultItemDetail>,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("userID")
	val userID: String
)
