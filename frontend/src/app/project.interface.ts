export interface ProjectCard {
  id: string;
  titulo: string;
  resumo: string;
  tags: string[];    // Lista de tecnologias (Java, Docker, etc)
  imageUrl: string;
  linkGithub: string;
}
