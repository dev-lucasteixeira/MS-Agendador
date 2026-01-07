package com.lucasteixeira.agendador.controller;

import com.lucasteixeira.agendador.business.services.TarefaService;
import com.lucasteixeira.agendador.business.dto.TarefasDTO;
import com.lucasteixeira.agendador.infrastructure.enums.StatusNotificacaoEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefasController {

    private final TarefaService tarefaService;

    @PostMapping
    public ResponseEntity <TarefasDTO> gravarTarefa(@RequestBody TarefasDTO tarefasDTO,
                                                    @AuthenticationPrincipal Jwt jwt){
        String email = jwt.getClaimAsString("email");
        return ResponseEntity.ok(tarefaService.gravarTarefa(tarefasDTO, email));
    }


    @GetMapping("/eventos")
    public ResponseEntity<List<TarefasDTO>> buscaListaDeTarefasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime dataFinal){

        return ResponseEntity.ok(tarefaService.buscaTarefasAgendadasPorPeriodo(dataInicial, dataFinal));
    }


    @GetMapping
    public ResponseEntity<List<TarefasDTO>> buscaListaDeTarefasPorEmail(@AuthenticationPrincipal Jwt jwt){
        String email = jwt.getClaimAsString("email");
        return ResponseEntity.ok(tarefaService.buscaTarefasPorEmail(email));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletaTarefaPorId(@RequestParam String id,
                                                  @AuthenticationPrincipal Jwt jwt){

        String email = jwt.getClaimAsString("email");
        tarefaService.deletaTarefaPorId(id, email);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<TarefasDTO> alteraStatusNotificacao(@RequestParam("status")StatusNotificacaoEnum status,
                                                              @RequestParam("id") String id){
        return ResponseEntity.ok(tarefaService.alteraStatus(status, id));
    }

    @PutMapping
    public ResponseEntity<TarefasDTO> UpdateTarefa(@RequestBody TarefasDTO dto, @RequestParam("id") String id){
        return ResponseEntity.ok(tarefaService.updateTarefas(dto, id));
    }

}
