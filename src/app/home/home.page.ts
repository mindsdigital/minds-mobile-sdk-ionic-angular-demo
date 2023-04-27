import { Component } from '@angular/core';
import Minds from 'src/plugins/minds_plugin'
import { Microphone } from '@mozartec/capacitor-microphone';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { VoiceBiometricsResponse } from 'src/types/voiceBiometrics';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {
  constructor() { }

  public showModal: boolean = false
  public isLoading: boolean = false
  public biometricsResponse: VoiceBiometricsResponse | null = null;


  get responseJson() {
    return JSON.stringify(this.biometricsResponse, null, 2);
  }

  toogleModal() {
    this.showModal = !this.showModal
  }

  async authenticate(cpf: string, phoneNumber: string) {
    try {
      this.isLoading = true
      const result = await Minds.authentication({
        cpf: cpf,
        token: '',
        telephone: phoneNumber,
      })
      this.biometricsResponse = result
      this.isLoading = false
      this.toogleModal()
    } catch (e: any) {
      console.log(e.message);
      this.isLoading = false
    }
  }

  async enrollment(cpf: string, phoneNumber: string) {
    try {
      this.isLoading = true
      const result = await Minds.enrollment({
        cpf: cpf,
        token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzZWNyZXRfNzBfYXBpIiwiY29tcGFueV9pZCI6NzB9.GWysHY9cAcfhuKPsxVRgRbfUlPWk4EwVOJ923SQU5KI',
        telephone: phoneNumber,
      })
      this.biometricsResponse = result
      this.isLoading = false
      this.toogleModal()
    } catch (e: any) {
      console.log(e.message);
      this.isLoading = false
    }
  }



  async requestPermissions() {
    return await Microphone.requestPermissions();
  }

}

