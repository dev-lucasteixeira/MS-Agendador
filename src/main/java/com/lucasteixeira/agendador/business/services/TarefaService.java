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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(value = "events", key = "#email")
    public TarefasDTO gravarTarefa( TarefasDTO tarefasDTO, String email){
        tarefasDTO.setDataCriacao(LocalDateTime.now()); //pega a hora atual
        tarefasDTO.setStatusNotificacaoEnum(StatusNotificacaoEnum.PENDENTE);
        tarefasDTO.setEmailUsuario(email);
        TarefasEntity entity = tarefaConverter.paraTarefasEntity(tarefasDTO);

        entity = tarefaRepository.save(entity);

        taskProducer.publishMessageEmailCadastro(email, entity);

        return tarefaConverter.paraTarefasDTO(entity);
    }

    @Cacheable(value = "task", key = "{#dataInicial, #dataFinal}")
    public List<TarefasDTO> buscaTarefasAgendadasPorPeriodo(LocalDateTime dataInicial, LocalDateTime dataFinal){
        return tarefaConverter.paraListaTarefasDTO(tarefaRepository.findByDataEventoBetweenAndStatusNotificacaoEnum(dataInicial, dataFinal, StatusNotificacaoEnum.PENDENTE));
    }

    @Cacheable(value = "task", key = "#email")
    public List<TarefasDTO> buscaTarefasPorEmail(String email){
        List<TarefasEntity> listasTarefas = tarefaRepository.findByEmailUsuario(email);
        return tarefaConverter.paraListaTarefasDTO(listasTarefas);
    }

    @CacheEvict(value = "events", key = "#email")
    public void deletaTarefaPorId(String id,String email){
        try {
            tarefaRepository.deleteById(id);
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Erro ao deletar tarefa por id, id inexistente " + id,
                    e.getCause());

        }

    }

    @CacheEvict(value = "task", allEntries = true)
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

    @CacheEvict(value = "task", allEntries = true)
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
