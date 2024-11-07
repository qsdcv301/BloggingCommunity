package taehyeon.com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taehyeon.com.blog.entity.Category;
import taehyeon.com.blog.repository.CategoryRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findById(Long id){
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Category create(Category category){
        return categoryRepository.save(category);
    }

    public Category update(Long id,Category category){
        Category newCategory = findById(id);
        newCategory.Builder(newCategory.getId(), category.getBlogId(), category.getName());
        return categoryRepository.save(newCategory);
    }

    public void delete(Long id){
        categoryRepository.deleteById(id);
    }

    public Integer countByBlogId(Long blogId){
        return categoryRepository.countByBlogId(blogId);
    }

}
