package com.lucasteixeira.agendador.business.services;

import com.lucasteixeira.agendador.business.dto.TarefasDTO;
import com.lucasteixeira.agendador.business.mapper.TarefaConverter;
import com.lucasteixeira.agendador.business.mapper.TarefaUpdateConverter;
import com.lucasteixeira.agendador.infrastructure.entity.TarefasEntity;
import com.lucasteixeira.agendador.infrastructure.enums.StatusNotificacaoEnum;
import com.lucasteixeira.agendador.infrastructure.exceptions.ResourceNotFoundException;
import com.lucasteixeira.agendador.infrastructure.repository.TarefasRepository;
import com.lucasteixeira.agendador.infrastructure.security.JwtUtil;
import com.lucasteixeira.agendador.producers.TaskProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefasRepository tarefaRepository;
    private final TarefaConverter tarefaConverter;
    private final JwtUtil jwtUtil;
    private final TarefaUpdateConverter tarefaUpdateConverter;
    private final TaskProducer taskProducer;

    public TarefasDTO gravarTarefa( TarefasDTO tarefasDTO, String token){

        String email = jwtUtil.extractEmailToken(token.substring(7));
        tarefasDTO.setDataCriacao(LocalDateTime.now()); //pega a hora atual
        tarefasDTO.setStatusNotificacaoEnum(StatusNotificacaoEnum.PENDENTE);
        tarefasDTO.setEmailUsuario(email);
        TarefasEntity entity = tarefaConverter.paraTarefasEntity(tarefasDTO);

        entity = tarefaRepository.save(entity);

        taskProducer.publishMessageEmailCadastro(email, entity);

        return tarefaConverter.paraTarefasDTO(entity);
    }

    public List<TarefasDTO> buscaTarefasAgendadasPorPeriodo(LocalDateTime dataInicial, LocalDateTime dataFinal){
        return tarefaConverter.paraListaTarefasDTO(tarefaRepository.findByDataEventoBetweenAndStatusNotificacaoEnum(dataInicial, dataFinal, StatusNotificacaoEnum.PENDENTE));
    }

    public List<TarefasDTO> buscaTarefasPorEmail(String token){
        String email = jwtUtil.extractEmailToken(token.substring(7));
        List<TarefasEntity> listasTarefas = tarefaRepository.findByEmailUsuario(email);
        return tarefaConverter.paraListaTarefasDTO(listasTarefas);
    }

    public void deletaTarefaPorId(String id){
        try {
            tarefaRepository.deleteById(id);
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Erro ao deletar tarefa por id, id inexistente " + id,
                    e.getCause());

        }

    }

    public TarefasDTO alteraStatus(StatusNotificacaoEnum status, String id){
        try {
            TarefasEntity entity = tarefaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada " + id));

            entity.setStatusNotificacaoEnum(status);
            entity.setDataAlteracao(LocalDateTime.now());

            entity = tarefaRepository.save(entity);

            taskProducer.publishMessageEmailUpdateStatusTask(entity.getEmailUsuario(), entity);

            return tarefaConverter.paraTarefasDTO(entity);
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Erro ao alterar status da tarefa " + id,
                    e.getCause());
        }
    }

    public TarefasDTO updateTarefas(TarefasDTO dto, String id) {
        try {
            TarefasEntity entity = tarefaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada " + id));
            tarefaUpdateConverter.updateTarefas(dto, entity);
            entity.setDataAlteracao(LocalDateTime.now());
            entity = tarefaRepository.save(entity);
            taskProducer.publishMessageEmailUpdateTask(entity.getEmailUsuario(), entity);

            return tarefaConverter.paraTarefasDTO(entity);

        } catch (ResourceNotFoundException e) {
            // Melhorei a mensagem para refletir que o erro é na atualização geral
            throw new ResourceNotFoundException("Erro ao atualizar a tarefa " + id, e);
        }
    }


}
