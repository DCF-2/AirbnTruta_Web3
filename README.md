# 🕵️‍♂️ AirBnTruta - Hospedagem Segura

> "O melhor sistema de hospedagem para quem precisa... desaparecer."

O **AirBnTruta** é um sistema web desenvolvido como projeto da disciplina de **Web 3 (IFPE)**. É uma paródia do Airbnb, focada no gerenciamento de aluguel de esconderijos ("mocós") para fugitivos que precisam de discrição total e hospedeiros que querem lucrar com segurança.

---

## 🎥 Demonstração do Sistema

Confira abaixo o vídeo demonstrando o fluxo completo (Cadastro, Login, Busca, Reserva e Aprovação):

[![Assista ao Vídeo](https://i.ytimg.com)](https://www.youtube.com/watch?v=qPMTrD-WjWQ)

*Sistema do AirbnTruta*

---

## 🚀 Funcionalidades

O sistema possui dois perfis de usuário com funcionalidades distintas:

### 🏠 Perfil: Hospedeiro (O Chefe)
* **Cadastro e Login:** Autenticação segura com "Vulgo" e Senha.
* **Gestão de Imóveis:** Cadastrar, visualizar e excluir esconderijos (Mocós).
* **Notificações:** Visualização de ícones de alerta (🔔) quando há interessados.
* **Aprovação:** Visualizar lista de fugitivos interessados e "Aceitar" um inquilino, o que torna o imóvel indisponível para outros.
* **Dados do Inquilino:** Acesso aos dados do fugitivo apenas após fechar negócio.

### 🏃‍♂️ Perfil: Fugitivo (O Cliente)
* **Busca Avançada:** Filtrar esconderijos por **Preço Máximo** e **Localização**.
* **Manifestar Interesse:** Solicitar vaga em um imóvel disponível.
* **Painel de Controle:** Acompanhar status das solicitações (⏳ Em Análise, ✅ Aprovado, ❌ Recusado).
* **Residência Atual:** Visualizar detalhes do local onde está morando (contato do dono).
* **Checkout:** Opção de "Vazar" (Sair do imóvel), liberando-o novamente no sistema.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17+**
* **Spring Boot 3** (Spring Web, DevTools)
* **Thymeleaf** (Template Engine com Server-Side Rendering)
* **JDBC & SQL Puro** (Implementação manual de Repositories e ConnectionManager sem JPA/Hibernate)
* **MySQL** (Banco de Dados)
* **Bootstrap 5** (Interface Responsiva e Dark Mode)
* **HTML5 & CSS3**

---

## ⚙️ Configuração e Execução

### 1. Pré-requisitos
* Java JDK 17 ou superior instalado.
* MySQL Server rodando.
* Maven (opcional, pois o projeto inclui o wrapper `mvnw`).

### 2. Configuração do Banco de Dados
1.  Crie um banco de dados no MySQL chamado `airbntruta`.
2.  Execute o script SQL disponível em `db_schema.sql` na raiz do projeto para criar as tabelas.
3.  Verifique o arquivo `src/main/resources/application.properties` e ajuste seu usuário e senha do banco:
    ```properties
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    ```

### 3. Rodando o Projeto
Abra o terminal na pasta do projeto e execute:

**No Windows:**
```powershell
./mvnw spring-boot:run
```

## No Linux/Mac

### Bash
```bash
./mvnw spring-boot:run
```

## 4. Acessando

Abra o navegador e acesse: 👉 http://localhost:8080

## 📂 Estrutura do Projeto (MVC)

- **controllers**: Controladores Spring (Hospedeiro, Fugitivo, etc).
- **model.entities**: Classes de domínio (POJOs).
- **model.repositories**: Camada de acesso a dados (DAO com JDBC).
- **resources/templates**: Views HTML com Thymeleaf.

## 🧼 Limpeza de Dados (Para Testes)

Se precisar zerar o banco de dados para gravar novos testes, execute os comandos SQL abaixo no seu Workbench:

```sql
USE airbntruta;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE interesse;
TRUNCATE TABLE hospedagem_servico;
TRUNCATE TABLE hospedagem;
TRUNCATE TABLE fugitivo;
TRUNCATE TABLE hospedeiro;

SET FOREIGN_KEY_CHECKS = 1;
```

## 👨‍💻 Autor

_Desenvolvido por_ _**Davi Freitas**_
_Curso de Análise e Desenvolvimento de Sistemas – IFPE_
