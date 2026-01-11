import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PortfolioService } from './portfolio';
import { ProjectCard } from './project.interface';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
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
