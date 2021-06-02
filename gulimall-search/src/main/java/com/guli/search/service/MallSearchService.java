package com.guli.search.service;

import com.guli.search.vo.SearchParam;
import com.guli.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam searchParam);
}
