package taehyeon.com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taehyeon.com.blog.entity.Neighbor;
import taehyeon.com.blog.repository.NeighborRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NeighborService {

    private final NeighborRepository neighborRepository;

    public Neighbor findById(Long id) {
        return neighborRepository.findById(id).orElse(null);
    }

    public List<Neighbor> findAll() {
        return neighborRepository.findAll();
    }

    public Neighbor create(Neighbor like) {
        return neighborRepository.save(like);
    }

    public Neighbor update(Long id, Neighbor neighbor) {
        Neighbor newLike = Neighbor.builder()
                .id(id)
                .blog(neighbor.getBlog())
                .neighborBlog(neighbor.getNeighborBlog())
                .build();
        return neighborRepository.save(newLike);
    }

    public void delete(Long id) {
        neighborRepository.deleteById(id);
    }

    public List<Neighbor> findAllByBlogId(Long blogId) {
        return neighborRepository.findAllByBlogId(blogId);
    }

    public Neighbor findByBlogIdAndNeighborBlogId(Long blogId, Long neighborBlogId) {
        return neighborRepository.findByBlogIdAndNeighborBlogId(blogId, neighborBlogId).orElse(null);
    }
}
