package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongOwnerException;
import ru.practicum.shareit.item.mapper.CommentListMapper;
import ru.practicum.shareit.item.mapper.ItemListMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplTest {
    final Long userId = 1L;
    final Long itemId = 1L;
    @Mock
    ItemMapper itemMapper;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    @Mock
    BookingService bookingService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentListMapper commentListMapper;
    @Mock
    ItemListMapper itemListMapper;
    @Mock
    ItemRequestService itemRequestService;
    @InjectMocks
    ItemServiceImpl service;
    User user1;
    Item item1;
    ItemDto itemDto;

    ItemResponseDto itemResponseDto;

    @BeforeEach
    void initialize() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("firstUserName");
        user1.setEmail("firstEmail@yandex.ru");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("firstItemName");
        item1.setDescription("firstItemDescription");
        item1.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("firstItemName");
        itemDto.setDescription("firstItemDescription");
        itemDto.setAvailable(true);

        itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("firstItemName");
        itemResponseDto.setDescription("firstItemDescription");
        itemResponseDto.setAvailable(true);
    }

    @Test
    void create_whenValidParams_thenReturnedItem() {
        when(userService.findById(userId)).thenReturn(user1);
        when(itemMapper.toItem(itemDto)).thenReturn(item1);
        when(itemRepository.save(item1)).thenReturn(item1);

        Item result = service.create(itemDto, userId);

        verify(userService, times(1)).findById(userId);
        verify(itemMapper, times(1)).toItem(itemDto);
        verify(itemRepository, times(1)).save(item1);

        assertEquals(item1, result);
    }


    @Test
    void update_whenTheOwnerUpdated_thenUpdateItem() {
        item1.setOwner(user1);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(itemRepository.save(item1)).thenReturn(item1);

        Item result = service.update(itemDto, itemId, userId);

        verify(itemRepository, times(1)).save(item1);

        assertEquals(item1, result);
    }

    @Test
    void update_whenTheNotOwnerUpdates_thenWrongOwnerThrown() {
        item1.setOwner(user1);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));

        WrongOwnerException e = assertThrows(WrongOwnerException.class,
                () -> service.update(itemDto, itemId, 2L));

        assertEquals("Wrong owner", e.getMessage());
    }


    @Test
    void findById_whenItemFound_thenReturnedItem() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));

        Item result = service.findById(itemId);

        verify(itemRepository, times(1)).findById(itemId);

        assertEquals(item1, result);
    }

    @Test
    void findById_whenItemNotFound_thenItemNotFoundThrown() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        ItemNotFoundException e = assertThrows(ItemNotFoundException.class,
                () -> service.findById(itemId));

        assertEquals("Item not found", e.getMessage());
    }

    @Test
    void search_whenTextIsFound_thenReturnedItemsList() {
        List<Item> items = List.of(item1, new Item());

        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(items);

        List<Item> result = service.search("text", 0, 10);

        assertEquals(items, result);
    }

    @Test
    void search_whenTextIsBlank_thenReturnedEmptyList() {
        List<Item> result = service.search("", 0, 10);

        assertEquals(0, result.size());
    }

    @Test
    void findByIdWithBookings() {
        item1.setOwner(user1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(itemMapper.toItemResponse(item1)).thenReturn(itemResponseDto);

        ItemResponseDto result = service.findByIdWithBookings(itemId, userId);

        assertEquals(itemResponseDto, result);
    }


    @Test
    void findAllByOwnerId() {
        service.findAllByOwnerId(1L, 0, 10);

        verify(itemRepository, times(1)).findAllByOwnerIdOrderById(anyLong(), any(Pageable.class));
    }


    @Test
    void addComment() {
        Comment comment = new Comment();
        comment.setText("text");
        comment.setItem(item1);
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());

        when(userService.findById(userId)).thenReturn(user1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(bookingService.hasUserBookedItem(userId, itemId)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = service.addComment(userId, itemId, comment);

        verify(userService, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingService, times(1)).hasUserBookedItem(userId, itemId);
        verify(commentRepository, times(1)).save(any(Comment.class));

        assertEquals(result, comment);

    }


    @Test
    void findAllByRequestId() {
        service.findAllByRequestId(1L);

        verify(itemRepository, times(1)).findAllByRequestId(1L);
        verify(itemListMapper, times(1)).toItemResponseDtoList(anyList());
    }
}