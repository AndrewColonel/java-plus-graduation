package ru.practicum.compilations.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.client.EventClient;
import ru.practicum.compilations.dto.*;
import ru.practicum.compilations.dto.ext.EventShortDto;
import ru.practicum.compilations.model.entity.Compilation;
import ru.practicum.compilations.model.CompilationMapper;
import ru.practicum.compilations.repository.CompilationRepository;

import ru.practicum.exception.EntityNotExistsException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventClient eventClient;


    /**
     * Добавление новой подборки
     *
     * @param adminNewCompilationParamDto Параметры новой подборки
     * @return Новая подборка CompilationDto
     */
    @Override
    public CompilationDto addCompilation(AdminNewCompilationParamDto adminNewCompilationParamDto) {

        // Проверка на null
        if (adminNewCompilationParamDto == null) {
            throw new IllegalArgumentException("Отсутствуют данные для новой подборки.");
        }

        // Проверки названия
        String title = adminNewCompilationParamDto.getTitle();
        if (title.isBlank()) {
            throw new IllegalArgumentException("В подборке отсутствует название.");
        }


        Set<EventShortDto> events = new HashSet<>();
        if (adminNewCompilationParamDto.getEvents() != null && !adminNewCompilationParamDto.getEvents().isEmpty()) {
            events = eventClient.getAllByIdIn(adminNewCompilationParamDto.getEvents());
        }
        Compilation compilation = CompilationMapper.toEntity(adminNewCompilationParamDto,events);
        Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toDto(saved, events);
    }

    /**
     * Удаление подборки по ID
     *
     * @param compId ID удаляемой подборки
     */
    @Override
    public void deleteCompilation(Long compId) {

        // Проверка на существование подборки
        validateCompilationExists(compId);
        compilationRepository.deleteById(compId);
    }

    /**
     * Обновление информации подборки с заданной ID на переданную информацию
     *
     * @param compId                         ID изменяемой подборки
     * @param adminUpdateCompilationParamDto Изменяемая информация
     * @return Измененная подборка CompilationDto
     */
    @Override
    public CompilationDto updateCompilation(Long compId, AdminUpdateCompilationParamDto adminUpdateCompilationParamDto) {
        // Проверка на существование подборки
        Compilation exitingCompilation = validateCompilationExists(compId);

        // Обновление полей подборки
        exitingCompilation.updateDetails(adminUpdateCompilationParamDto.getTitle(), adminUpdateCompilationParamDto.getPinned());

        // Обновляем список событий (если передан в запросе)
        Set<EventShortDto> events = new HashSet<>();
        if (adminUpdateCompilationParamDto.getEvents() != null) {
            events = eventClient.getAllByIdIn(adminUpdateCompilationParamDto.getEvents());
            exitingCompilation.replaceEvents(events.stream().map(EventShortDto::getId).collect(Collectors.toSet()));
        }

        compilationRepository.save(exitingCompilation);
        return CompilationMapper.toDto(exitingCompilation, events);

    }

    /**
     * Получение подборки по ID = complId
     *
     * @param complId ID подборки
     * @return CompilationDto
     */
    @Override
    public CompilationDto getCompilationById(long complId) {
        Compilation compilation = validateCompilationExists(complId);
        Set<EventShortDto> events = eventClient.getAllByIdIn(new ArrayList<>(compilation.getEvents()));
        return CompilationMapper.toDto(compilation, events);
    }

    /**
     * Получение подборок событий. Параметры звпроса: PublicCompilationRequestParamsDto
     *
     * @param params Входные параметры запроса PublicCompilationRequestParamsDto
     * @return List<CompilationDto>
     */
    @Override
    public List<CompilationDto> getCompilationsList(PublicCompilationRequestParamsDto params) {

        Pageable pageRequest = params.toPageable();

        List<Compilation> compilations;
        if (params.getPinned() != null) {
            compilations = compilationRepository.findAllByPinned(params.getPinned(), pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }

        Set<Long> eventIds = new HashSet<>();
        for (Compilation compilation : compilations) {
            // собираю множесто событий, задействованных в подборках
            eventIds.addAll(compilation.getEvents());
        }

        Map<Long,EventShortDto> eventShortDtoMap =
                eventClient.getAllByIdIn(new ArrayList<>(eventIds)).stream()
                        .collect(Collectors.toMap(EventShortDto::getId,
                                Function.identity()));

        return compilations.stream()
                .map(c -> CompilationMapper.toDto(c, eventShortDtoMap))
                .collect(Collectors.toList());
    }


    /**
     * Проверка переданного в поиск ID подборки
     *
     * @param compId ID подборки
     * @return Если существует, возвращается Compilation
     */
    public Compilation validateCompilationExists(Long compId) {
        log.warn("validateCompilationExists(Long {})", compId);
        // Проверка на null ID
        if (compId == null) {
            throw new EntityNotExistsException("ID подборки не может быть null");
        }

        // Проверка существования подборки
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotExistsException("Подборка с ID " + compId + " не найден"));
    }

}
