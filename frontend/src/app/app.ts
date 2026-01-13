import { Component, signal, inject, OnInit, Inject, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DOCUMENT } from '@angular/common';
import { ChatFloatingComponent } from './components/chat-floating/chat-floating';
import { PortfolioService } from './portfolio';
import { ProjectCard } from './project.interface';

declare const mermaid: any;

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    ChatFloatingComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent implements OnInit {
  private portfolioService = inject(PortfolioService);
  private document = inject(DOCUMENT);
  private cdr = inject(ChangeDetectorRef);

  @ViewChild('mermaidContainer') mermaidContainer!: ElementRef;

  projects = signal<ProjectCard[]>([]);
  rgbMode = signal<string>('rainbow');

  selectedProject = signal<ProjectCard | null>(null);
  diagramCode = signal<string>('');
  isLoadingDiagram = signal<boolean>(false);

  cycleRgbMode() {
    const modes = ['rainbow', 'cyberpunk', 'red', 'stealth'];
    const current = this.rgbMode();
    const nextIndex = (modes.indexOf(current) + 1) % modes.length;
    const nextMode = modes[nextIndex];

    this.rgbMode.set(nextMode);
    this.updateFavicon(nextMode);
  }

  updateFavicon(mode: string) {
    let color = '#00ff00';
    switch (mode) {
      case 'cyberpunk': color = '#00f3ff'; break;
      case 'red': color = '#ff0000'; break;
      case 'stealth': color = '#ffffff'; break;
      case 'rainbow': color = '#00ff00'; break;
    }
    const svg = `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 512 512'><path fill='${color}' d='M288 32c0-17.7-14.3-32-32-32s-32 14.3-32 32V256c0 17.7 14.3 32 32 32s32-14.3 32-32V32zM143.5 120.6c13.6-11.3 15.4-31.5 4.1-45.1s-31.5-15.4-45.1-4.1C49.7 115.4 16 181.8 16 256c0 132.5 107.5 240 240 240s240-107.5 240-240c0-74.2-33.8-140.6-86.6-184.6c-13.6-11.3-33.8-9.4-45.1 4.1s-9.4 33.8 4.1 45.1c38.9 32.3 63.5 81 63.5 135.4c0 97.2-78.8 176-176 176s-176-78.8-176-176c0-54.4 24.7-103.1 63.5-135.4z'/></svg>`;
    const iconUrl = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`;
    const link: HTMLLinkElement | null = this.document.querySelector("link[rel*='icon']");
    if (link) {
      link.href = iconUrl;
    } else {
      const newLink = this.document.createElement('link');
      newLink.rel = 'icon';
      newLink.href = iconUrl;
      this.document.head.appendChild(newLink);
    }
  }

  openArchitecture(project: ProjectCard) {
    this.selectedProject.set(project);
    this.isLoadingDiagram.set(true);
    this.diagramCode.set('');

    this.portfolioService.getArchitecture(project.id).subscribe({
      next: (res: any) => {
        let code = '';
        if (res && res.mermaidCode) code = res.mermaidCode;
        else if (typeof res === 'string') code = res;
        else if (res && res.diagram) code = res.diagram;

        if (code) {
          // Limpeza Robusta
          code = code
            .replace(/^"|"$/g, '')
            .replace(/\\n/g, '\n')
            .replace(/\\r/g, '')
            .replace(/\r/g, '')
            .replace(/&gt;/g, '>')
            .replace(/&lt;/g, '<')
            .replace(/,?\s*shape=[a-zA-Z0-9]+/g, '')
            .replace(/\s*\|>\s*/g, ' --> ')
            .replace(/\[([^\]]*?\(.*?\)[^\]]*?)\]/g, '["$1"]')
            // Correções específicas para 'end' colado
            .replace(/Composeend/gi, 'Compose\nend')
            .replace(/endsubgraph/gi, 'end\nsubgraph') // Corrige 'endsubgraph'
            .replace(/end\s*subgraph/gi, 'end\nsubgraph')
            .replace(/([a-z])end\s*$/gmi, (match, p1) => {
               if (p1 === 'k' || p1 === 't') return match;
               return p1 + '\nend';
            })
            .trim();

          // AUTO-CORREÇÃO DE TIPO DE DIAGRAMA
          if (code.includes('participant') || code.includes('actor')) {
            code = code.replace(/^graph\s+[A-Z]+\s*/i, '');
            if (!code.startsWith('sequenceDiagram')) {
              code = 'sequenceDiagram\n' + code;
            }
          }

          code += '\n';

          console.log('Mermaid Code Final:', code);

          this.diagramCode.set(code);
          this.isLoadingDiagram.set(false);

          this.cdr.detectChanges();

          setTimeout(() => this.renderMermaid(), 100);
        } else {
          this.handleError('Formato inválido');
        }
      },
      error: (err) => {
        console.error(err);
        this.handleError('Erro ao carregar');
      }
    });
  }

  handleError(msg: string) {
    this.diagramCode.set(`graph TD; A[Erro] --> B[${msg}]; style A fill:#f00;`);
    this.isLoadingDiagram.set(false);
    this.cdr.detectChanges();
    setTimeout(() => this.renderMermaid(), 100);
  }

  closeModal() {
    this.selectedProject.set(null);
  }

  async renderMermaid() {
    if (typeof mermaid === 'undefined' || !this.mermaidContainer) {
      console.error('Mermaid não carregado');
      return;
    }

    try {
      mermaid.initialize({
        startOnLoad: false,
        theme: 'dark',
        securityLevel: 'loose',
        fontFamily: 'Courier New',
        logLevel: 'error'
      });

      const container = this.mermaidContainer.nativeElement;
      const code = this.diagramCode();

      container.innerHTML = '<div style="color:var(--accent-color)">Renderizando...</div>';

      const id = 'mermaid-' + Math.floor(Math.random() * 100000);

      const { svg } = await mermaid.render(id, code);

      container.innerHTML = svg;

      const svgElement = container.querySelector('svg');
      if (svgElement) {
        svgElement.style.maxWidth = '100%';
        svgElement.style.height = 'auto';
      }

    } catch (e: any) {
      console.error('Erro Fatal Mermaid:', e);
      if (this.mermaidContainer) {
        this.mermaidContainer.nativeElement.innerHTML = `
          <div style="color:red; padding:20px; border:1px solid red; overflow:auto;">
            <strong>Erro de Renderização:</strong><br>
            <pre>${e.message}</pre>
            <br>
            <small>Tente fechar e abrir novamente.</small>
          </div>`;
      }
    }
  }

  ngOnInit() {
    this.portfolioService.getProjects().subscribe({
      next: (data) => {
        const sortedData = data.sort((a, b) => {
          const tagsA = a.tags ? a.tags.length : 0;
          const tagsB = b.tags ? b.tags.length : 0;
          return tagsB - tagsA;
        });
        this.projects.set(sortedData);
      },
      error: (err) => console.error(err)
    });
  }
}
