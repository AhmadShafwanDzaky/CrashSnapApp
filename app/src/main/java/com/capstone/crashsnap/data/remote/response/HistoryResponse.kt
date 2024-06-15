package com.capstone.crashsnap.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class CostPredictItem(

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("maxCost")
	val maxCost: String,

	@field:SerializedName("minCost")
	val minCost: String
)

data class ResultItem(

	@field:SerializedName("damageDetected")
	val damageDetected: List<String>,

	@field:SerializedName("costPredict")
	val costPredict: List<CostPredictItem>,

	@field:SerializedName("imageUrl")
	val imageUrl: String
)

data class DataItem(

	@field:SerializedName("result")
	val result: List<ResultItem>,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("userID")
	val userID: String
)
