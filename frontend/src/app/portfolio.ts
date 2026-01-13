import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { ProjectCard } from './project.interface';

@Injectable({ providedIn: 'root' })
export class PortfolioService {
  private http = inject(HttpClient);

  // O Angular chama o Java na porta 8080
  private API_URL = 'http://localhost:8080/api/projects';
  private ARCH_API_URL = 'http://localhost:8080/api/architecture';

  getProjects() {
    return this.http.get<ProjectCard[]>(this.API_URL);
  }

  getArchitecture(projectId: string | number) {
    return this.http.get<{ mermaidCode: string }>(`${this.ARCH_API_URL}/${projectId}`);
  }
}
