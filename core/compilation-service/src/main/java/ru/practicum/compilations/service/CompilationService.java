package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.AdminNewCompilationParamDto;
import ru.practicum.compilations.dto.AdminUpdateCompilationParamDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.PublicCompilationRequestParamsDto;

import java.util.List;

public interface CompilationService {

    /**
     * Добавление новой подборки
     *
     * @param adminNewCompilationParamDto Параметры новой подборки
     * @return Новая подборка CompilationDto
     */
    CompilationDto addCompilation(AdminNewCompilationParamDto adminNewCompilationParamDto);

    /**
     * Удаление подборки по ID
     *
     * @param compId ID удаляемой подборки
     */
    void deleteCompilation(Long compId);

    /**
     * Обновление информации подборки с заданной ID на переданную информацию
     *
     * @param compId                         ID изменяемой подборки
     * @param adminUpdateCompilationParamDto Изменяемая информация
     * @return Измененная подборка CompilationDto
     */
    CompilationDto updateCompilation(Long compId, AdminUpdateCompilationParamDto adminUpdateCompilationParamDto);


    /**
     * Получение подборки по ID = complId
     *
     * @param complId ID подборки
     * @return CompilationDto
     */
    CompilationDto getCompilationById(long complId);

    /**
     * Получение подборок событий. Параметры звпроса: PublicCompilationRequestParamsDto
     *
     * @param params Входные параметры запроса PublicCompilationRequestParamsDto
     * @return List<CompilationDto>
     */
    List<CompilationDto> getCompilationsList(PublicCompilationRequestParamsDto params);

}
