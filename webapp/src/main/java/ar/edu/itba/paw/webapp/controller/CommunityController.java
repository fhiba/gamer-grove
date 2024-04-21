package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.FileService;
import ar.edu.itba.paw.services.PostService;
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
import java.util.Arrays;
import java.util.List;

@Controller
public class CommunityController {

    @Autowired
    private CommunityService cs;
    @Autowired
    private PostService ps;
    @Autowired
    private FileService fs;

    @RequestMapping(path="/new-community", method = RequestMethod.GET)
    public ModelAndView newCommunity(@ModelAttribute("newCommunityForm") final NewCommunityForm newCommunityForm) {
        return new ModelAndView("community/newCommunity").addObject("categories", Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory).toArray(String[]::new));
    }

    @RequestMapping(path="/new-community", method = RequestMethod.POST)
    public ModelAndView createCommunity(@Valid @ModelAttribute("newCommunityForm") final NewCommunityForm newCommunityForm, BindingResult errors) throws NoSuchCommunityException{
        if(errors.hasErrors())
            return new ModelAndView("community/newCommunity");
        System.out.println(newCommunityForm.getCategories());
        cs.createCommunity(newCommunityForm.getName(), newCommunityForm.getDescription(), newCommunityForm.getCategories());
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(path="/community/{communityName}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityName") final String communityName, @ModelAttribute("newPostForm") final NewPostForm newPostForm) throws NoSuchCommunityException {
        ModelAndView mav = new ModelAndView("community/community");
        Community community = cs.findByName(communityName);
        List<Post> posts = ps.getPostsByCommunity(communityName);
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        mav.addObject("community",community);
        mav.addObject("posts",posts);
        return mav;
    }

    @RequestMapping(path="/community/{communityName}", method = RequestMethod.POST)
    public ModelAndView createPostOnCommunity(@PathVariable("communityName") final String communityName, @ModelAttribute("newPostForm") final NewPostForm newPostForm,BindingResult errors) throws NoLoggedUserException, NoSuchCommunityException {

        if(errors.hasErrors())
            return community(communityName,newPostForm);

        ps.createPost(newPostForm.getTitle(),newPostForm.getBody(),communityName,newPostForm.getCategory());
        return new ModelAndView("redirect:/community/"+communityName);
    }



    @RequestMapping(path="/communities", method = RequestMethod.GET)
    public ModelAndView communities(@ModelAttribute("searchTerms") final String searchTerms, @ModelAttribute("categories") final String categories) {
        ModelAndView mav = new ModelAndView("community/communities");
        List<String> selectedCategories = Arrays.asList(categories.split(","));
        List<Community> communities = cs.find(searchTerms, selectedCategories);
        mav.addObject("categories", Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory).toArray(String[]::new));
        mav.addObject("selectedCategories", selectedCategories);
        mav.addObject("searchTerms", searchTerms);
        mav.addObject("communities",communities);
        return mav;
    }

    @RequestMapping(path="/community/{communityName}/new", method = RequestMethod.POST)
    public ModelAndView newCommunityPost(@PathVariable("communityName") final String communityName,@ModelAttribute("newPostForm") final NewPostForm newPostForm,BindingResult errors) throws NoSuchCommunityException, NoLoggedUserException {
        System.out.println(newPostForm.getCategory());
        System.out.println(newPostForm.getBody());
        System.out.println(newPostForm.getTitle());
        System.out.println(newPostForm.getCommunity());
        if(errors.hasErrors())
            return community(communityName,newPostForm);
        //chequeo de que exista la community
        Community community = cs.findByName(communityName);
        ps.createPost(newPostForm.getTitle(),newPostForm.getBody(),community.getName(),newPostForm.getCategory());
        return community(communityName,newPostForm);
    }

    @RequestMapping(path="/community/{communityName}/image", method = RequestMethod.GET)
    public ModelAndView communityImage(@PathVariable("communityName") final String communityName, @ModelAttribute("newCommunityImage") final FileForm newCommunityImage) throws NoSuchCommunityException {
        ModelAndView mav = new ModelAndView("community/communityImage");
        Community community = cs.findByName(communityName);
        mav.addObject("community",community);
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
        ModelAndView mav = new ModelAndView("image");
//        mav.addObject("image",fs.getFile(imageId).get());
        return mav;
    }

    @RequestMapping(value = "/image/{imageId}", method = RequestMethod.GET,
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public byte[] getImage(@PathVariable Integer imageId) {
        //File image = fs.getFile(doctorId).orElse(null);
        return fs.getFile(imageId).map(File::getFile).orElse(null);
    }

}
