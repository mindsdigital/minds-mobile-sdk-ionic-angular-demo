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
    val document: String? = call.getString("document")
    val token: String? = call.getString("token")
    val telephone: String? = call.getString("telephone")
    _result = call

    try {
      authenticationMindsSDK =
        MindsConfig.authentication(document!!, token!!, telephone!!)
      CoroutineScope(Dispatchers.Main).launch {
        try {
          val intent = MindsDigital.getIntent(context, authenticationMindsSDK)
          startActivityForResult(call, intent, "result")
        } catch (e: InvalidDocument) {
          _result.reject(e.message, "invalid_document")
        } catch (e: InvalidCPF) {
          _result.reject(e.message, "invalid_cpf")
        } catch (e: InvalidPhoneNumber) {
          _result.reject(e.message, "invalid_phone_number")
        } catch (e: CustomerNotFoundToPerformVerification) {
          _result.reject(e.message, "customer_not_found")
        } catch (e: CustomerNotEnrolled) {
          _result.reject(e.message, "customer_not_enrolled")
        } catch (e: CustomerNotCertified) {
          _result.reject(e.message, "customer_not_certified")
        } catch (e: InvalidToken) {
          _result.reject(e.message, "invalid_token")
        } catch (e: InternalServerException) {
          _result.reject(e.message, "internal_server_error")
        } catch (e: Exception) {
          _result.reject(e.message, "MINDS_SDK_INIT_ERROR")
        }
      }
    } catch (e: Exception) {
      _result.reject(e.message, "MINDS_SDK_INIT_ERROR")
    }
  }

  @PluginMethod
  fun enrollment(call: PluginCall) {
    val document: String? = call.getString("document")
    val token: String? = call.getString("token")
    val telephone: String? = call.getString("telephone")
    _result = call

    try {
      enrollmentMindsSDK =
        MindsConfig.enrollment(document!!, token!!, telephone!!)
      CoroutineScope(Dispatchers.Main).launch {
        try {
          val intent = MindsDigital.getIntent(context, enrollmentMindsSDK)
          startActivityForResult(call, intent, "result")
        } catch (e: InvalidDocument) {
          _result.reject(e.message, "invalid_document")
        } catch (e: InvalidCPF) {
          _result.reject(e.message, "invalid_cpf")
        } catch (e: InvalidPhoneNumber) {
          _result.reject(e.message, "invalid_phone_number")
        } catch (e: CustomerNotFoundToPerformVerification) {
          _result.reject(e.message, "customer_not_found")
        } catch (e: CustomerNotEnrolled) {
          _result.reject(e.message, "customer_not_enrolled")
        } catch (e: CustomerNotCertified) {
          _result.reject(e.message, "customer_not_certified")
        } catch (e: InvalidToken) {
          _result.reject(e.message, "invalid_token")
        } catch (e: InternalServerException) {
          _result.reject(e.message, "internal_server_error")
        } catch (e: Exception) {
          _result.reject(e.message, "MINDS_SDK_INIT_ERROR")
        }
      }
    } catch (e: Exception) {
      _result.reject(e.message, "MINDS_SDK_INIT_ERROR")
    }
  }

  @ActivityCallback
  private fun result(call: PluginCall?, result: ActivityResult) {
    if (call == null) {
      return
    }
    val mindsSDKResponse = result.data?.extras?.get(VOICE_MATCH_RESPONSE) as? VoiceMatchResponse
            val jsonObject = JSONObject()
            jsonObject.put("success", mindsSDKResponse?.success)
            jsonObject.put("error", JSONObject().apply {
                put("code", mindsSDKResponse?.error?.code)
                put("description", mindsSDKResponse?.error?.description)
            })
            jsonObject.put("id", mindsSDKResponse?.id)
            jsonObject.put("cpf", mindsSDKResponse?.cpf)
            jsonObject.put("external_id", mindsSDKResponse?.externalId)
            jsonObject.put("created_at", mindsSDKResponse?.createdAt)
            jsonObject.put("utc_created_at", mindsSDKResponse?.utcCreatedAt)
            jsonObject.put("result", JSONObject().apply {
                put("recommended_action", mindsSDKResponse?.result?.recommendedAction)
                put("reasons", JSONArray(mindsSDKResponse?.result?.reasons))
            })
            jsonObject.put("details", JSONObject().apply {
                jsonObject.put("flag", JSONObject().apply {
                    put("type", mindsSDKResponse?.details?.flag?.type)
                    put("status", mindsSDKResponse?.details?.flag?.status)
                })
                put("liveness", JSONObject().apply {
                    put("status", mindsSDKResponse?.details?.liveness?.status)
                    put("replay_attack", JSONObject().apply {
                        put("enabled", mindsSDKResponse?.details?.liveness?.replayAttack?.enabled)
                        put("status", mindsSDKResponse?.details?.liveness?.replayAttack?.status)
                        put("result", mindsSDKResponse?.details?.liveness?.replayAttack?.result)
                        put("confidence", mindsSDKResponse?.details?.liveness?.replayAttack?.confidence)
                        put("score", mindsSDKResponse?.details?.liveness?.replayAttack?.score)
                        put("threshold", mindsSDKResponse?.details?.liveness?.replayAttack?.threshold)
                    })
                    put("deepfake", JSONObject().apply {
                        put("enabled", mindsSDKResponse?.details?.liveness?.deepFake?.enabled)
                        put("status", mindsSDKResponse?.details?.liveness?.deepFake?.status)
                        put("result", mindsSDKResponse?.details?.liveness?.deepFake?.result)
                        put("confidence", mindsSDKResponse?.details?.liveness?.deepFake?.confidence)
                        put("score", mindsSDKResponse?.details?.liveness?.deepFake?.score)
                        put("threshold", mindsSDKResponse?.details?.liveness?.deepFake?.threshold)
                    })
                    put("sentence_match", JSONObject().apply {
                        put("enabled", mindsSDKResponse?.details?.liveness?.sentenceMatch?.enabled)
                        put("status", mindsSDKResponse?.details?.liveness?.sentenceMatch?.status)
                        put("result", mindsSDKResponse?.details?.liveness?.sentenceMatch?.result)
                        put("confidence", mindsSDKResponse?.details?.liveness?.sentenceMatch?.confidence)
                        put("score", mindsSDKResponse?.details?.liveness?.sentenceMatch?.score)
                        put("threshold", mindsSDKResponse?.details?.liveness?.sentenceMatch?.threshold)
                    })
                })
                put("voice_match", JSONObject().apply {
                    put("status", mindsSDKResponse?.details?.voiceMatch?.status)
                    put("result", mindsSDKResponse?.details?.voiceMatch?.result)
                    put("confidence", mindsSDKResponse?.details?.voiceMatch?.confidence)
                    put("score", mindsSDKResponse?.details?.voiceMatch?.score)
                    put("threshold", mindsSDKResponse?.details?.voiceMatch?.threshold)
                })
            })
    val jsonResult = JSObject.fromJSONObject(jsonObject)
    _result.resolve(jsonResult)
  }
}

