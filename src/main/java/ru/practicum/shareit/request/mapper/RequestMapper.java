package ru.practicum.shareit.request.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
@Mapper(componentModel = "spring")
public interface RequestMapper {
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    ItemRequest toItemRequest(ItemRequestRequestDto item);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest);
}
