package redis_08.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis_08.pojo.Product;
import redis_08.service.ProductService;

import java.util.List;

/**
 * @projectName: Redis7_PNode
 * @package: redis_05.controller
 * @className: ProductController
 * @author: White
 * @description: TODO
 * @date: 2023/1/7 18:58
 * @version: 1.0
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 查询单个
     * @return
     */
    @GetMapping("/{id}")
    public Product getById(@PathVariable Integer id){
        Product product = productService.getById(id);
        return product;
    }

    /**
     * 查询全部
     * @return
     */
    @GetMapping
    public List<Product> getAll(){
        List<Product> products = productService.list();
        return products;
    }

    /**
     * 新增产品
     * @return
     */
    @PostMapping
    public boolean saveProduct(@RequestBody Product product){
        return productService.save(product);
    }

    /**
     * 修改产品
     * @param product
     * @return
     */
    @PutMapping
    public boolean updateProduct(@RequestBody Product product){
        return productService.updateById(product);
    }

    /**
     * 删除产品
     * @return
     */
    @DeleteMapping("/{id}")
    public boolean deleteProduct(@PathVariable Integer id){
        return productService.removeById(id);
    }

}
