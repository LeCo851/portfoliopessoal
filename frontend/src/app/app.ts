import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatFloatingComponent } from './components/chat-floating/chat-floating';
import { PortfolioService } from './portfolio';
import { ProjectCard } from './project.interface';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    ChatFloatingComponent // <--- Importante: O componente do chat deve estar aqui
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent implements OnInit {
  private portfolioService = inject(PortfolioService);

  // Inicializa vazio para ser preenchido pela API
  projects = signal<ProjectCard[]>([]);

  ngOnInit() {
    this.portfolioService.getProjects().subscribe({
      next: (data) => {
        this.projects.set(data);
      },
      error: (err) => {
        console.error('Erro ao carregar projetos:', err);
        // Opcional: manter dados de fallback em caso de erro
      }
    });
  }
}
