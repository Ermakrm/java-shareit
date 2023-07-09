package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring", uses = RequestMapper.class)
public interface RequestListMapper {

    List<ItemRequestResponseDto> toResponseDtoList(List<ItemRequest> items);
}
