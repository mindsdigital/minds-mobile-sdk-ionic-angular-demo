# Pré-requisitos

Antes de iniciar é necessário ter instalado e configurado o Capacitor em seu projeto.

Caso não tenha configurado, siga os passos a seguir: https://ionicframework.com/docs/cli/commands/capacitor-add

# Configuração do Projeto

Para integrar a SDK da Minds no seu projeto iOS, é necessário seguir os seguintes passos:

- Abra o seu projeto no Xcode e selecione o target do seu aplicativo.
- Clique na aba "Swift Packages" na parte superior da janela.
- Clique no botão "+" no canto inferior esquerdo da janela para adicionar um pacote.
- Na caixa de diálogo que aparece, cole a URL do repositório da SDK da Minds: https://github.com/mindsdigital/minds-sdk-mobile-ios.git
- Certifique-se de que o destino esteja selecionado corretamente e clique em "Finish".
- Aguarde a importação da SDK ser concluída.

# AppDelegate

Este trecho de código inicializa o Capacitor e configura o controlador de visualização raiz para a aplicação. Ele faz isso chamando a função `application(_:didFinishLaunchingWithOptions:)` do protocolo `UIApplicationDelegate`.

Primeiro, a função obtém a referência para o controlador de visualização raiz do aplicativo através da propriedade `rootViewController` da janela do aplicativo. Em seguida, cria uma instância de `UINavigationController` com o controlador de visualização raiz como seu primeiro controlador de visualização e esconde a barra de navegação. Por fim, define a instância do `UINavigationController` como a nova controladora raiz da janela do aplicativo.

Adicione o código abaixo na implementação do método application:

```swift
import UIKit
import Capacitor

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    var navigationController: UINavigationController?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let controller = window?.rootViewController as? CAPBridgeViewController
        navigationController = UINavigationController(rootViewController: controller!)
        navigationController?.navigationBar.isHidden = true
        window?.rootViewController = navigationController
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
        // Called when the app was launched with a url. Feel free to add additional processing here,
        // but if you want the App API to support tracking app url opens, make sure to keep this call
        return ApplicationDelegateProxy.shared.application(app, open: url, options: options)
    }

    func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        // Called when the app was launched with an activity, including Universal Links.
        // Feel free to add additional processing here, but if you want the App API to support
        // tracking app url opens, make sure to keep this call
        return ApplicationDelegateProxy.shared.application(application, continue: userActivity, restorationHandler: restorationHandler)
    }

}
```

# Criação do plugin em swift

- Crie o arquivo swift com o nome de "MindsPlugin.swift"

Adicione o código abaixo:

```swift
import Foundation
import Capacitor
import MindsSDK
import AVFAudio

@objc(MindsPlugin)
public class MindsPlugin: CAPPlugin, MindsSDKDelegate {
    // ...
}
```

A classe MindsPlugin é a classe principal do plugin. Ela implementa a interface MindsSDKDelegate, que é usada para receber o resultado que vem da SDK da Minds Digital.

O plugin oferece dois métodos principais:

## Autenticação

Este método inicia o processo de autenticação. Recebe como parâmetros obrigatórios o CPF e o token de autenticação.

```swift
@objc func authentication(_ call: CAPPluginCall) {
    let cpf = call.getString("cpf") ?? ""
    let token = call.getString("token") ?? ""
    let telephone = call.getString("telephone") ?? ""
    
    biometricsCall = call
    self.startSDK(processType: .authentication, cpf: cpf, token: token, telephone: telephone, externalId: nil, externalCustomerId: nil)
}
```

## Enrollment

Este método inicia o processo de Cadastro de voz. Recebe como parâmetros obrigatórios o CPF e o token de autenticação.

