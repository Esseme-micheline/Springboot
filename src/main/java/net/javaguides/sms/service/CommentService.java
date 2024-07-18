package net.javaguides.sms.service;

import net.javaguides.sms.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByCourseId(Long courseId);
    Comment saveComment(Comment comment);
}