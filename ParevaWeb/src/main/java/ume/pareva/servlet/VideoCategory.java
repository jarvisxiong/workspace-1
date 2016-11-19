package ume.pareva.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import ume.pareva.pojo.CategoryPojo;

public class VideoCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PrintWriter out = null;

	public VideoCategory() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = getServletContext();
		List activeCategories = (List) request.getAttribute("activeCategories");
		Map categoryMap = (HashMap) request.getAttribute("categoryMap");
		out = response.getWriter();
		if (categoryMap != null) {
			out.print("<ul class=\"mktree\" id=\"myTree\">");
			try {
				renderCategory(categoryMap, activeCategories);
			} catch (Exception e) {
			}

			out.print("</ul>");

		}
	}

	private void renderCategory(Map categoryMap, java.util.List activeCategories)
			throws IOException, JspException {
		Iterator it = categoryMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			CategoryPojo category = (CategoryPojo) categoryMap.get(key);
			if (category.getSubCategoryMap() != null) {
				out.print("<li>");
				out.print(category.getCategoryName());
				out.print("<ul>");
				renderCategory(category.getSubCategoryMap(), activeCategories);
				out.print("</ul>");
				out.print("</li>");
			} else {
				out.print("<li>");
				boolean matched = false;
				for (int j = 0; j < activeCategories.size(); j++) {

					if (category.getCategoryId()
							.equals(activeCategories.get(j))) {
						matched = true;
						break;
					}
				}
				if (matched)
					out.print("<input type=\"checkbox\" value=\""
							+ category.getCategoryId() + "\" name=\""
							+ category.getCategoryId() + "\" checked>"
							+ category.getCategoryName());
				else
					out.print("<input type=\"checkbox\" value=\""
							+ category.getCategoryId() + "\" name=\""
							+ category.getCategoryId() + "\">"
							+ category.getCategoryName());
				out.print("</li>");
			}
		}
	}

}
