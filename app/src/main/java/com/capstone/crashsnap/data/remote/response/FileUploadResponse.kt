package com.capstone.crashsnap.data.remote.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FileUploadResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class CostPredictItem(

	@field:SerializedName("maxCost")
	val maxCost: String? = null,

	@field:SerializedName("minCost")
	val minCost: String? = null
) : Serializable

data class ResultItem(

	@field:SerializedName("damageDetected")
	val damageDetected: List<String?>? = null,

	@field:SerializedName("costPredict")
	val costPredict: List<CostPredictItem?>? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null
) : Serializable

data class Data(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userID")
	val userID: String? = null
) : Serializable
