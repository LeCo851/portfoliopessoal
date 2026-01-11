import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideZonelessChangeDetection } from '@angular/core';

bootstrapApplication(AppComponent, {
  providers: [
    provideZonelessChangeDetection(), // Ativa o modo Zoneless (Angular 18+) e remove a dependência do zone.js
    provideHttpClient(), // Necessário para o ChatFloatingComponent fazer requisições
    provideAnimationsAsync() // Necessário para os componentes do Angular Material
  ]
}).catch((err) => console.error(err));
