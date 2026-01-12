import { Component, signal, inject, OnInit, Inject } from '@angular/core';
import { CommonModule, DOCUMENT } from '@angular/common';
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
  private document = inject(DOCUMENT); // Injeta o documento para manipular o head

  // Inicializa vazio para ser preenchido pela API
  projects = signal<ProjectCard[]>([]);

  // Modos RGB: 'rainbow' | 'cyberpunk' | 'red' | 'stealth'
  rgbMode = signal<string>('rainbow');

  cycleRgbMode() {
    const modes = ['rainbow', 'cyberpunk', 'red', 'stealth'];
    const current = this.rgbMode();
    const nextIndex = (modes.indexOf(current) + 1) % modes.length;
    const nextMode = modes[nextIndex];

    this.rgbMode.set(nextMode);
    this.updateFavicon(nextMode);
  }

  updateFavicon(mode: string) {
    let color = '#00ff00'; // Default (Rainbow - Verde)

    switch (mode) {
      case 'cyberpunk': color = '#00f3ff'; break; // Ciano
      case 'red': color = '#ff0000'; break;       // Vermelho
      case 'stealth': color = '#ffffff'; break;   // Branco
      case 'rainbow': color = '#00ff00'; break;   // Verde
    }

    // SVG do Power Button com a cor dinâmica (URL Encoded)
    // %23 é o # para hex colors
    const svg = `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 512 512'><path fill='${color}' d='M288 32c0-17.7-14.3-32-32-32s-32 14.3-32 32V256c0 17.7 14.3 32 32 32s32-14.3 32-32V32zM143.5 120.6c13.6-11.3 15.4-31.5 4.1-45.1s-31.5-15.4-45.1-4.1C49.7 115.4 16 181.8 16 256c0 132.5 107.5 240 240 240s240-107.5 240-240c0-74.2-33.8-140.6-86.6-184.6c-13.6-11.3-33.8-9.4-45.1 4.1s-9.4 33.8 4.1 45.1c38.9 32.3 63.5 81 63.5 135.4c0 97.2-78.8 176-176 176s-176-78.8-176-176c0-54.4 24.7-103.1 63.5-135.4z'/></svg>`;

    const iconUrl = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`;

    // Atualiza a tag link
    const link: HTMLLinkElement | null = this.document.querySelector("link[rel*='icon']");
    if (link) {
      link.href = iconUrl;
    } else {
      // Cria se não existir (fallback)
      const newLink = this.document.createElement('link');
      newLink.rel = 'icon';
      newLink.href = iconUrl;
      this.document.head.appendChild(newLink);
    }
  }

  ngOnInit() {
    this.portfolioService.getProjects().subscribe({
      next: (data) => {
        // Ordena os projetos pela quantidade de tags (decrescente)
        const sortedData = data.sort((a, b) => {
          const tagsA = a.tags ? a.tags.length : 0;
          const tagsB = b.tags ? b.tags.length : 0;
          return tagsB - tagsA;
        });
        this.projects.set(sortedData);
      },
      error: (err) => {
        console.error('Erro ao carregar projetos:', err);
        // Opcional: manter dados de fallback em caso de erro
      }
    });
  }
}
