//
//  MindsPlugin.swift
//  App
//
//  Created by Wennys on 26/04/23.
//

import Foundation
import Capacitor
import MindsSDK
import AVFAudio

@objc(MindsPlugin)
public class MindsPlugin: CAPPlugin, MindsSDKDelegate {
    
    public func onSuccess(_ response: BiometricResponse?) {
        self.biometricsReceive(response)
    }
    
    public func onError(_ response: BiometricResponse?) {
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
        let document = call.getString("document") ?? ""
        let token = call.getString("token") ?? ""
        let telephone = call.getString("telephone") ?? ""
        
        biometricsCall = call
        self.startSDK(processType: .authentication, document: document, token: token, telephone: telephone, externalId: nil, externalCustomerId: nil)
    }
    
    @objc func enrollment(_ call: CAPPluginCall) {
        let document = call.getString("document") ?? ""
        let token = call.getString("token") ?? ""
        let telephone = call.getString("telephone") ?? ""
        
        biometricsCall = call
        self.startSDK(processType: .enrollment, document: document, token: token, telephone: telephone, externalId: nil, externalCustomerId: nil)
    }
    
    
    private func startSDK(processType: MindsSDK.ProcessType, document: String, token: String, telephone: String, externalId: String?, externalCustomerId: String?) {
        sdk = MindsSDK(delegate: self)
        sdk?.setToken(token)
        sdk?.setExternalId(externalId)
        sdk?.setExternalCustomerId(externalCustomerId)
        sdk?.setPhoneNumber(telephone)
        sdk?.setShowDetails(true)
        sdk?.setDocument(document)
        sdk?.setProcessType(processType)
        sdk?.setEnvironment(.staging)
        
        DispatchQueue.main.async {
            
            guard let navigationController: UINavigationController = self.bridge?.viewController?.navigationController else { return }
            
            
            self.sdk?.initialize(on: navigationController) { error in
                if let error = error {
                    do {
                        throw error
                    } catch DomainError.invalidDocument(let message) {
                        self.biometricsCall?.reject(message!, "invalid_document")
                        
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
    
    
    private func biometricsReceive(_ response: BiometricResponse?) {
        self.biometricsCall?.resolve([
            "success": response?.success as Any,
            "error": [
                "code": response?.error?.code as Any,
                "description": response?.error?.description as Any
            ],
            "id": response?.id as Any,
            "cpf": response?.cpf as Any,
            "external_id": response?.externalID as Any,
            "created_at": response?.createdAt as Any,
            "utc_created_at": response?.utcCreatedAt as Any,
            "result": [
                "recommended_action": response?.result?.recommendedAction as Any,
                "reasons": response?.result?.reasons as Any
            ],
            "details": [
                "flag": [
                    "type": response?.details?.flag?.type as Any,
                    "status": response?.details?.flag?.status as Any
                ],
                "liveness": [
                    "status": response?.details?.liveness?.status as Any,
                    "replay_attack": [
                        "enabled": response?.details?.liveness?.replayAttack?.enabled as Any,
                        "status": response?.details?.liveness?.replayAttack?.status as Any,
                        "result": response?.details?.liveness?.replayAttack?.result as Any,
                        "confidence": response?.details?.liveness?.replayAttack?.confidence as Any,
                        "score": response?.details?.liveness?.replayAttack?.score as Any,
                        "threshold": response?.details?.liveness?.replayAttack?.threshold as Any
                    ],
                    "deepfake": [
                        "enabled": response?.details?.liveness?.deepFake?.enabled as Any,
                        "status": response?.details?.liveness?.deepFake?.status as Any,
                        "result": response?.details?.liveness?.deepFake?.result as Any,
                        "confidence": response?.details?.liveness?.deepFake?.confidence as Any,
                        "score": response?.details?.liveness?.deepFake?.score as Any,
                        "threshold": response?.details?.liveness?.deepFake?.threshold as Any
                    ],
                    "sentence_match": [
                        "enabled": response?.details?.liveness?.sentenceMatch?.enabled as Any,
                        "status": response?.details?.liveness?.sentenceMatch?.status as Any,
                        "result": response?.details?.liveness?.sentenceMatch?.result as Any,
                        "confidence": response?.details?.liveness?.sentenceMatch?.confidence as Any,
                        "score": response?.details?.liveness?.sentenceMatch?.score as Any,
                        "threshold": response?.details?.liveness?.sentenceMatch?.threshold as Any
                    ]
                ],
                "voice_match": [
                    "status": response?.details?.voiceMatch?.status as Any,
                    "result": response?.details?.voiceMatch?.result as Any,
                    "confidence": response?.details?.voiceMatch?.confidence as Any,
                    "score": response?.details?.voiceMatch?.score as Any,
                    "threshold": response?.details?.voiceMatch?.threshold as Any
                ]
            ]
            
        ])
    }
}
