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