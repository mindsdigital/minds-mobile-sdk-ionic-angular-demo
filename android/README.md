# Dicas
Para obter todas as informações da SDK, acesse o link oficial: https://api.minds.digital/docs/sdk/visao_geral

# Pré-requisitos

Antes de iniciar é necessário ter instalado e configurado o Capacitor em seu projeto.

Caso não tenha configurado, siga os passos a seguir: https://ionicframework.com/docs/cli/commands/capacitor-add

# Configuração do Projeto

Para adicionar as dependências necessárias e configurar a compilação do projeto, é preciso adicionar código ao arquivo build.gradle. O código abaixo pode ser adicionado à seção buildscript no arquivo `build.gradle` da pasta `/android`:

```gradle
buildscript {
  ext.kotlin_version = '1.7.10'
  ext.ktxCoreVersion = '1.2.0'  
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        def nav_version = "2.5.0"
        classpath "androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version"
        classpath 'com.android.tools.build:gradle:7.2.1'
        classpath 'com.google.gms:google-services:4.3.13'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"

        // NOTA: Não coloque as dependências do aplicativo aqui, elas pertencem
        // aos arquivos de build.gradle de módulos individuais.
    }
}
```

# Adicionando o plugin do Google Artifact Registry:

## Essa configuração deve ser feita no arquivo `build.gradle` da pasta `android/app/`

Para adicionar o plugin Gradle que permite a integração do projeto com o Google Artifact Registry, inclua as seguinte linha no arquivo `build.gradle`:

```gradle
plugins {
    id "com.google.cloud.artifactregistry.gradle-plugin" version "2.1.5"
    id 'kotlin-android'
}
```
Certifique-se de especificar a versão mais recente do plugin compatível com o seu projeto.

Essa etapa é importante para que o Gradle possa buscar e baixar as dependências necessárias para o projeto no Google Artifact Registry.

# Adicionando um repositório Maven

Para adicionar um repositório Maven no Gradle, inclua a seguinte linha no arquivo `build.gradle`:

```gradle
repositories {
    google()
    mavenCentral()
    maven {
        url "artifactregistry://us-east1-maven.pkg.dev/minds-digital-238513/mobile-sdk-android"
    }
}
```

Certifique-se de especificar a URL correta do repositório Maven que você deseja adicionar.

Essa etapa é importante para que o Gradle possa buscar e baixar as dependências necessárias para o projeto no repositório Maven especificado.

# Habilitando as funcionalidades de binding:

Para habilitar as funcionalidades de binding de views e binding de dados do Android no projeto, inclua as seguintes linhas no arquivo `build.gradle`:

```gradle
android {
    buildFeatures {
        viewBinding true
        dataBinding true
    }
}
```

# Adicionando dependências:

Para adicionar as dependências necessárias para o projeto, inclua as seguintes linhas no arquivo `build.gradle`:

```gradle
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'digital.minds.clients.sdk.android:release:1.18.1'
    implementation 'digital.minds.clients.sdk.kotlin.core:release:1.0.22'
}
```

## Aplicando o plugin:

Para aplicar o plugin do Google Artifact Registry ao projeto, inclua a seguinte linha no arquivo `build.gradle`:


```gradle
apply plugin: 'com.google.cloud.artifactregistry.gradle-plugin'
```
Essa etapa é importante para que o Gradle possa buscar e baixar as dependências necessárias para o projeto no Google Artifact Registry.


# Configuração da Classe Minds Config
- Criar um arquivo Kotlin para definir os valores que serão passados para a SDK, chamado de "`MindsConfig.kt`".

A classe MindsConfig é responsável por configurar a SDK para a operação de autenticação ou cadastro de biometria, dependendo do método escolhido.

Os métodos `enrollment` e `authentication` criam um objeto `MindsSDK` com as configurações necessárias para cada operação, incluindo o Documento do cliente (cpf, ssn, etc), o token de acesso, o telefone do usuário e outras informações relevantes.

Veja todos os métodos disponíveis aqui: https://api.minds.digital/docs/sdk/android/referencias#mindssdk

```kotlin
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
                .setEnvironment(Environment.SANDBOX)
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
                .setEnvironment(Environment.SANDBOX)
                .setExternalID(null)
                .setPhoneNumber(telephone)
                .setProcessType(ProcessType.AUTHENTICATION)
                .setExternalCustomerId(null)
                .setShowDetails(true)
                .build()
        }
    }
}
```

O objeto `MindsSDK` retornado por esses métodos é passado para a SDK através do método `MindsDigital.getIntent()`, que é responsável por iniciar a operação na SDK

# Criação do plugin - Capacitor

