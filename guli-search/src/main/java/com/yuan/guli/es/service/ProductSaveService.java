package com.yuan.guli.es.service;


import com.yuan.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
  Boolean saveProduct(List<SkuEsModel> esModels) throws IOException;
}
