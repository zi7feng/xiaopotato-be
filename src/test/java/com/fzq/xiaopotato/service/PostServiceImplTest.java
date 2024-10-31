//package com.fzq.xiaopotato.service;
//
//import com.fzq.xiaopotato.mapper.PostMapper;
//import com.fzq.xiaopotato.mapper.PosttagMapper;
//import com.fzq.xiaopotato.mapper.TagMapper;
//import com.fzq.xiaopotato.mapper.UserPostMapper;
//import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
//import com.fzq.xiaopotato.model.vo.UserVO;
//import com.fzq.xiaopotato.service.UserService;
//import com.fzq.xiaopotato.service.impl.PostServiceImpl;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import static org.mockito.Mockito.when;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//
//@ExtendWith(MockitoExtension.class)
//public class PostServiceImplTest {
//
//    @Autowired
//    private PostServiceImpl postService;
//
//    @Autowired
//    private PostMapper postMapper;
//
//    @MockBean
//    private UserService userService;
//
//    @Autowired
//    private UserPostMapper userPostMapper;
//
//    @MockBean
//    private HttpServletRequest request;
//
//    private UserVO mockUser;
//
//    @Autowired
//    private TagMapper tagMapper;
//
//    @Autowired
//    private PosttagMapper posttagMapper;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        // 创建一个模拟用户
//        mockUser = new UserVO();
//        mockUser.setId(1L);
//        when(userService.getCurrentUser(request)).thenReturn(mockUser);
//    }
//
//    @Test
//    public void testBulkPostCreate() {
//
//            // 定义要插入的帖子数据
//            List<PostCreateDTO> posts = new ArrayList<>();
//            PostCreateDTO post1 = new PostCreateDTO();
//            post1.setPostTitle("The Tranquil Beauty of Watercolor Landscapes");
//            post1.setPostContent("Watercolor landscapes capture serene scenes with soft edges and subtle blends. This technique brings out the tranquility of nature, from misty mountains to calm lakes. Have you tried watercolor landscapes? #Watercolor #Landscapes");
//            post1.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/watercolor.jpg");
//            post1.setPostGenre("Painting");
//            posts.add(post1);
//
//            PostCreateDTO post2 = new PostCreateDTO();
//            post2.setPostTitle("Sculpting in Clay: From Idea to Reality");
//            post2.setPostContent("Sculpting is an art form that begins with a vision and evolves through the shaping of clay. Each piece carries its own story, molded by the artist’s hands. Share your latest clay creations and your inspiration behind them! #Sculpture #ClayArt");
//            post2.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/clay.jpg");
//            post2.setPostGenre("Painting");
//            posts.add(post2);
//
//            PostCreateDTO post3 = new PostCreateDTO();
//            post3.setPostTitle("Abstract Art: Expressing Emotion through Colors and Shapes");
//            post3.setPostContent("Abstract art lets artists convey emotions without strict form, using colors, lines, and textures to communicate. What emotions do you aim to express in your abstract pieces? #AbstractArt #Expression");
//            post3.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/abstract.jpg");
//            post3.setPostGenre("Painting");
//            posts.add(post3);
//
//            PostCreateDTO post4 = new PostCreateDTO();
//            post4.setPostTitle("Capturing Urban Life through Street Photography");
//            post4.setPostContent("Street photography brings cities to life, capturing the beauty in everyday scenes and people. It’s spontaneous, raw, and tells stories of urban landscapes. Do you have a favorite shot from your city? #StreetPhotography #CityLife");
//            post4.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/street.jpg");
//            post4.setPostGenre("Photo");
//            posts.add(post4);
//
//            PostCreateDTO post5 = new PostCreateDTO();
//            post5.setPostTitle("Exploring the World of Digital Illustration");
//            post5.setPostContent("Digital art opens limitless possibilities, from fantasy landscapes to realistic portraits. What digital tools do you use, and how do they shape your art? Show us your latest digital pieces! #DigitalArt #Illustration");
//            post5.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/digital.jpg");
//            post5.setPostGenre("Painting");
//            posts.add(post5);
//
//            PostCreateDTO post6 = new PostCreateDTO();
//            post6.setPostTitle("Art Nouveau: Celebrating Nature with Flowing Lines");
//            post6.setPostContent("Inspired by natural forms, Art Nouveau brings elegance to art through flowing lines and floral designs. Have you experimented with this style? #ArtNouveau #FloralDesign");
//            post6.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/lines.jpg");
//            post6.setPostGenre("Painting");
//            posts.add(post6);
//
//            PostCreateDTO post7 = new PostCreateDTO();
//            post7.setPostTitle("The Power of Monochrome Photography");
//            post7.setPostContent("Black-and-white photos can convey powerful contrasts and emotions. Stripping away color, monochrome photography highlights textures, light, and shadow. Share your favorite monochrome shots! #Monochrome #BlackAndWhite");
//            post7.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/monochrome.jpg");
//            post7.setPostGenre("Photo");
//            posts.add(post7);
//
//            PostCreateDTO post8 = new PostCreateDTO();
//            post8.setPostTitle("Street Art: Colorful Expressions on City Walls");
//            post8.setPostContent("Street art transforms walls into vibrant canvases, reflecting culture, politics, and social issues. Do you have favorite street art in your city, or have you created some yourself? #StreetArt #UrbanExpression");
//            post8.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/citywalls.jpg");
//            post8.setPostGenre("Painting");
//            posts.add(post8);
//
//            PostCreateDTO post9 = new PostCreateDTO();
//            post9.setPostTitle("Oil Painting: Rich Textures and Deep Colors");
//            post9.setPostContent("Oil paints allow artists to create vivid textures and layers, giving depth to the artwork. It’s a technique rich in tradition and full of potential. Post your recent oil paintings here! #OilPainting #ArtTechnique");
//            post9.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/oilpainting.jpg");
//            post9.setPostGenre("Painting");
//            posts.add(post9);
//
//            PostCreateDTO post10 = new PostCreateDTO();
//            post10.setPostTitle("Life in Miniature: The Art of Tiny Worlds");
//            post10.setPostContent("Miniature art captures entire scenes in a tiny space, full of incredible detail. These pieces take patience and precision. Do you create miniatures? #MiniatureArt #TinyWorlds");
//            post10.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/tiny.jpg");
//            post10.setPostGenre("Painting");
//            posts.add(post10);
//
//            PostCreateDTO post11 = new PostCreateDTO();
//            post11.setPostTitle("Ink and Wash: Combining Control and Flow");
//            post11.setPostContent("The art of ink and wash blends controlled lines with free-flowing ink washes. It’s a style full of character and contrast. Show us your recent ink and wash work! #InkArt #WashTechnique");
//            post11.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/ink.jpg");
//            post11.setPostGenre("Painting");
//            posts.add(post11);
//
//            PostCreateDTO post12 = new PostCreateDTO();
//            post12.setPostTitle("Impressionism: Capturing Moments in Brushstrokes");
//            post12.setPostContent("Impressionism brings scenes to life with quick brushstrokes and vibrant colors, capturing the essence of the moment. Share your impressionist-inspired art and the scenes that inspire you! #Impressionism #Brushstrokes");
//            post12.setPostImage("https://fzqqq-test.oss-us-east-1.aliyuncs.com/brushstrokes.jpg");
//            post12.setPostGenre("Painting");
//            posts.add(post12);
//
//        for (PostCreateDTO postCreateDTO : posts) {
//            Long postId = postService.postCreate(postCreateDTO, request);
////            assertNotNull(postId, "Post ID should not be null after insertion.");
//        }
//    }
//}
