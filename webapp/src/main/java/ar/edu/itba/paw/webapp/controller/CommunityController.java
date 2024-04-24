package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.FollowCommunityForm;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.FileForm;
import ar.edu.itba.paw.webapp.form.NewCommunityForm;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class CommunityController {

    @Autowired
    private CommunityService cs;
    @Autowired
    private PostService ps;
    @Autowired
    private FileService fs;
    @Autowired
    private ModderService ms;
    @Autowired
    private UserService us;

    @RequestMapping(path="/new-community", method = RequestMethod.GET)
    public ModelAndView newCommunity(@ModelAttribute("newCommunityForm") final NewCommunityForm newCommunityForm) {
        ModelAndView mav = new ModelAndView("community/newCommunity");
        User user = null;
        List<Community> communities;
        Boolean isAdmin = false;
        try{
            user = us.getLoggedUserChecked();
        }catch (Exception ignored){

        }
        if(user != null) {
            communities = cs.getFollowedCommunities(user);
            isAdmin = us.isUserAdmin(user.getId());
        }else{
            communities = cs.getAllCommunitiesNoCat();
        }
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("isLogged", user != null);
        mav.addObject("communities",communities);
        mav.addObject("categories", Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path="/new-community", method = RequestMethod.POST)
    public ModelAndView createCommunity(@Valid @ModelAttribute("newCommunityForm") final NewCommunityForm newCommunityForm, BindingResult errors) throws NoSuchCommunityException{
        if(errors.hasErrors())
            return newCommunity(newCommunityForm);
        System.out.println(newCommunityForm.getCategories());
        cs.createCommunity(newCommunityForm.getName(), newCommunityForm.getDescription(), newCommunityForm.getCategories(), newCommunityForm.getDeveloper(),newCommunityForm.getPublisher(), LocalDateTime.now());
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(path="/community/{communityName}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityName") final String communityName, @ModelAttribute("newPostForm") final NewPostForm newPostForm, @ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm) throws NoSuchCommunityException {
        ModelAndView mav = new ModelAndView("community/community");
        Community community = cs.findByName(communityName);
        List<Post> posts = ps.getPostsByCommunity(communityName);
        Boolean isAdmin = false;
        Boolean isFollowing = false;
        try {
            isFollowing = cs.checkIfUserFollowsCommunity((int)community.getId());
        } catch (NoLoggedUserException e) {
            //do nothing
        }
        User user = null;
        List<Community> communities;
        try{
            user = us.getLoggedUserChecked();
            isAdmin = us.isUserAdmin(user.getId());
        }catch (Exception ignored){

        }

        if(user != null)
            communities = cs.getFollowedCommunities(user);
        else {
            communities = cs.getAllCommunitiesNoCat();
        }
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("isLogged", user != null);
        mav.addObject("communities", communities);
        mav.addObject("isFollowing",isFollowing);
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        mav.addObject("community",community);
        mav.addObject("posts",posts);
        Optional<User> possiblyUser = us.getLoggedUser();
        boolean canEdit = false;
        if(possiblyUser.isPresent()){
            canEdit = ms.isModderOfCommunity(possiblyUser.get().getId(),community.getId());
        }
        mav.addObject("canEdit",canEdit);
        return mav;
    }

    @RequestMapping(path="/community/{communityName}", method = RequestMethod.POST)
    public ModelAndView createPostOnCommunity(@PathVariable("communityName") final String communityName,@ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm,@Valid @ModelAttribute("newPostForm") final NewPostForm newPostForm,BindingResult errors) throws NoLoggedUserException, NoSuchCommunityException {

        if(errors.hasErrors())
            return community(communityName,newPostForm,followCommunityForm);

        ps.createPost(newPostForm.getTitle(),newPostForm.getBody(),communityName,newPostForm.getCategory(),newPostForm.getFiles());
        return new ModelAndView("redirect:/community/"+communityName);
    }



    @RequestMapping(path="/communities", method = RequestMethod.GET)
    public ModelAndView communities(@ModelAttribute("searchTerms") final String searchTerms, @ModelAttribute("categories") final String categories) {
        ModelAndView mav = new ModelAndView("community/communities");
        List<String> selectedCategories = Arrays.asList(categories.split(","));
        List<Community> communities = cs.find(searchTerms, selectedCategories);
        Boolean isAdmin = false;
        User user = null;
        List<Community> followedCommunities;
        try{
            user = us.getLoggedUserChecked();
            isAdmin = us.isUserAdmin(user.getId());
        }catch (Exception ignored){

        }

        if(user != null)
            followedCommunities = cs.getFollowedCommunities(user);
        else {
            followedCommunities = cs.getAllCommunitiesNoCat();
        }
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("isLogged",user != null);
        mav.addObject("categories", Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory).toArray(String[]::new));
        mav.addObject("selectedCategories", selectedCategories);
        mav.addObject("searchTerms", searchTerms);
        mav.addObject("communities",communities);
        mav.addObject("followedCommunities",followedCommunities);
        return mav;
    }

    @RequestMapping(path="/community/{communityName}/new", method = RequestMethod.POST)
    public ModelAndView newCommunityPost(@PathVariable("communityName") final String communityName,@ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm,@ModelAttribute("newPostForm") final NewPostForm newPostForm,BindingResult errors) throws NoSuchCommunityException, NoLoggedUserException {
        if(errors.hasErrors())
            return community(communityName,newPostForm,followCommunityForm);
        //chequeo de que exista la community
        Community community = cs.findByName(communityName);
        ps.createPost(newPostForm.getTitle(),newPostForm.getBody(),community.getName(),newPostForm.getCategory(),newPostForm.getFiles());
        return community(communityName,newPostForm,followCommunityForm);
    }

    @RequestMapping(path="/community/{communityName}/image", method = RequestMethod.GET)
    public ModelAndView communityImage(@PathVariable("communityName") final String communityName, @ModelAttribute("newCommunityImage") final FileForm newCommunityImage) throws NoSuchCommunityException {
        ModelAndView mav = new ModelAndView("community/communityImage");
        Community community = cs.findByName(communityName);
        mav.addObject("community",community);
        mav.addObject("communities",cs.getAllCommunities());
        return mav;
    }

    @RequestMapping(path="/community/{communityName}/image", method = RequestMethod.POST)
    public ModelAndView uploadCommunityImage(@PathVariable("communityName") final String communityName, @Valid @ModelAttribute("newCommunityImage") final FileForm newCommunityImage, BindingResult errors) throws NoSuchCommunityException {
        if(errors.hasErrors())
            return communityImage(communityName,newCommunityImage);
        fs.uploadCommunityImage(communityName,newCommunityImage.getFile());
        return new ModelAndView("redirect:/community/"+communityName);
    }

    @RequestMapping(path="/seeImages/{imageId}", method = RequestMethod.GET)
    public ModelAndView getImage(@PathVariable("imageId") final long imageId) {
        //        mav.addObject("image",fs.getFile(imageId).get());
        return new ModelAndView("image");
    }

    @RequestMapping(value = "/image/{imageId}", method = RequestMethod.GET,
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public byte[] getImage(@PathVariable Integer imageId) {
        //File image = fs.getFile(doctorId).orElse(null);
        return fs.getFile(imageId).map(File::getFile).orElse(null);
    }

    @RequestMapping(path = "/community/{communityName}/follow", method = RequestMethod.POST)
    public ModelAndView followCommunity(@PathVariable("communityName") final String communityName, @ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm, BindingResult errors) throws NoSuchCommunityException, NoLoggedUserException {
        if(errors.hasErrors())
            return community(communityName,new NewPostForm(),followCommunityForm);
        cs.modifyUserOnCommunity(followCommunityForm.getCommunityId(),followCommunityForm.getCommunityName());
        return new ModelAndView("redirect:/community/"+communityName);
    }
}