```swift
@objc func enrollment(_ call: CAPPluginCall) {
    let cpf = call.getString("cpf") ?? ""
    let token = call.getString("token") ?? ""
    let telephone = call.getString("telephone") ?? ""
    
    biometricsCall = call
    self.startSDK(processType: .enrollment, cpf: cpf, token: token, telephone: telephone, externalId: nil, externalCustomerId: nil)
}
```

Ambos os métodos retornam um JSON com informações sobre o resultado da SDK da Minds Digital.

## Inicialização da SDK

Para inicializar a SDK, você pode chamar a função startSDK e passar os seguintes parâmetros:

- processType: o tipo de processo a ser executado, que pode ser enrollment ou authentication.
- cpf: CPF do cliente
- token: o token de autenticação.
- telephone: Número de telefone do cliente
- externalId: ID externo para posterior identificação do áudio.
- externalCustomerId: ID externo para identificação do cliente que está enviando o áudio

Abaixo está um exemplo de como chamar a função startSDK:


```swift
    private func startSDK(processType: MindsSDK.ProcessType, cpf: String, token: String, telephone: String, externalId: String?, externalCustomerId: String?) {
        sdk = MindsSDK(delegate: self)
        sdk?.setToken(token)
        sdk?.setExternalId(externalId)
        sdk?.setExternalCustomerId(externalCustomerId)
        sdk?.setPhoneNumber(telephone)
        sdk?.setShowDetails(true)
        sdk?.setCpf(cpf)
        sdk?.setProcessType(processType)
        sdk?.setEnvironment(.sandbox)
        
        DispatchQueue.main.async {
            
            guard let navigationController: UINavigationController = self.bridge?.viewController?.navigationController else { return }
            
        
            self.sdk?.initialize(on: navigationController) { error in
                if let error = error {
                    do {
                        throw error
                    } catch DomainError.invalidCPF(let message) {
                        self.biometricsCall?.reject(message!, "invalid_cpf")
                        
                    } catch DomainError.invalidPhoneNumber(let message) {
                        self.biometricsCall?.reject(message!, "invalid_phone_number")
                        
                    } catch DomainError.customerNotFoundToPerformVerification(let message) {
                        self.biometricsCall?.reject(message!, "customer_not_found")
                        
                    } catch DomainError.customerNotEnrolled(let message) {
                        self.biometricsCall?.reject(message!, "customer_not_enrolled")
                        
                    } catch DomainError.customerNotCertified(let message) {
                        self.biometricsCall?.reject(message!, "customer_not_certified")
                        
                    } catch DomainError.invalidToken {
                        self.biometricsCall?.reject("Invalid Token", "invalid_token")
                        
                    } catch DomainError.undefinedEnvironment {
                        self.biometricsCall?.reject("No environment defined", "undefined_environment")
                        
                    } catch DomainError.internalServerException {
                        self.biometricsCall?.reject("Internal server error", "internal_server_error")
                        
                    } catch {
                        print("\(error): \(error.localizedDescription)")
                        self.biometricsCall?.reject("ERROR", error.localizedDescription)
                    }
                }
            }
        }
    }
```

## Implementação do protocolo MindsSDKDelegate

O MindsSDKDelegate é responsável por receber as respostas da SDK. A classe MindsPlugin implementa este delegate e possui duas funções que podem ser chamadas quando a autenticação ou cadastro de voz, ou quando ocorre um erro:

- `onSuccess`: chamado quando a autenticação ou a inscrição são concluídas com sucesso.
- `onError`: chamado quando ocorre um erro durante a autenticação ou a inscrição.

```swift
public func onSuccess(_ response: BiometricResponse) {
    self.biometricsReceive(response)
}

public func onError(_ response: BiometricResponse) {
    self.biometricsReceive(response)
}

public func showMicrophonePermissionPrompt() {
    AVAudioSession.sharedInstance().requestRecordPermission { granted in
        print("granted: \(granted)")
    }
}

public func microphonePermissionNotGranted() {
    print("microphonePermissionNotGranted")
}
```

Além disso, a classe `MindsPlugin` também implementa as seguintes funções do delegate:

