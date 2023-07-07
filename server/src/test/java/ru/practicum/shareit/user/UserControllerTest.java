package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserListMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;
    @MockBean
    UserMapper userMapper;
    @MockBean
    UserListMapper userListMapper;

    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setEmail("email@yandex.ru");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("userName");
        userDto.setEmail("email@yandex.ru");
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        List<UserDto> users = List.of(userDto, new UserDto());

        when(userService.findAll()).thenReturn(Collections.emptyList());
        when(userListMapper.toUserDtoList(Collections.emptyList())).thenReturn(users);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(users), result);
    }

    @SneakyThrows
    @Test
    void createUser() {
        UserDto userDtoToCreate = userDto;
        User userToCreate = user;

        when(userService.create(any())).thenReturn(userToCreate);
        when(userMapper.toUserDto(any())).thenReturn(userDtoToCreate);


        String result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToCreate), result);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserNameIsEmpty_thenReturnedBadRequest() {
        UserDto userToCreate = userDto;
        userToCreate.setName(null);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsEmpty_thenReturnedBadRequest() {
        UserDto userToCreate = userDto;
        userToCreate.setEmail(null);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsNotValid_thenReturnedBadRequest() {
        UserDto userToCreate = userDto;
        userToCreate.setEmail("emialyandex.com");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void getUser() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).findById(userId);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        UserDto userToUpdate = userDto;
        long userId = userToUpdate.getId();

        mockMvc.perform(patch("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk());

        verify(userMapper, times(1)).toUser(userToUpdate);
        verify(userService, times(1)).update(any(), anyLong());

    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId)).andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }
}