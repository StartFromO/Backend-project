package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    int insert(Product product);

    int batchInsert(@Param("list") List<Product> list);

    int deleteById(Long id);

    int deleteByIds(@Param("list") List<Long> ids);

    int updateById(Product product);

    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    int addSales(@Param("id") Long id, @Param("quantity") Integer quantity);

    Product selectById(Long id);

    List<Product> selectByIds(@Param("list") List<Long> ids);

    List<Product> selectAll();


    // 条件查询
    List<Product> selectByCondition(@Param("name") String name,
                                    @Param("category") String category,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    @Param("isHot") Integer isHot,
                                    @Param("status") Integer status,
                                    @Param("minStock") Integer minStock,
                                    @Param("sortField") String sortField,
                                    @Param("sortOrder") String sortOrder);

    List<Product> selectHotProducts(@Param("limit") Integer limit);

    List<Product> selectByCategory(@Param("category") String category);

}