`showMicrophonePermissionPrompt`: chamado quando é necessário solicitar permissão para usar o microfone do dispositivo.
`microphonePermissionNotGranted`: chamado quando a permissão para usar o microfone do dispositivo não foi concedida.

Implemente a função biometricsReceive que é utilizada no delegate:

```swift
    private func biometricsReceive(_ response: BiometricResponse) {
        self.biometricsCall?.resolve([
            "success": response.success,
            "error": [
                "code": response.error?.code,
                "description": response.error?.description
            ],
            "id": response.id,
            "cpf": response.cpf,
            "external_id": response.externalID,
            "created_at": response.createdAt,
            "result": [
                "recommended_action": response.result?.recommendedAction as Any,
                "reasons": response.result?.reasons as Any
            ],
            "details": [
                "flag": [
                    "id": response.details?.flag?.id as Any ,
                    "type": response.details?.flag?.type as Any,
                    "description": response.details?.flag?.description as Any,
                    "status": response.details?.flag?.status as Any
                ],
                "voice_match": [
                    "result": response.details?.voiceMatch?.result as Any,
                    "confidence": response.details?.voiceMatch?.confidence as Any,
                    "status": response.details?.voiceMatch?.status as Any
                ]
            ]
        ])
    }
```

# Código completo:

```swift
import Foundation
import Capacitor
import MindsSDK
import AVFAudio

@objc(MindsPlugin)
public class MindsPlugin: CAPPlugin, MindsSDKDelegate {
    
    public func onSuccess(_ response: BiometricResponse) {
        self.biometricsReceive(response)
    }
    
    public func onError(_ response: BiometricResponse) {
        self.biometricsReceive(response)
    }
    
    public func showMicrophonePermissionPrompt() {
        AVAudioSession.sharedInstance().requestRecordPermission { granted in
            print("granted: \(granted)")
        }
    }
    
    public func microphonePermissionNotGranted() {
        print("microphonePermissionNotGranted")
    }
    
    
    var sdk: MindsSDK?
    var navigationController: UINavigationController?
    var biometricsCall: CAPPluginCall?
    
    
    @objc func authentication(_ call: CAPPluginCall) {
        let cpf = call.getString("cpf") ?? ""
        let token = call.getString("token") ?? ""
        let telephone = call.getString("telephone") ?? ""
        
        biometricsCall = call
        self.startSDK(processType: .authentication, cpf: cpf, token: token, telephone: telephone, externalId: nil, externalCustomerId: nil)
    }
    
    @objc func enrollment(_ call: CAPPluginCall) {
        let cpf = call.getString("cpf") ?? ""
        let token = call.getString("token") ?? ""
        let telephone = call.getString("telephone") ?? ""
        
        biometricsCall = call
        self.startSDK(processType: .enrollment, cpf: cpf, token: token, telephone: telephone, externalId: nil, externalCustomerId: nil)
    }
    
    
    private func startSDK(processType: MindsSDK.ProcessType, cpf: String, token: String, telephone: String, externalId: String?, externalCustomerId: String?) {
        sdk = MindsSDK(delegate: self)
        sdk?.setToken(token)
        sdk?.setExternalId(externalId)
        sdk?.setExternalCustomerId(externalCustomerId)
        sdk?.setPhoneNumber(telephone)
        sdk?.setShowDetails(true)
        sdk?.setCpf(cpf)
        sdk?.setProcessType(processType)
        sdk?.setEnvironment(.sandbox)
        
        DispatchQueue.main.async {
            
            guard let navigationController: UINavigationController = self.bridge?.viewController?.navigationController else { return }
            
        
            self.sdk?.initialize(on: navigationController) { error in
                if let error = error {
                    do {
                        throw error
                    } catch DomainError.invalidCPF(let message) {
                        self.biometricsCall?.reject(message!, "invalid_cpf")
                        
                    } catch DomainError.invalidPhoneNumber(let message) {
                        self.biometricsCall?.reject(message!, "invalid_phone_number")
                        
                    } catch DomainError.customerNotFoundToPerformVerification(let message) {
                        self.biometricsCall?.reject(message!, "customer_not_found")
                        
                    } catch DomainError.customerNotEnrolled(let message) {
                        self.biometricsCall?.reject(message!, "customer_not_enrolled")
                        
                    } catch DomainError.customerNotCertified(let message) {
                        self.biometricsCall?.reject(message!, "customer_not_certified")
                        
                    } catch DomainError.invalidToken {
                        self.biometricsCall?.reject("Invalid Token", "invalid_token")
                        
                    } catch DomainError.undefinedEnvironment {
                        self.biometricsCall?.reject("No environment defined", "undefined_environment")
                        
                    } catch DomainError.internalServerException {
                        self.biometricsCall?.reject("Internal server error", "internal_server_error")
                        
                    } catch {
                        print("\(error): \(error.localizedDescription)")
                        self.biometricsCall?.reject("ERROR", error.localizedDescription)
                    }
                }
            }
        }
    }
    
    
    private func biometricsReceive(_ response: BiometricResponse) {
        self.biometricsCall?.resolve([
            "success": response.success,
            "error": [
                "code": response.error?.code,
                "description": response.error?.description
            ],
            "id": response.id,
            "cpf": response.cpf,
            "external_id": response.externalID,
            "created_at": response.createdAt,
            "result": [
                "recommended_action": response.result?.recommendedAction as Any,
                "reasons": response.result?.reasons as Any
            ],
            "details": [
                "flag": [
                    "id": response.details?.flag?.id as Any ,
                    "type": response.details?.flag?.type as Any,
                    "description": response.details?.flag?.description as Any,
                    "status": response.details?.flag?.status as Any
                ],
                "voice_match": [
                    "result": response.details?.voiceMatch?.result as Any,
                    "confidence": response.details?.voiceMatch?.confidence as Any,
                    "status": response.details?.voiceMatch?.status as Any
                ]
            ]
        ])
    }
}
```

