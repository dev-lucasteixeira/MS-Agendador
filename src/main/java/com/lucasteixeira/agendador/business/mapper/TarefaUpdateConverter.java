package com.lucasteixeira.agendador.business.mapper;


import com.lucasteixeira.agendador.business.dto.TarefasDTO;
import com.lucasteixeira.agendador.infrastructure.entity.TarefasEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
<<<<<<< HEAD
import org.mapstruct.NullValueMappingStrategy;
=======
>>>>>>> a94526c (reconectando no projeto)
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)//mapeas os valores e se for nulo, ele ignora
public interface TarefaUpdateConverter {

    void updateTarefas(TarefasDTO dto, @MappingTarget TarefasEntity entity); // mappingtarget faz com que o maper nao sobrescreva os valores e seleciona se um for nulo
}
