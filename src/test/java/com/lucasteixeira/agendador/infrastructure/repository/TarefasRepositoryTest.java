package com.lucasteixeira.agendador.infrastructure.repository;

import com.lucasteixeira.agendador.infrastructure.entity.TarefasEntity;
import com.lucasteixeira.agendador.infrastructure.enums.StatusNotificacaoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TarefasRepositoryTest {

    @Autowired
    private TarefasRepository tarefasRepository;

    @Test
    @DisplayName("")
    void findByDataEventoBetweenAndStatusNotificacaoEnum() {

    }

    @Test
    @DisplayName("")
    void findByEmailUsuario() {

        String emailUser = "teste@teste.com";
        TarefasEntity tarefa = new TarefasEntity("1","teste",null, LocalDateTime.now(),
                null,emailUser,null, StatusNotificacaoEnum.PENDENTE);


    }
}