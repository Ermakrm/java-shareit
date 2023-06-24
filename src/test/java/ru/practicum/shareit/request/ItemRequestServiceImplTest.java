package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.mapper.RequestListMapper;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {
    final LocalDateTime created = LocalDateTime.now();
    final Long userId = 1L;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserService userService;
    @Mock
    RequestMapper requestMapper;
    @Mock
    ItemService itemService;
    @Mock
    RequestListMapper requestListMapper;
    @InjectMocks
    ItemRequestServiceImpl service;
    User user1;
    ItemRequest request;


    @BeforeEach
    public void initialize() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("firstUserName");
        user1.setEmail("firstEmail@yandex.ru");

        request = new ItemRequest();
        request.setId(1L);
        request.setCreated(created);
        request.setRequester(user1);
    }

    @Test
    void save_whenValid_thenSavedRequest() {
        ItemRequestRequestDto req = new ItemRequestRequestDto();

        when(itemRequestRepository.save(request)).thenReturn(request);
        when(userService.findById(anyLong())).thenReturn(user1);
        when(requestMapper.toItemRequest(any())).thenReturn(request);

        ItemRequest result = service.save(req, userId);

        verify(itemRequestRepository, times(1)).save(request);
        verify(userService, times(1)).findById(anyLong());
        verify(requestMapper, times(1)).toItemRequest(any());

        assertEquals(request, result);
    }


    @Test
    void findByUserId_whenUserAndRequestFound_thenReturnedRequest() {
        List<ItemRequestResponseDto> req = List.of(new ItemRequestResponseDto());

        when(requestListMapper.toResponseDtoList(any())).thenReturn(req);
        when(userService.findById(anyLong())).thenReturn(user1);
        when(itemRequestRepository.findAllByRequesterId(userId)).thenReturn(List.of(request));

        List<ItemRequestResponseDto> result = service.findByUserId(userId);

        verify(userService, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findAllByRequesterId(userId);
        verify(requestListMapper, times(1)).toResponseDtoList(anyList());

        assertEquals(req, result);
    }


    @Test
    void findById_whenRequestFound_thenReturnedRequest() {
        ItemRequestResponseDto req = new ItemRequestResponseDto();
        req.setId(1L);
        req.setDescription("description");

        when(userService.findById(userId)).thenReturn(user1);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(requestMapper.toItemRequestResponseDto(request)).thenReturn(req);

        ItemRequestResponseDto result = service.findById(1L, userId);

        verify(userService, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(requestMapper, times(1)).toItemRequestResponseDto(request);

        assertEquals(req, result);
    }

    @Test
    void findById_whenRequestNotFound_thenRequestNotFoundThrown() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        RequestNotFoundException e = assertThrows(RequestNotFoundException.class,
                () -> service.findById(1L, 1L));
        assertEquals("Request with id 1 not found", e.getMessage());
    }

    @Test
    void getItemRequestById_whenRequestFound_thenReturnedRequest() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequest result = service.getItemRequestById(1L);

        verify(itemRequestRepository, times(1)).findById(1L);

        assertEquals(request, result);
    }


    @Test
    void getItemRequestById_whenRequestNotFound_thenRequestNotFoundThrown() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        RequestNotFoundException e = assertThrows(RequestNotFoundException.class,
                () -> service.getItemRequestById(1L));

        assertEquals("Request with id 1 not found", e.getMessage());
    }

    @Test
    void findAllWithPagination() {
        List<ItemRequestResponseDto> requests = new ArrayList<>();
        ItemRequestResponseDto req = new ItemRequestResponseDto();
        req.setId(1L);
        req.setDescription("description");
        requests.add(req);
        requests.add(new ItemRequestResponseDto());

        when(userService.findById(userId)).thenReturn(user1);
        when(itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(0, 10)))
                .thenReturn(List.of(request, new ItemRequest()));
        when(requestListMapper.toResponseDtoList(anyList())).thenReturn(requests);

        List<ItemRequestResponseDto> result = service.findAllWithPagination(userId, 0, 10);

        verify(userService, times(1)).findById(userId);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdNot(userId, PageRequest.of(0, 10));

        assertEquals(requests, result);
    }

}