1. Criar um arquivo Kotlin para o plugin, com o nome desejado, neste exemplo, "`MindsPlugin.kt`".
2. Adicionar a anotação `@CapacitorPlugin(name = "Minds")` para informar o nome do plugin.
3. Estender a classe `Plugin()`, que é fornecida pela biblioteca `Capacitor`.
4. Implementar os métodos desejados para o plugin. Neste exemplo, serão implementados os métodos `authentication` e `enrollment`.
5. Adicionar a anotação `@ActivityCallback` para definir o método que será chamado quando a atividade for encerrada.

```kotlin
@CapacitorPlugin(name = "Minds")
class MindsPlugin : Plugin() {

    fun authentication(call: PluginCall) {
        // Implementação do método authentication
    }

    fun enrollment(call: PluginCall) {
        // Implementação do método enrollment
    }

    @ActivityCallback
    private fun result(call: PluginCall?, result: ActivityResult) {
        // Implementação do método que será chamado quando a atividade for encerrada
    }
}
```

Este plugin foi implementado em `Kotlin` e possui dois métodos, "`authentication`" e "`enrollment`", além do método "`result`" que é utilizado para retornar o resultado da atividade que vem da SDK.

Veja o código completo: 

```kotlin
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
```





# Registro do Plugin

Para finalizar a configuração do plugin, é necessário registrar o mesmo na aplicação. Para isso, deve-se criar o arquivo "`MainActivity.java`" e adicionar o seguinte código:

```kotlin
package io.ionic.starter
import android.os.Bundle
import com.getcapacitor.BridgeActivity

class MainActivity : BridgeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPlugin(MindsPlugin::class.java)
    }
}
```

Neste exemplo, o arquivo `MainActivity.kt` herda de `BridgeActivity()`, que é fornecido pela biblioteca Capacitor. O método `registerPlugin` é utilizado para registrar o plugin criado anteriormente, neste caso, o `MindsPlugin`.

Após realizar este registro, o plugin estará pronto para ser utilizado em sua aplicação.


# Utilização do plugin

O plugin exporta uma interface chamada `MindsPlugin` com dois métodos, `authentication` e `enrollment`, ambos recebendo um objeto `options` com as informações necessárias para autenticação ou cadastro de voz. O método authentication retorna um objeto Promise com o resultado.

Para facilitar crie uma interface para receber o resultado do plugin:

<details>
<summary>VoiceBiometricsResponse</summary>
  
```typescript
export interface VoiceBiometricsResponse {
    success: boolean;
    error: {
        code: string;
        description: string;
    } | null;
    id: number;
    cpf: string;
    external_id: string;
    created_at: string;
    utc_created_at: string;
    result: {
        recommended_action: string;
        reasons: string[];
    };
    details: {
        flag: {
            type: string;
            status: string;
        } | null;
        liveness: {
            status: string;
            replay_attack: {
                enabled: boolean;
                status: string;
                result: string;
                confidence: string;
                score: number;
                threshold: number;
            };
            deepfake: {
                enabled: boolean;
                status: string;
                result: string;
                confidence: string;
                score: number;
                threshold: number;
            };
            sentence_match: {
                enabled: boolean;
                status: string;
                result: string;
                confidence: string;
                score: number;
                threshold: number;
            };
        };
        voice_match: {
            result: string;
            confidence: string;
            status: string;
            score: number;
            threshold: number;
        };
    };
}
```  
</details>

```typescript
import { registerPlugin } from '@capacitor/core';
import { VoiceBiometricsResponse } from 'src/types/voiceBiometrics';

export interface MindsPlugin {
    authentication(options: { document: string, token: string, telephone: string }): Promise<VoiceBiometricsResponse>;
    enrollment(options: { document: string, token: string, telephone: string }): Promise<VoiceBiometricsResponse>;
}

const Minds = registerPlugin<MindsPlugin>('Minds');
export default Minds;
```

## 📌 Observação

É importante ressaltar que o integrador deve garantir que a permissão do microfone seja fornecida em seu aplicativo antes de utilizar a SDK. Sem essa permissão, a SDK não funcionará corretamente. É responsabilidade do integrador garantir que seu aplicativo tenha as permissões necessárias para utilizar a SDK com sucesso.


## 🛠️ Corrigir possíveis problemas 

Em caso de erro "What went wrong: Execution failed for task `:app:processDebugMainManifest` , você deve adicionar a tag "tools:replace" com o valor "android:label,theme" dentro da tag <application> no arquivo AndroidManifest.xml do seu projeto. 

Esse erro ocorre porque há um conflito no arquivo AndroidManifest.xml, especificamente com a tag `application`. O atributo application@label está presente tanto no arquivo AndroidManifest.xml do projeto quanto no arquivo AndroidManifest.xml da SDK da Minds Digital.

O código deve ficar assim:

```dart
<application
    tools:replace="android:label,theme"
</application>
```

Dessa forma, o atributo label do arquivo da SDK será substituído pelo atributo label do seu projeto. Essa configuração permitirá que o erro seja resolvido.

# Referências

Para mais informações sobre o Capacitor acessar documentação oficieal em: https://capacitorjs.com/docs/plugins/android

  
  





