package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.webapp.form.NewCommunityForm;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class CommunityController {

    @Autowired
    private CommunityService cs;
    @Autowired
    private PostService ps;

    @RequestMapping(path="/new-community", method = RequestMethod.GET)
    public ModelAndView newCommunity(@ModelAttribute("newCommunityForm") final NewCommunityForm newCommunityForm) {
        return new ModelAndView("community/newCommunity");
    }

    @RequestMapping(path="/new-community", method = RequestMethod.POST)
    public ModelAndView createCommunity(@ModelAttribute("newCommunityForm") final NewCommunityForm newCommunityForm, BindingResult errors) {
        if(errors.hasErrors())
            return new ModelAndView("community/newCommunity");
        cs.createCommunity(newCommunityForm.getName(), newCommunityForm.getDescription());
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(path="/community/{communityName}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityName") final String communityName) {
        ModelAndView mav = new ModelAndView("community/community");
        Community community = cs.findByName(communityName);
        List<Post> posts = ps.getPostsByCommunity(communityName);
        mav.addObject("community",community);
        mav.addObject("posts",posts);
        return mav;
    }

    @RequestMapping(path="/communities", method = RequestMethod.GET)
    public ModelAndView communities(@ModelAttribute("searchTerms") final String searchTerms) {
        ModelAndView mav = new ModelAndView("community/communities");
        List<Community> communities = cs.find(searchTerms);
        mav.addObject("communities",communities);
        return mav;
    }

    @RequestMapping(path="/community/{communityName}/new", method = RequestMethod.POST)
    public ModelAndView newCommunityPost(@PathVariable("communityName") final String communityName,@ModelAttribute("newPostForm") final NewPostForm newPostForm,BindingResult errors) {
        if(errors.hasErrors())
            return community(communityName);
        //chequeo de que exista la community
        Community community = cs.findByName(communityName);
        ps.createPost(newPostForm.getTitle(),newPostForm.getBody(),community.getName(),newPostForm.getCategory());
        return community(communityName);
    }

}
