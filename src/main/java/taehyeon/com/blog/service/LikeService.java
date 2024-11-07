package taehyeon.com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taehyeon.com.blog.entity.Like;
import taehyeon.com.blog.repository.LikeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public Like findById(Long id) {
        return likeRepository.findById(id).orElse(null);
    }

    public List<Like> findAll() {
        return likeRepository.findAll();
    }

    public Like create(Like like) {
        return likeRepository.save(like);
    }

    public Like update(Long id, Like like) {
        Like newLike = findById(id);
        newLike.Builder(newLike.getId(), like.getPostId(), like.getUserId());
        return likeRepository.save(newLike);
    }

    public void delete(Long id) {
        likeRepository.deleteById(id);
    }

}
