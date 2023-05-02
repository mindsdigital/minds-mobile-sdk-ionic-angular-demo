package io.ionic.starter

import androidx.activity.result.ActivityResult
import com.getcapacitor.*
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import digital.minds.clients.sdk.android.MindsDigital
import digital.minds.clients.sdk.kotlin.data.model.VoiceMatchResponse
import digital.minds.clients.sdk.kotlin.domain.constants.VOICE_MATCH_RESPONSE
import digital.minds.clients.sdk.kotlin.domain.exceptions.*
import digital.minds.clients.sdk.kotlin.main.MindsSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@CapacitorPlugin(name = "Minds")
class MindsPlugin : Plugin() {

  private lateinit var authenticationMindsSDK: MindsSDK
  private lateinit var enrollmentMindsSDK: MindsSDK
  private lateinit var _result: PluginCall


  @PluginMethod
  fun authentication(call: PluginCall) {
    val cpf: String? = call.getString("cpf")
    val token: String? = call.getString("token")
    val telephone: String? = call.getString("telephone")
    _result = call

    try {
      authenticationMindsSDK =
        MindsConfig.authentication(cpf!!, token!!, telephone!!)
      CoroutineScope(Dispatchers.Main).launch {
        try {
          val intent = MindsDigital.getIntent(context, authenticationMindsSDK)
          startActivityForResult(call, intent, "result")
        } catch (e: InvalidCPF) {
          _result.reject("invalid_cpf", e.message)
        } catch (e: InvalidPhoneNumber) {
          _result.reject("invalid_phone_number", e.message)
        } catch (e: CustomerNotFoundToPerformVerification) {
          _result.reject("customer_not_found", e.message)
        } catch (e: CustomerNotEnrolled) {
          _result.reject("customer_not_enrolled", e.message)
        } catch (e: CustomerNotCertified) {
          _result.reject("customer_not_certified", e.message)
        } catch (e: InvalidToken) {
          _result.reject("invalid_token", e.message)
        } catch (e: InternalServerException) {
          _result.reject("internal_server_error", e.message)
        } catch (e: Exception) {
          _result.reject("MINDS_SDK_INIT_ERROR", e.message)
        }
      }
    } catch (e: Exception) {
      _result.reject("MINDS_SDK_INIT_ERROR", e.message)
    }
  }

  @PluginMethod
  fun enrollment(call: PluginCall) {
    val cpf: String? = call.getString("cpf")
    val token: String? = call.getString("token")
    val telephone: String? = call.getString("telephone")
    _result = call

    try {
      enrollmentMindsSDK =
        MindsConfig.enrollment(cpf!!, token!!, telephone!!)
      CoroutineScope(Dispatchers.Main).launch {
        try {
          val intent = MindsDigital.getIntent(context, enrollmentMindsSDK)
          startActivityForResult(call, intent, "result")
        } catch (e: InvalidCPF) {
          _result.reject("invalid_cpf", e.message)
        } catch (e: InvalidPhoneNumber) {
          _result.reject("invalid_phone_number", e.message)
        } catch (e: CustomerNotFoundToPerformVerification) {
          _result.reject("customer_not_found", e.message)
        } catch (e: CustomerNotEnrolled) {
          _result.reject("customer_not_enrolled", e.message)
        } catch (e: CustomerNotCertified) {
          _result.reject("customer_not_certified", e.message)
        } catch (e: InvalidToken) {
          _result.reject("invalid_token", e.message)
        } catch (e: InternalServerException) {
          _result.reject("internal_server_error", e.message)
        } catch (e: Exception) {
          _result.reject("MINDS_SDK_INIT_ERROR", e.message)
        }
      }
    } catch (e: Exception) {
      _result.reject("MINDS_SDK_INIT_ERROR", e.message)
    }
  }

  @ActivityCallback
  private fun result(call: PluginCall?, result: ActivityResult) {
    if (call == null) {
      return
    }
    val mindsSDKResponse = result.data?.extras?.get(VOICE_MATCH_RESPONSE) as? VoiceMatchResponse
    val jsonObject = JSONObject().apply {
      put("success", mindsSDKResponse?.success)
      put("error", JSONObject().apply {
        put("code", mindsSDKResponse?.error?.code)
        put("description", mindsSDKResponse?.error?.description)
      })
      put("id", mindsSDKResponse?.id)
      put("cpf", mindsSDKResponse?.cpf)
      put("external_id", mindsSDKResponse?.external_id)
      put("created_at", mindsSDKResponse?.created_at)
      put("result", JSONObject().apply {
        put("recommended_action", mindsSDKResponse?.result?.recommended_action)
        put("reasons", JSONArray(mindsSDKResponse?.result?.reasons))
      })
      put("details", JSONObject().apply {
        put("flag", JSONObject().apply {
          put("id", mindsSDKResponse?.details?.flag?.id)
          put("type", mindsSDKResponse?.details?.flag?.type)
          put("description", mindsSDKResponse?.details?.flag?.description)
          put("status", mindsSDKResponse?.details?.flag?.status)
        })
        put("voice_match", JSONObject().apply {
          put("result", mindsSDKResponse?.details?.voice_match?.result)
          put("confidence", mindsSDKResponse?.details?.voice_match?.confidence)
          put("status", mindsSDKResponse?.details?.voice_match?.status)
        })
        put("antispoofing", JSONObject().apply {
          put("result", mindsSDKResponse?.details?.antispoofing?.result)
          put("status", mindsSDKResponse?.details?.antispoofing?.status)
        })
      })
    }
    val jsonResult = JSObject.fromJSONObject(jsonObject)
    _result.resolve(jsonResult)
  }
}

