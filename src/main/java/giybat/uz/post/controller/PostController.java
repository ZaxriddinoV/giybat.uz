package giybat.uz.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.post.dto.CreatePostDTO;
import giybat.uz.post.dto.FilterDTO;
import giybat.uz.post.service.PostService;
import giybat.uz.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/post")
@Tag(name = "Post Controller", description = "Post management")
public class PostController {
    @Autowired
    private PostService postService;


    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "user create post", description = "Users can create posts in the buyer.")
    public ResponseEntity<?> create(
            @RequestPart("dto") CreatePostDTO dto,
            @RequestPart("file") MultipartFile file) {
        ApiResponse<?> response = new ApiResponse<>(200, "success", postService.AddedPost(dto, file));
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update", description = "Edit Content or photo")
    public ResponseEntity<?> update(@RequestPart CreatePostDTO dto, @PathVariable Integer id,
                                    @RequestPart("file") MultipartFile file) {
        ApiResponse<?> response = new ApiResponse<>(200, "success", postService.update(dto, id, file));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "delete", description = "In Buyer, a User can delete their own post or an ADMIN can delete all users' posts.")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        ApiResponse<?> response = new ApiResponse<>(200, "success", postService.delete(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get By Id", description = "Post get By Id")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        ApiResponse<?> response = new ApiResponse<>(200, "success", postService.getPostId(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<?> pagination(@RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        ApiResponse<?> response = new ApiResponse<>(200, "succuess", postService.postAll(page - 1, size));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter", description = "filter (user:name,surname or content)")
    public ResponseEntity<?> filter(@RequestBody FilterDTO dto,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        ApiResponse<?> response = new ApiResponse<>(200, "success", postService.filter(dto, page - 1, size));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<?> myPosts(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        ApiResponse<?> response = new ApiResponse<>(200, "succuess", postService.myPosts(page - 1, size));
        return ResponseEntity.ok(response);
    }


    @ExceptionHandler({AppBadException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handle(AppBadException e) {
        ApiResponse<?> response = new ApiResponse<>(400, e.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }


}
