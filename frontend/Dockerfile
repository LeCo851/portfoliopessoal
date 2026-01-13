# --- ESTÁGIO 1: BUILD ---
FROM node:20-alpine AS builder

WORKDIR /app

# Instala dependências
COPY package*.json ./
RUN npm ci

# Copia código e compila para produção
COPY . .
RUN npm run build -- --configuration production

# --- ESTÁGIO 2: SERVIDOR NGINX ---
FROM nginx:alpine

# Copia o build do Angular para a pasta do Nginx
# IMPORTANTE: Verifique se sua pasta dist é 'dist/seu-projeto/browser' ou só 'dist/seu-projeto'
COPY --from=builder /app/dist/*/browser /usr/share/nginx/html

# Copia nossa configuração personalizada do Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
