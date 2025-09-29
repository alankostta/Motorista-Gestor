# 🚗 Motorista-Gestor

> **Sistema de Gestão para Motoristas de Aplicativos**

Um sistema web completo desenvolvido em Spring Boot para ajudar motoristas de aplicativos a gerenciar suas atividades diárias, despesas, veículos e ganhos de forma eficiente e organizada.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.0-purple)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-green)

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Como Usar](#como-usar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contribuição](#contribuição)
- [Licença](#licença)

## 🎯 Sobre o Projeto

O **Motorista-Gestor** é uma aplicação web desenvolvida para facilitar a vida dos motoristas de aplicativos, oferecendo ferramentas para:

- **Controle Financeiro**: Registro de ganhos e despesas diárias
- **Gestão de Veículos**: Cadastro e controle de carros utilizados
- **Relatórios**: Análise de performance e lucratividade
- **Perfil Personalizado**: Sistema de usuários com fotos de perfil
- **Dashboard Intuitivo**: Visão geral das atividades e métricas

## ✨ Funcionalidades

### 🔐 Autenticação e Segurança
- [x] Sistema de login e registro seguro
- [x] Autenticação baseada em Spring Security
- [x] Controle de acesso por roles (USER/ADMIN)
- [x] Sessões seguras e criptografia de senhas

### 👤 Gestão de Perfil
- [x] Cadastro e edição de perfil de usuário
- [x] Upload de foto de perfil com preview instantâneo
- [x] Validação de arquivos de imagem
- [x] Armazenamento seguro de fotos

### 🚙 Gestão de Veículos
- [x] Cadastro de múltiplos veículos
- [x] Controle de informações detalhadas (modelo, placa, ano, etc.)
- [x] Associação de registros diários com veículos específicos

### 📊 Registros Diários
- [x] Registro de atividades diárias de trabalho
- [x] Controle de horários de início e fim
- [x] Registro de quilometragem
- [x] Cálculo automático de ganhos líquidos

### 💰 Controle de Despesas
- [x] Cadastro de despesas por categoria
- [x] Associação de despesas com registros diários
- [x] Relatórios de gastos por período
- [x] Análise de lucratividade

### 📈 Dashboard e Relatórios
- [x] Dashboard com métricas principais
- [x] Gráficos de performance
- [x] Relatórios de ganhos e despesas
- [x] Análise de tendências

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.3** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM (Object-Relational Mapping)
- **PostgreSQL** - Banco de dados relacional
- **Maven** - Gerenciamento de dependências

### Frontend
- **Thymeleaf** - Template engine
- **Bootstrap 5.3.0** - Framework CSS
- **Font Awesome 6.2.0** - Ícones
- **Chart.js** - Gráficos e visualizações
- **JavaScript ES6+** - Interatividade

### Ferramentas de Desenvolvimento
- **Spring Boot DevTools** - Hot reload
- **Spring Boot Validation** - Validação de dados
- **Maven** - Build e gerenciamento de dependências

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 17** ou superior
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git**

## 🚀 Instalação

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/Motorista-Gestor.git
cd Motorista-Gestor
```

### 2. Configure o banco de dados PostgreSQL
```sql
-- Crie um banco de dados
CREATE DATABASE app;

-- Crie um usuário (opcional)
CREATE USER motorista_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE app TO motorista_user;
```

### 3. Configure as variáveis de ambiente
Edite o arquivo `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/app
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 4. Execute a aplicação
```bash
# Compile e execute
mvn spring-boot:run

# Ou compile e execute o JAR
mvn clean package
java -jar target/Motorista-Gestor-0.0.1-SNAPSHOT.jar
```

### 5. Acesse a aplicação
Abra seu navegador e acesse: `http://localhost:8080`

## ⚙️ Configuração

### Configurações Principais

| Configuração | Valor Padrão | Descrição |
|--------------|--------------|-----------|
| `server.port` | 8080 | Porta do servidor |
| `spring.jpa.hibernate.ddl-auto` | update | Criação automática de tabelas |
| `spring.servlet.multipart.max-file-size` | 10MB | Tamanho máximo de upload |

### Upload de Arquivos
- **Pasta de upload**: `src/main/resources/static/uploads/profile-photos/`
- **Tipos aceitos**: Imagens (JPG, PNG, GIF, etc.)
- **Tamanho máximo**: 10MB por arquivo

## 📖 Como Usar

### 1. Primeiro Acesso
1. Acesse `http://localhost:8080`
2. Clique em "Criar Conta"
3. Preencha seus dados e faça upload de uma foto
4. Faça login com suas credenciais

### 2. Configurando seu Perfil
1. Clique no seu avatar no menu lateral
2. Selecione "Configurações"
3. Atualize suas informações e foto de perfil

### 3. Cadastrando Veículos
1. Acesse "Veículos" no menu
2. Clique em "Adicionar Veículo"
3. Preencha as informações do seu carro

### 4. Registrando Atividades Diárias
1. Vá para "Registros Diários"
2. Clique em "Novo Registro"
3. Preencha horários, quilometragem e ganhos
4. Associe despesas se necessário

### 5. Controlando Despesas
1. Acesse "Despesas"
2. Registre combustível, manutenção, etc.
3. Associe com registros diários específicos

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/dg/
│   │   ├── controller/          # Controllers REST
│   │   ├── model/              # Entidades JPA
│   │   ├── repository/         # Repositórios de dados
│   │   ├── service/            # Lógica de negócio
│   │   ├── dto/                # Data Transfer Objects
│   │   └── config/             # Configurações
│   └── resources/
│       ├── templates/          # Templates Thymeleaf
│       ├── static/             # CSS, JS, imagens
│       └── application.properties
└── test/                       # Testes unitários
```

### Principais Componentes

- **Controllers**: Gerenciam requisições HTTP
- **Services**: Contêm a lógica de negócio
- **Repositories**: Interface com o banco de dados
- **Models**: Entidades do domínio
- **DTOs**: Transferência de dados entre camadas

## 🤝 Contribuição

Contribuições são sempre bem-vindas! Para contribuir:

1. **Fork** o projeto
2. Crie uma **branch** para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. Abra um **Pull Request**

### Diretrizes de Contribuição

- Siga os padrões de código existentes
- Adicione testes para novas funcionalidades
- Atualize a documentação quando necessário
- Use mensagens de commit descritivas

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 📞 Contato

**Desenvolvedor**: Seu Nome
- GitHub: [@seu-usuario](https://github.com/seu-usuario)
- Email: seu.email@exemplo.com
- LinkedIn: [Seu Perfil](https://linkedin.com/in/seu-perfil)

## 🙏 Agradecimentos

- [Spring Boot](https://spring.io/projects/spring-boot) - Framework principal
- [Bootstrap](https://getbootstrap.com/) - Framework CSS
- [Font Awesome](https://fontawesome.com/) - Ícones
- [Chart.js](https://www.chartjs.org/) - Gráficos

---

⭐ **Se este projeto foi útil para você, considere dar uma estrela!**

---

*Desenvolvido com ❤️ para a comunidade de motoristas de aplicativos*