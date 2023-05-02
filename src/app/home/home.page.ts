import { Component } from '@angular/core';
import { LoadingController, ToastController } from '@ionic/angular';
import Minds from 'src/plugins/minds_plugin'
import { VoiceBiometricsResponse } from 'src/types/voiceBiometrics';
import { Microphone } from '@mozartec/capacitor-microphone';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {

  public showModal = false;
  public biometricsResponse: VoiceBiometricsResponse | null = null;

  constructor(
    private toastController: ToastController,
    private loadingController: LoadingController,
  ) { }

  get responseJson(): string {
    return JSON.stringify(this.biometricsResponse, null, 2);
  }


  async presentLoading() {
    const loading = await this.loadingController.create({
      message: 'Carregando...',
    });
    await loading.present();
    return loading;
  }

  async toogleModal() {
    this.showModal = !this.showModal;
  }

  async handleAuthOrEnrollment(method: 'authentication' | 'enrollment', cpf: string, phoneNumber: string) {
    const loading = await this.presentLoading();
    try {
      const result = await Minds[method]({
        cpf: cpf,
        token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzZWNyZXRfNzBfYXBpIiwiY29tcGFueV9pZCI6NzB9.GWysHY9cAcfhuKPsxVRgRbfUlPWk4EwVOJ923SQU5KI',
        telephone: phoneNumber,
      });
      this.biometricsResponse = result;
      this.toogleModal();
    } catch (e: any) {
      const toast = await this.toastController.create({
        message: e.message,
        duration: 2000,
        color: 'danger',
        position: 'bottom',
      });
      toast.present();
    } finally {
      loading.dismiss();
    }
  }

  async requestPermissions() {
    return await Microphone.requestPermissions();
  }

}
