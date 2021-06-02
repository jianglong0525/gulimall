package com.guli.search.service;

import com.guli.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;


public interface ProduceUpService {

    boolean saveSearch(List<SkuEsModel> skuEsModel) throws IOException;
}
