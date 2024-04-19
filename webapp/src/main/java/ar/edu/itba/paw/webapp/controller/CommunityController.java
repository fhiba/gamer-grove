package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategories;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.webapp.form.FollowCommunityForm;
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

import java.util.Arrays;
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
    public ModelAndView community(@PathVariable("communityName") final String communityName, @ModelAttribute("newPostForm") final NewPostForm newPostForm, @ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm) throws NoSuchCommunityException {
        ModelAndView mav = new ModelAndView("community/community");
        Community community = cs.findByName(communityName);
        List<Post> posts = ps.getPostsByCommunity(communityName);
        Boolean isFollowing = false;
        try {
            isFollowing = cs.checkIfUserFollowsCommunity((int)community.getId());
        } catch (NoLoggedUserException e) {
            //do nothing
        }
        mav.addObject("isFollowing",isFollowing);
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        mav.addObject("community",community);
        mav.addObject("posts",posts);
        return mav;
    }

    @RequestMapping(path="/community/{communityName}", method = RequestMethod.POST)
    public ModelAndView createPostOnCommunity(@PathVariable("communityName") final String communityName,@ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm, @ModelAttribute("newPostForm") final NewPostForm newPostForm,BindingResult errors) throws NoLoggedUserException, NoSuchCommunityException {

        if(errors.hasErrors())
            return community(communityName,newPostForm,followCommunityForm);

        ps.createPost(newPostForm.getTitle(),newPostForm.getBody(),communityName,newPostForm.getCategory());
        return new ModelAndView("redirect:/community/"+communityName);
    }



    @RequestMapping(path="/communities", method = RequestMethod.GET)
    public ModelAndView communities(@ModelAttribute("searchTerms") final String searchTerms) {
        ModelAndView mav = new ModelAndView("community/communities");
        List<Community> communities = cs.find(searchTerms);
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));
        mav.addObject("communities",communities);
        return mav;
    }

    @RequestMapping(path="/community/{communityName}/new", method = RequestMethod.POST)
    public ModelAndView newCommunityPost(@PathVariable("communityName") final String communityName,@ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm,@ModelAttribute("newPostForm") final NewPostForm newPostForm,BindingResult errors) throws NoSuchCommunityException, NoLoggedUserException {
        System.out.println(newPostForm.getCategory());
        System.out.println(newPostForm.getBody());
        System.out.println(newPostForm.getTitle());
        System.out.println(newPostForm.getCommunity());
        if(errors.hasErrors())
            return community(communityName,newPostForm,followCommunityForm);
        //chequeo de que exista la community
        Community community = cs.findByName(communityName);
        ps.createPost(newPostForm.getTitle(),newPostForm.getBody(),community.getName(),newPostForm.getCategory());
        return community(communityName,newPostForm,followCommunityForm);
    }

    @RequestMapping(path = "/community/{communityName}/follow", method = RequestMethod.POST)
    public ModelAndView followCommunity(@PathVariable("communityName") final String communityName, @ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm, BindingResult errors) throws NoSuchCommunityException, NoLoggedUserException {
        if(errors.hasErrors())
            return community(communityName,new NewPostForm(),followCommunityForm);
        cs.modifyUserOnCommunity(followCommunityForm.getCommunityId(),followCommunityForm.getCommunityName());
        return new ModelAndView("redirect:/community/"+communityName);
    }
}
