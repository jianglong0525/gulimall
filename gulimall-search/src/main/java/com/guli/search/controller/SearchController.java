package com.guli.search.controller;

import com.guli.search.service.MallSearchService;
import com.guli.search.vo.SearchParam;
import com.guli.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    /**
     * @param searchParam 封装页面传来的所有查询参数
     * @return
     */
    @GetMapping("/list.html")
    public String search(SearchParam searchParam, Model model, HttpServletRequest httpServletRequest){
        String queryString = httpServletRequest.getQueryString();
        searchParam.set_queryString(queryString);
        SearchResult searchResult = mallSearchService.search(searchParam);
        model.addAttribute("result",searchResult);
        return "list";
    }
}
