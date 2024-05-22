package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategories;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.PostDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class PostServiceImpl implements PostService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);


    @Autowired
    private PostDao postDao;

    @Autowired
    private UserService userService;
    @Autowired
    private CommunityService communityService;

    @Autowired
    private MailingService mailingService;
    @Autowired
    private FileService fs;

    @Override
    public List<Post> getAllPosts() {
        List<Post> posts = postDao.findAllPosts();
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }

    @Transactional
    @Override
    public Post createPost(final String title, final String body, final String communityName, final String category, final MultipartFile[] files) throws NoLoggedUserException, NoSuchCommunityException {
        Optional<User> maybeUser = userService.getLoggedUser();
        if(maybeUser.isEmpty())
            throw new NoLoggedUserException("User not logged");
        User user = maybeUser.get();
        Community community = communityService.findByName(communityName);
        Post post = postDao.createPost(title,body,user,community,false, LocalDateTime.now(), category);
        notifyUsers(post, user);
        for (MultipartFile file : files) {
            if (!file.isEmpty())
                fs.uploadPostImage(file, post.getId());
        }

        return post;
    }
    @Async
    void notifyUsers(Post post, User user) {
        Community community = post.getcommunity();
        List<User> users = community.getFollowers();
        mailingService.sendNewPostNotifications(users, post, user);
    }

    @Override
    public List<Post> getPostsByCommunity(final String communityName) {
        List<Post> posts = postDao.findPostsByCommunity(URLDecoder.decode(communityName, StandardCharsets.UTF_8));
        return posts.isEmpty()? Collections.emptyList(): posts;

    }

    @Override
    public PaginatedDataWrapper<Post> getPostsByCommunityPaginated(final String communityName, PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalPostByCommunityCount(URLDecoder.decode(communityName, StandardCharsets.UTF_8));
        if(totalCount == 0){
            return new PaginatedDataWrapper<>(Collections.emptyList(), request.getPageNumber(), totalCount, request.getPageSize());
        }


        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getPostsByCommunityPaginated(URLDecoder.decode(communityName, StandardCharsets.UTF_8),request.getPageSize() , offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }


    @Override
    public List<Post> getByCategory(String category) {
        List<Post> posts = postDao.findByCategory(category);
        return posts.isEmpty()? Collections.emptyList(): posts;
    }

    @Override
    public PaginatedDataWrapper<Post> getPostsByCategoryPaginated(String category, PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalPostByCategoryCount(category);
        if(totalCount == 0){
            return new PaginatedDataWrapper<>(Collections.emptyList(), request.getPageNumber(), totalCount, request.getPageSize());
        }

        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getAllPostsByCategoryPaginated(category,request.getPageSize() , offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public Post getPostById(long postId) throws NoSuchPostException{
        Optional<Post> post = postDao.findById(postId);
        if(post.isEmpty())
            throw new NoSuchPostException("Post with id:" + postId+ " not found");
        return post.get();
    }
    @Override
    public Post getPostByIdWithImage(long postId) throws NoSuchPostException{
        Optional<Post> post = postDao.findById(postId);
        if(post.isEmpty())
            throw new NoSuchPostException("Post with id:" + postId+ " not found");
        return post.get();
    }

    @Transactional
    @Override
    public void editGrooviness(int grooviness, long postId) throws NoSuchPostException, NoLoggedUserException {
        Optional<Post> post = postDao.findById(postId);
        User user = userService.getLoggedUser().orElseThrow(() -> new NoLoggedUserException("User not found"));
        if(post.isEmpty()) {
            throw new NoSuchPostException("Post not found");
        }
        //checks whether the user has already grooved the comment
        Optional<Boolean> isGroovy = postDao.checkGrooviness(postId, user.getId());
        if(isGroovy.isEmpty()) {
            postDao.insertIntoGroovyHistory(postId, user.getId(),(grooviness == 1));
            postDao.editGrooviness(postId,grooviness);
            return;
        }


        switch (grooviness){
            case 1:
                if(isGroovy.get()) {
                    postDao.deleteGrooviness(postId, user.getId());
                    postDao.editGrooviness(postId, -1);
                } else {
                    postDao.editGrooviness(postId,2);
                    postDao.updateGroovyHistory(postId, user.getId(), true);
                }
                break;
            case -1:
                if(isGroovy.get()) {
                    postDao.editGrooviness(postId,-2);
                    postDao.updateGroovyHistory(postId, user.getId(), false);
                }
                else {
                    postDao.deleteGrooviness(postId, user.getId());
                    postDao.editGrooviness(postId,1);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness value");
        }
    }

    @Override
    public int checkGrooviness(long postId) {
        Optional<Boolean> maybeGroovy = postDao.checkGrooviness(postId, userService.getLoggedUser().get().getId());
        return maybeGroovy.map(aBoolean -> aBoolean ? 1 : -1).orElse(0);
    }

    @Override
    public List<Post> getMyFollowedPosts(User user) {
        List<Post> posts = postDao.getMyFollowedPosts(user.getId());
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }
    @Override
    public PaginatedDataWrapper<Post> getUserFollowedPostsPaginated(long userId, PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotaltFollowedPostsByUserCount(userId);
        if(totalCount == 0){
            return new PaginatedDataWrapper<>(Collections.emptyList(), request.getPageNumber(), totalCount, request.getPageSize());
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getFollowedPostsByUserPaginated(userId,request.getPageSize() , offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public List<Post> getMyFollowedPostsByCategory(String category, User user) {
        List<Post> posts = postDao.getMyFollowedPostsByCategory(category,user.getId());
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }
    @Override
    public PaginatedDataWrapper<Post> getUserFollowedPostsByCategoryPaginated(String category, long userId, PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalUserFollowedPostsByCategoryCount(userId,category);
        if(totalCount == 0){
            return new PaginatedDataWrapper<>(Collections.emptyList(), request.getPageNumber(), totalCount, request.getPageSize());
        }

        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getUserFollowedPostsByCategoryPaginated(userId,category,request.getPageSize() , offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public List<Post> getPostsByUser(long id) {
        List<Post> posts = postDao.findPostsByUser(id);
        return posts.isEmpty()? Collections.emptyList(): posts;
    }

    @Override
    public List<Post> getUserLikedPosts(long id) {
        List<Post> posts = postDao.findPostsLikedByUser(id);
        return posts.isEmpty()? Collections.emptyList(): posts;
    }

    @Override
    public List<Post> getNewsLimited(int limit) {
        if(limit < 1){
            throw new IllegalArgumentException();
        }
        List<Post> posts = postDao.getNewsLimited(limit);
        return posts.isEmpty()? Collections.emptyList(): posts;
    }

    @Override
    public PaginatedDataWrapper<Post> getUserLikedPostsPaginated(long userId, PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalUserLikedPostCount(userId);
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getUserLikedPostPaginated(userId,request.getPageSize() , offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }


    @Override
    public List<String> getUsedCategories() {
        List<String> categories = postDao.getUsedCategories();

//        System.out.println("LAs CAT SON:");
//        for (PostCategories category : categories) {
//            System.out.println(category.getCategory());
//        }



//        List<String> categoryNames = categories.stream().map(PostCategories::getCategory).toList();
        return categories.isEmpty() ? Collections.emptyList() : categories;


//        return categoryNames.isEmpty() ? Collections.emptyList() : categoryNames;
    }

    @Override
    public PaginatedDataWrapper<Post> getAllPostsPaginated(PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalPostCount();
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }

        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getAllPostsPaginated(request.getPageSize(), offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public PaginatedDataWrapper<Post> getPostsByUserPaginated(long id, PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalPostsByUser(id);
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getPostsByUserPaginated(id,request.getPageSize(), offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }


}
