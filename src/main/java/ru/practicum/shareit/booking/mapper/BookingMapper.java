package ru.practicum.shareit.booking.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    BookingResponseDto toBookingResponse(Booking booking);

    @Mapping(target = "item.id", source = "dto.itemId")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Booking toBooking(BookingRequestDto dto);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    BookingInfoDto toBookingInfo(Booking booking);
}
