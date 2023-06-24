package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = RequestMapper.class)
public interface RequestListMapper {

    List<ItemRequestResponseDto> toResponseDtoList(List<ItemRequest> items);
}
