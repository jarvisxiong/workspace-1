package ume.pareva.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ume.pareva.template.FileLoader;
import ume.pareva.template.TemplateLoader;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;

/**
 * Servlet implementation class TemplateLoader
 */

public class LoadTemplate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadTemplate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	public void init(ServletConfig config) throws ServletException {		 
		super.init(config);
		TemplateLoader loader = new TemplateLoader(this.getServletContext());
		loader.setPrefix("templates/uktemplate");
		loader.setSuffix(".html");
		PebbleEngine engine = new PebbleEngine(loader);
		EscaperExtension escaper = engine.getExtension(EscaperExtension.class);
		escaper.setAutoEscaping(false);
		this.getServletContext().setAttribute("engine",engine);
		
		TemplateLoader mexico_template_loader = new TemplateLoader(this.getServletContext());
		mexico_template_loader.setPrefix("templates/mexicotemplate");
		mexico_template_loader.setSuffix(".html");
		PebbleEngine mexico_engine = new PebbleEngine(mexico_template_loader);
		EscaperExtension mexico_escaper = mexico_engine.getExtension(EscaperExtension.class);
		mexico_escaper.setAutoEscaping(false);
		this.getServletContext().setAttribute("mexico_engine",mexico_engine);
		
		FileLoader za_template_loader = new FileLoader();
		za_template_loader.setPrefix("/var/www/lib/templates");
		za_template_loader.setSuffix(".html");
		PebbleEngine za_engine = new PebbleEngine(za_template_loader);
		EscaperExtension za_escaper = za_engine.getExtension(EscaperExtension.class);
		za_escaper.setAutoEscaping(false);
		this.getServletContext().setAttribute("za_engine",za_engine);
		
		
		
	}   

}
