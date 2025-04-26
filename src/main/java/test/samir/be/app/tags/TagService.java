package test.samir.be.app.tags;

import test.samir.be.app.tags.model.FilterTagDTO;
import test.samir.be.app.tags.model.TagResponse;

import java.util.List;

public interface TagService {
    List<TagResponse> findTags(FilterTagDTO dto);
}
