package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.Comment;
import net.javaguides.sms.repository.CommentRepository;
import net.javaguides.sms.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> getCommentsByCourseId(Long courseId) {
        return commentRepository.findByCourseId(courseId);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
