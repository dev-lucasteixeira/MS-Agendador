package com.lucasteixeira.agendador.producers;

import com.lucasteixeira.agendador.business.dto.EmailDTO;
import com.lucasteixeira.agendador.infrastructure.entity.TarefasEntity;
import com.lucasteixeira.agendador.infrastructure.security.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class TaskProducer {

    final RabbitTemplate rabbitTemplate;
    private final JwtUtil jwtUtil;

    public TaskProducer(RabbitTemplate rabbitTemplate, JwtUtil jwtUtil) {
        this.rabbitTemplate = rabbitTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Value(value = "${mq.queues.emailcadastrotarefas-queue}")
    private String routingKey;

    public void publishMessageEmailCadastro(String email, TarefasEntity tarefas){

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var emailDTO = new EmailDTO();
        emailDTO.setEmailTo(email);
        emailDTO.setSubject("Tarefa Cadastrada! 📝");
        emailDTO.setText("Uma nova tarefa foi agendada com sucesso no seu painel!\n\n" +
                "Detalhes da Tarefa:\n" +
                "------------------------------------------\n" +
                "📝 Tarefa: " + tarefas.getNomeTarefa() + "\n" +
                "📅 Data: " + tarefas.getDataCriacao().format(formatador) + "\n" +
                "------------------------------------------\n\n" +
                "Organizar suas atividades é o melhor caminho para um dia produtivo. " +
                "Não se esqueça de verificar seus prazos regularmente!\n\n" +
                "Bom trabalho,\n" +
                "Equipe Task Manager");

        rabbitTemplate.convertAndSend("",routingKey, emailDTO);

    }

    public void publishMessageEmailUpdateTask(String email, TarefasEntity tarefas){

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var emailDTO = new EmailDTO();
        emailDTO.setEmailTo(email);
        emailDTO.setSubject("Tarefa Atualizada! 📝");
        emailDTO.setText("Uma tarefa do seu painel foi atualizada com sucesso!\n\n" +
                "Detalhes da Atualização:\n" +
                "------------------------------------------\n" +
                "📝 Tarefa: " + tarefas.getNomeTarefa() + "\n" +
                "📅 Modificada em: " + tarefas.getDataAlteracao().format(formatador) + "\n" +
                "------------------------------------------\n\n" +
                "Manter suas tarefas atualizadas ajuda você a ter um controle melhor do seu tempo. " +
                "Continue focado(a) em seus objetivos!\n\n" +
                "Bom trabalho,\n" +
                "Equipe Task Manager");

        rabbitTemplate.convertAndSend("",routingKey, emailDTO);

    }

    public void publishMessageEmailUpdateStatusTask(String email, TarefasEntity tarefas){

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var emailDTO = new EmailDTO();
        emailDTO.setEmailTo(email);
        emailDTO.setSubject("Tarefa Atualizada! 📝");
        emailDTO.setText("O status da sua tarefa foi alterado com sucesso!\n\n" +
                "Confira a atualização:\n" +
                "------------------------------------------\n" +
                "📝 Tarefa: " + tarefas.getNomeTarefa() + "\n" +
                "📊 Novo Status: " + tarefas.getStatusNotificacaoEnum().toString() + "\n" +
                "📅 Alterado em: " + tarefas.getDataAlteracao().format(formatador) + "\n" +
                "------------------------------------------\n\n" +
                "Acompanhar o progresso das suas atividades é fundamental para manter a organização. " +
                "Continue assim, cada tarefa concluída é um passo a mais rumo aos seus objetivos!\n\n" +
                "Bom trabalho,\n" +
                "Equipe Task Manager");

        rabbitTemplate.convertAndSend("",routingKey, emailDTO);

    }

}
