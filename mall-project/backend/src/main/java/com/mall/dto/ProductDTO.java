package com.mall.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品数据传输对象
 */
@Data
public class ProductDTO {

    private Long id;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称不能超过200个字符")
    private String name;

    @Size(max = 2000, message = "商品描述不能超过2000个字符")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "原价不能为负数")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    private BigDecimal originalPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String category;

    @Size(max = 500, message = "图片URL不能超过500个字符")
    private String imageUrl;

    private Boolean isHot = false;

    private Integer status = 1;
}
