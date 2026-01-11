import { Component, inject, signal } from '@angular/core';
import { PortfolioService } from './portfolio';
import { ProjectCard } from './project.interface';
import { ChatFloatingComponent } from './components/chat-floating/chat-floating'

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ChatFloatingComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent {
  private service = inject(PortfolioService);

  // Usando Signals para performance máxima
  projects = signal<ProjectCard[]>([]);

  constructor() {
    this.service.getProjects().subscribe({
      next: (dados) => {
        console.log('Dados recebidos do Java:', dados); // Debug no navegador
        this.projects.set(dados);
      },
      error: (erro) => console.error('Erro ao conectar no Java:', erro)
    });
  }
}
