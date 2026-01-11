# Portfolio Pessoal Inteligente 🚀

Este é um projeto Full Stack que automatiza a exibição de um portfólio técnico. Ele consome a API do GitHub para buscar repositórios marcados com a tag `portfolio` e utiliza Inteligência Artificial para enriquecer as descrições e extrair tecnologias automaticamente.

## 🛠️ Tecnologias Utilizadas

### Backend (Spring Boot)
- **Java 21**: Versão mais recente com foco em performance.
- **Spring Boot 3.5.x**: Framework base para a API REST.
- **Spring AI**: Integração com OpenAI para análise inteligente dos READMEs.
- **Spring Cache**: Otimização de chamadas à API do GitHub.
- **Lombok**: Redução de código boilerplate.
- **RestClient**: Cliente HTTP moderno do Spring para consumo de APIs externas.

### Frontend (Angular)
- **Angular 19+**: Framework moderno para a interface.
- **Signals**: Gerenciamento de estado reativo e performático.
- **SCSS**: Estilização avançada e modular.
- **TypeScript**: Tipagem forte para maior segurança no desenvolvimento.

### Infraestrutura
- **Docker & Docker Compose**: Orquestração de containers.
- **GitHub API**: Fonte de dados dos projetos.

## 🏗️ Arquitetura do Projeto

O projeto é dividido em dois módulos principais:

1.  **`spring-app/`**: API REST que faz o "trabalho pesado".
    - Busca repositórios no GitHub.
    - Lê o conteúdo do `README.md` de cada projeto.
    - Envia os dados para a IA (OpenAI) gerar um título comercial, resumo técnico e identificar tecnologias.
    - Expõe um endpoint `/api/projects` para o frontend.

2.  **`frontend/`**: Interface de usuário moderna e responsiva.
    - Consome a API Java.
    - Exibe os projetos em cards atrativos com tags e links diretos.

## 🚀 Como Executar

### Pré-requisitos
- JDK 21
- Node.js & Angular CLI
- Docker (opcional)
- Uma chave de API da OpenAI (configurada no backend)

### Backend
1. Navegue até `spring-app/`.
2. Configure sua `SPRING_AI_OPENAI_API_KEY` no arquivo de propriedades ou variável de ambiente.
3. Execute: `./mvnw spring-boot:run`

### Frontend
1. Navegue até `frontend/`.
2. Instale as dependências: `npm install`
3. Execute: `ng serve`
4. Acesse: `http://localhost:4200`

## 📝 Notas de Desenvolvimento
- O backend utiliza o pacote `com.leandrocoelho.springapp.portfoliopessoal`.
- A integração com a IA foi desenhada para atuar como um "Recrutador Técnico Sênior", garantindo que os resumos sejam profissionais e focados em valor de negócio.

---
Desenvolvido por [Leandro Coelho](https://github.com/LeCo851)
