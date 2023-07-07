package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentListMapper {
    List<CommentResponseDto> toCommentResponseList(List<Comment> comments);
}