# Registro do Plugin

Para finalizar a configuração do plugin, é necessário registrar o mesmo na aplicação. Para isso, deve-se criar o arquivo "MindsPlugin.m" e adicionar o seguinte código:

```objective-c
#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

CAP_PLUGIN(MindsPlugin, "Minds",
    CAP_PLUGIN_METHOD(authentication, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(enrollment, CAPPluginReturnPromise);
)
```
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
        code: number;
        description: string;
    };
    id: string;
    cpf: string;
    external_id: string;
    created_at: string;
    result: {
        recommended_action: string;
        reasons: string[];
    };
    details: {
        flag: {
            id: string;
            type: string;
            description: string;
            status: string;
        };
        voice_match: {
            result: string;
            confidence: number;
            status: string;
        };
    };
}
```  
</details>

```typescript
import { registerPlugin } from '@capacitor/core';
import { VoiceBiometricsResponse } from 'src/types/voiceBiometrics';

export interface MindsPlugin {
    authentication(options: { cpf: string, token: string, telephone: string }): Promise<VoiceBiometricsResponse>;
    enrollment(options: { cpf: string, token: string, telephone: string }): Promise<VoiceBiometricsResponse>;
}

const Minds = registerPlugin<MindsPlugin>('Minds');
export default Minds;
```

## 📌 Observação

É importante ressaltar que o integrador deve garantir que a permissão do microfone seja fornecida em seu aplicativo antes de utilizar a SDK. Sem essa permissão, a SDK não funcionará corretamente. É responsabilidade do integrador garantir que seu aplicativo tenha as permissões necessárias para utilizar a SDK com sucesso.

# Referências

Para mais informações sobre o Capacitor acessar documentação oficieal em: https://capacitorjs.com/docs/plugins/ios




