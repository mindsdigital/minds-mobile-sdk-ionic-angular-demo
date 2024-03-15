package io.ionic.starter
import digital.minds.clients.sdk.kotlin.domain.helpers.Environment
import digital.minds.clients.sdk.kotlin.domain.helpers.ProcessType
import digital.minds.clients.sdk.kotlin.main.MindsSDK


class MindsConfig {
  companion object {
    fun enrollment(document: String, token: String, telephone: String): MindsSDK {
      return MindsSDK
        .Builder()
        .setToken(token)
        .setDocument(document)
        .setEnvironment(Environment.STAGING)
        .setExternalID(null)
        .setPhoneNumber(telephone)
        .setProcessType(ProcessType.ENROLLMENT)
        .setExternalCustomerId(null)
        .setShowDetails(true)
        .build()
    }

    fun authentication(document: String, token: String, telephone: String): MindsSDK {
      return MindsSDK
        .Builder()
        .setToken(token)
        .setDocument(document)
        .setEnvironment(Environment.STAGING)
        .setExternalID(null)
        .setPhoneNumber(telephone)
        .setProcessType(ProcessType.AUTHENTICATION)
        .setExternalCustomerId(null)
        .setShowDetails(true)
        .build()
    }
  }
}
