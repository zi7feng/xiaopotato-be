package com.fzq.xiaopotato.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.post.PostUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.fzq.xiaopotato.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "Create a new post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": 123,
                      "message": "ok",
                      "description": "Post created successfully."
                    }
                """)))
    })
    @PostMapping("/create")
    public BaseResponse<Long> createPost(@RequestBody PostCreateDTO postCreateDTO, HttpServletRequest request) {
        if (postCreateDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Post info is null");
        }
        return ResultUtils.success(postService.postCreate(postCreateDTO, request));
    }

    @Operation(summary = "List posts with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts listed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": {
                        "current": 1,
                        "size": 10,
                        "total": 100,
                        "records": [
                          {
                            "id": 123,
                            "postTitle": "Sample Post",
                            "postContent": "This is a sample post content."
                          }
                        ]
                      },
                      "message": "ok",
                      "description": ""
                    }
                """)))
    })
    @GetMapping("/selectByPage")
    public BaseResponse<IPage<Post>> listPostByPage(PostQueryDTO postQueryDTO, HttpServletRequest request) {
        if (postQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (postQueryDTO.getCurrentPage() <= 0 || postQueryDTO.getPageSize() <= 0) {
            postQueryDTO.setCurrentPage(1);
            postQueryDTO.setPageSize(10);
        }
        IPage<Post> result = postService.listPostByPage(postQueryDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get post details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": {
                        "id": 123,
                        "postTitle": "Sample Post",
                        "postContent": "This is a sample post content."
                      },
                      "message": "ok",
                      "description": ""
                    }
                """)))
    })
    @GetMapping("/selectById")
    public BaseResponse<Post> getPostById(IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(postService.selectPostById(idDTO, request));
    }

    @Operation(summary = "Update a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": true,
                      "message": "ok",
                      "description": "Post updated successfully."
                    }
                """)))
    })
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePostById(@RequestBody PostUpdateDTO postUpdateDTO, HttpServletRequest request) {

        boolean result = postService.updatePostById(postUpdateDTO, request);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update post fail.");
        }
        return ResultUtils.success(true);
    }

    @Operation(summary = "Delete a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": true,
                      "message": "ok",
                      "description": "Post deleted successfully."
                    }
                """)))
    })
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePostById(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(postService.deletePostById(idDTO, request));
    }

    @Operation(summary = "List posts by user ID with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's posts listed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": {
                        "current": 1,
                        "size": 10,
                        "total": 50,
                        "records": [
                          {
                            "id": 123,
                            "postTitle": "User Post",
                            "postContent": "This is a user-specific post content."
                          }
                        ]
                      },
                      "message": "ok",
                      "description": ""
                    }
                """)))
    })
    @GetMapping("/selectByUserId")
    public BaseResponse<IPage<Post>> listPostByUserId(PostQueryDTO postQueryDTO, HttpServletRequest request) {
        if (postQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (postQueryDTO.getCurrentPage() <= 0 || postQueryDTO.getPageSize() <= 0) {
            postQueryDTO.setCurrentPage(1);
            postQueryDTO.setPageSize(10);
        }
        IPage<Post> result = postService.listPostByUserId(postQueryDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get Like Count for a Post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved like count for the post",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                        {
                          "code": 200,
                          "data": 15,
                          "message": "ok",
                          "description": "Number of likes for the specified post."
                        }
                """)))
    })
    @GetMapping("/getLikedCount")
    public BaseResponse<Integer> getLikedCount(IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "post id is null");
        }
        return ResultUtils.success(postService.getLikedCount(idDTO, request));
    }

    @Operation(summary = "Get Save Count for a Post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved save count for the post",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                        {
                          "code": 200,
                          "data": 10,
                          "message": "ok",
                          "description": "Number of times the specified post has been saved."
                        }
                """)))
    })
    @GetMapping("/getSavedCount")
    public BaseResponse<Integer> getSavedCount(IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "post id is null");
        }
        return ResultUtils.success(postService.getSavedCount(idDTO, request));
    }
}
