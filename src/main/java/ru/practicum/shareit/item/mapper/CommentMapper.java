package ru.practicum.shareit.item.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

@Component
@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorName", source = "comment.author.name")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    CommentResponseDto toCommentResponseDto(Comment comment);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Comment toComment(CommentRequestDto commentRequestDto);
}
