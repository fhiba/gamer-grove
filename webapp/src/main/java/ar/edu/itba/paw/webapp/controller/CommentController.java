package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.webapp.form.CommentDeleteForm;
import ar.edu.itba.paw.webapp.form.NewCommentForm;
import ar.edu.itba.paw.webapp.form.NewCommentGroovyForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;




    @RequestMapping(path="/post/{postId}/+" , method = RequestMethod.POST)
    public ModelAndView editGroovinessOnComment(@Valid @ModelAttribute("newCommentGroovyForm") final NewCommentGroovyForm newCommentGroovyForm, final BindingResult errors) throws UserNotFoundException, NoSuchCommentException, NoLoggedUserException, NoSuchPostException {
        if(errors.hasErrors()) {
            return new ModelAndView("redirect:/post/"+newCommentGroovyForm.getCommentPostId());
        }

        commentService.editGroovinessOnComment(newCommentGroovyForm.getCommentId(), newCommentGroovyForm.isGroovyType() ? 1 : -1, newCommentGroovyForm.getCommentPostId());
        return new ModelAndView("redirect:/post/"+newCommentGroovyForm.getCommentPostId());
    }

    @RequestMapping(path="/comment/{postId}/delete", method = RequestMethod.POST)
    public ModelAndView deleteComment(@Valid @ModelAttribute("commentDeleteForm") final CommentDeleteForm commentDeleteForm, final BindingResult errors) throws NoSuchCommentException, PostIsDeletedException, NoSuchPostException {
        if(errors.hasErrors()) {
            return new ModelAndView("redirect:/post/{postId}");
        }
        commentService.deleteComment(commentDeleteForm.getCommentId());
        return new ModelAndView("redirect:/post/{postId}");
    }

}
