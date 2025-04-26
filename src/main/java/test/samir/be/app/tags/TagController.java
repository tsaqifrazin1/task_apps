package test.samir.be.app.tags;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.samir.be.app.common.response.WebResponse;
import test.samir.be.app.tags.model.FilterTagDTO;
import test.samir.be.app.tags.model.TagResponse;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class TagController {

    @Autowired
    TagService tagService;

    @GetMapping(
            path = "/api/tags"
    )
    public WebResponse<List<TagResponse>> findListTags(
            @RequestParam(required = false) String name){
        FilterTagDTO dto = new FilterTagDTO();
        dto.setName(name);

        List<TagResponse> taskResponses = tagService.findTags(dto);

        return WebResponse.
                <List<TagResponse>>
                builder()
                .data(taskResponses)
                .build();
    }
}
