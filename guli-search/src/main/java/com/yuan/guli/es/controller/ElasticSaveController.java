package com.yuan.guli.es.controller;

import com.yuan.common.exception.CodeEnume;
import com.yuan.common.to.es.SkuEsModel;
import com.yuan.common.utils.R;
import com.yuan.guli.es.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
@Slf4j
@RequestMapping("/search")
@RestController
public class ElasticSaveController {

    @Autowired
    ProductSaveService saveService;
        //上架商品
    @RequestMapping("/save")
    public R UpProduct(@RequestBody List<SkuEsModel> esModels) throws IOException {
        Boolean aBoolean = saveService.saveProduct(esModels);
        if(aBoolean == true){  //如果返回的结果为true，那么说明es保存失败
            log.error("save控制层出错，商品上架存在失败");
         return  R.error(CodeEnume.ES_EXCEPTION.getCode(),CodeEnume.ES_EXCEPTION.getMessage());
        }
        return  R.ok();
    }


}
