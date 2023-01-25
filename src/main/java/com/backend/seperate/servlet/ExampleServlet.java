package com.backend.seperate.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name ="ExampleServlet", urlPatterns = "/exampleServlet")
public class ExampleServlet extends HttpServlet{
  @Override
  public void init() throws ServletException {
    // TODO Auto-generated method stub
    System.out.println("-- MENU SERVLET init() START --");
    super.init();
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // TODO Auto-generated method stub
    System.out.println("-- MENU SERVLET service() LOADING --");
    super.service(req, resp);
  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

    System.out.println("-- MENU SERVLET destroy()  END --");
    
    super.destroy();
  }
}
