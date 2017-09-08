package com.sparke.web.spider;

import java.util.ArrayList;

import com.sparke.model.News;

public interface NewsRefreshInterface {
	public ArrayList<News> pullToRefresh();
}
