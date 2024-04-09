package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.webapp.form.NewCommentForm;
import ar.edu.itba.paw.webapp.form.NewPostForm;
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
    @RequestMapping(path="/comment", method = RequestMethod.POST)
    public ModelAndView newComment(@Valid @ModelAttribute("newCommentForm") final NewCommentForm newCommentForm, final BindingResult errors) {
        if(errors.hasErrors()) {
            return new ModelAndView("redirect:/post/"+newCommentForm.getPostId());
        }

        commentService.createComment(newCommentForm.getPostId(), newCommentForm.getBody());
        return new ModelAndView("redirect:/post/"+newCommentForm.getPostId());
    }




}
