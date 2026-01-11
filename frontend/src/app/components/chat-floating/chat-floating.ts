import { Component, inject, signal, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Message {
  text: string;
  isUser: boolean;
}

@Component({
  selector: 'app-chat-floating',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-floating.html',
  // RADICAL: Forçamos o posicionamento aqui para não depender do Tailwind carregar ou não.
  styles: [`
    :host {
      position: fixed;
      bottom: 20px;
      right: 20px;
      z-index: 999999; /* Valor nuclear para garantir visibilidade */
      display: block;
    }
  `]
})
export class ChatFloatingComponent {
  // RADICAL: Se o HttpClient falhar, o componente NÃO trava.
  private http = inject(HttpClient, { optional: true });

  // Usando Signals para estado reativo
  isOpen = signal(false);
  isLoading = signal(false);
  userQuestion = signal('');
  
  messages = signal<Message[]>([
    { text: 'Olá! Sou a IA do Leandro. Pergunte sobre projetos ou skills!', isUser: false }
  ]);

  constructor() {
    console.log('ChatFloatingComponent: INICIADO');
    if (!this.http) {
      console.error('ATENÇÃO: HttpClient não encontrado. Verifique app.config.ts');
    }
  }

  toggleChat() {
    this.isOpen.update(v => !v);
  }

  sendMessage() {
    const question = this.userQuestion();
    if (!question.trim()) return;

    // Atualiza UI imediatamente
    this.messages.update(msgs => [...msgs, { text: question, isUser: true }]);
    this.userQuestion.set('');
    this.isLoading.set(true);

    if (!this.http) {
      setTimeout(() => {
        this.messages.update(msgs => [...msgs, { text: 'ERRO: HttpClient não configurado. O chat está em modo offline.', isUser: false }]);
        this.isLoading.set(false);
      }, 1000);
      return;
    }

    // Chama Backend
    this.http.post<any>('http://localhost:8080/api/chat', { question: question })
      .subscribe({
        next: (res) => {
          this.messages.update(msgs => [...msgs, { text: res.answer, isUser: false }]);
          this.isLoading.set(false);
        },
        error: () => {
          this.messages.update(msgs => [...msgs, { text: 'Erro ao conectar com o servidor.', isUser: false }]);
          this.isLoading.set(false);
        }
      });
  }
}
