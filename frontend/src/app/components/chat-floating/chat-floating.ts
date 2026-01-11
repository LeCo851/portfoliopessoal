import { Component, ViewEncapsulation, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

// Material Imports
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

interface Message {
  text: string;
  isUser: boolean;
}

@Component({
  selector: 'app-chat-floating',
  standalone: true,
  imports: [
    FormsModule,            // Para o [(ngModel)]
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './chat-floating.html',
  styleUrl: './chat-floating.scss',
  encapsulation: ViewEncapsulation.None
})
export class ChatFloatingComponent {
  // Injeção moderna (sem construtor)
  private http = inject(HttpClient);

  // Signals (O coração do Angular 21)
  isOpen = signal(false);
  isLoading = signal(false);
  userQuestion = signal(''); // Transformei o input em signal também (opcional, mas bom)

  messages = signal<Message[]>([
    { text: 'Olá! Sou a IA do Leandro. Pergunte sobre projetos ou stack técnica.', isUser: false }
  ]);

  toggleChat() {
    this.isOpen.update(v => !v);
  }

  sendMessage() {
    const question = this.userQuestion();
    if (!question.trim()) return;

    // 1. Atualiza UI imediatamente (Optimistic UI)
    this.messages.update(msgs => [...msgs, { text: question, isUser: true }]);
    this.userQuestion.set(''); // Limpa input
    this.isLoading.set(true);

    // 2. Chama Backend
    this.http.post<any>('http://localhost:8080/api/chat', { question })
      .subscribe({
        next: (res) => {
          this.messages.update(msgs => [...msgs, { text: res.answer, isUser: false }]);
          this.isLoading.set(false);
        },
        error: () => {
          this.messages.update(msgs => [...msgs, { text: 'Erro ao conectar. O backend está rodando?', isUser: false }]);
          this.isLoading.set(false);
        }
      });
  }
}
