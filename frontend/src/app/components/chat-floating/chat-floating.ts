import { Component, ViewEncapsulation, signal, inject , ViewChild, ElementRef, effect, Input} from '@angular/core';
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
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  // Recebe o modo RGB do componente pai (Usando @Input tradicional para compatibilidade)
  @Input() rgbMode: string = 'rainbow';

  constructor(private http: HttpClient) {
    // 2. Criamos um "Efeito" que vigia o signal de mensagens
    effect(() => {
      // Apenas lendo o signal, o Angular sabe que deve rodar isso quando ele mudar
      const msgs = this.messages();
      const loading = this.isLoading(); // Também vigia o loading

      // Pequeno delay para dar tempo do HTML renderizar a nova mensagem
      setTimeout(() => {
        this.scrollToBottom();
      }, 100);
    });
  }
  scrollToBottom(): void {
    if (this.scrollContainer) {
      const element = this.scrollContainer.nativeElement;
      // Define a posição do scroll igual à altura total do conteúdo
      element.scrollTop = element.scrollHeight;
    }
  }

  // ADICIONE ESTA FUNÇÃO:
  formatMessage(text: string): string {
    if (!text) return '';

    // 1. Converte **texto** para <strong>texto</strong> (Negrito)
    let formatted = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

    // 2. Converte * item para • item (Lista) com quebra de linha
    formatted = formatted.replace(/^\* /gm, '<br>• ');

    // 3. Converte quebras de linha normais para <br>
    formatted = formatted.replace(/\n/g, '<br>');

    return formatted;
  }
  // Injeção moderna (sem construtor)
  //private http = inject(HttpClient);

  // Signals (O coração do Angular 21)
  isOpen = signal(false);
  isExpanded = signal(false); // Novo signal para controlar a expansão
  isLoading = signal(false);
  userQuestion = signal(''); // Transformei o input em signal também (opcional, mas bom)

  messages = signal<Message[]>([
    { text: 'Olá! Sou a IA do Leandro. Pergunte sobre projetos ou stack técnica.', isUser: false }
  ]);

  toggleChat() {
    this.isOpen.update(v => !v);
  }

  toggleExpand() {
    this.isExpanded.update(v => !v);
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
