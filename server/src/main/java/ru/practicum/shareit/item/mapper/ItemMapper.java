package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")

public interface ItemMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toItemDto(Item item);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Item toItem(ItemDto itemDto);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "requestId", source = "item.request.id")
    ItemResponseDto toItemResponse(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void updateItemFromDto(ItemDto dto, @MappingTarget Item entity);
}
