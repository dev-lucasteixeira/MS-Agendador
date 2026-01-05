# 📅 MS-Agendador — Core Engine & Scheduler

Este microsserviço é o núcleo de inteligência do ecossistema de agendamento. Ele é responsável por gerenciar o ciclo de vida das tarefas, garantir a persistência em banco de dados NoSQL e orquestrar a comunicação orientada a eventos via mensageria.

---

## 🏗️ Arquitetura e Papel no Ecossistema

O **MS-Agendador** atua como o provedor de recursos para o BFF e o produtor de eventos para o sistema de notificações:



1.  **Recebimento**: Processa requisições REST validadas pelo BFF.
2.  **Persistência**: Armazena dados de forma flexível no **MongoDB**.
3.  **Agendamento**: Monitora prazos e eventos cronológicos.
4.  **Mensageria**: Publica eventos no **RabbitMQ** para que o `MS-Notificacao` realize os disparos de e-mail de forma assíncrona.
5.  **Escalabilidade com Cache:** Utilização de Redis para armazenamento temporário de dados, mitigando gargalos de I/O no banco de dados relacional.

---

## 🛠️ Stack Tecnológica

- **Java 17/24** & **Spring Boot 3.5.x**
- **Spring Data MongoDB**: Persistência de alta performance e esquema flexível.
- **Spring Security + JWT**: Validação de integridade de tokens propagados.
- **RabbitMQ (AMQP)**: Mensageria para desacoplamento de serviços.
- **JUnit 5 & AssertJ**: Cobertura de testes unitários e de integração.
- **Gradle**: Gerenciamento de dependências e build.
- **Performance & Caching:** Spring Data Redis & Redis (In-memory store)

---

## ⚙️ Funcionalidades Principais

| Operação | Descrição Técnica |
| :--- | :--- |
| **POST /tarefas** | Registra nova tarefa e agenda o gatilho de notificação. |
| **GET /tarefas** | Filtra tarefas dinamicamente por e-mail do proprietário. |
| **GET /tarefas/eventos** | Consulta complexa por range de `LocalDateTime`. |
| **PATCH /status** | Atualiza o estado da notificação (Pendente, Enviado, Erro). |
| **DELETE /{id}** | Remove tarefas com validação de propriedade. |

---

## 🔐 Segurança e Identidade

Diferente de sistemas monolíticos, este serviço pratica a **Segurança Stateless**. Ele não consulta o banco de usuários; ele confia na assinatura do JWT propagado pelo BFF. O e-mail do usuário é extraído diretamente do *Subject* do Token para garantir que um usuário nunca acesse ou delete tarefas de outro.

---

## 🚀 Como Executar

### 1. Pré-requisitos
- MongoDB (Porta `27017`)
- RabbitMQ (Porta `5672` para AMQP / `15672` para Management)
- Instância do `MS-Usuario` ativa para geração de tokens de teste.

### 2. Configuração (`application.yml`)
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/db_agendador
  rabbitmq:
    host: localhost
    port: 5672
3. Build e Execução
Bash

./gradlew clean build
java -jar build/libs/agendador-0.0.1-SNAPSHOT.jar
📨 Integração com RabbitMQ
Este serviço atua como um Producer. Ao salvar ou atingir o tempo de uma tarefa, ele envia um JSON para a Exchange configurada:

JSON

{
  "id": "65a1...",
  "emailUsuario": "lucas@email.com",
  "nomeTarefa": "Lavar louça",
  "dataEvento": "2026-01-03T11:00:00"
}

```

Desenvolvido por Lucas Teixeira 👨‍💻
