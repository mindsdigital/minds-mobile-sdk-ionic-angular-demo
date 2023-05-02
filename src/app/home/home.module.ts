import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { HomePage } from './home.page';

import { HomePageRoutingModule } from './home-routing.module';
import { CpfMaskDirective } from 'src/utils/cpf.directive';
import { PhoneMaskDirective } from 'src/utils/phone.directive';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HomePageRoutingModule
  ],
  declarations: [HomePage, CpfMaskDirective,
    PhoneMaskDirective]
})
export class HomePageModule { }
