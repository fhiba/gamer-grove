package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.webapp.form.NewCommunityForm;
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


    @RequestMapping(path="/community/{communityId}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityId") final long communityId) {
        ModelAndView mav = new ModelAndView("community/community");
        Community community = cs.findById(communityId);
        List<Post> posts = ps.getPostsByCommunity(community.getName());
        mav.addObject("community",community);
        mav.addObject("posts",posts);
        return mav;
    }

}
