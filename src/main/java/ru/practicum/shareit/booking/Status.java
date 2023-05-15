package ru.practicum.shareit.booking;

enum Status {
    WAITING, // новое бронирование, ожидает одобрения
    APPROVED, //бронирование подтверждено владельцем
    REJECTED, // бронирование отклонено владельцем
    CANCELED // бронирование отменено создателем
}
