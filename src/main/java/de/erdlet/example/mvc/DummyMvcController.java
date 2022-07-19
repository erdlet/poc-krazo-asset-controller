package de.erdlet.example.mvc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.mvc.Controller;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("")
@RequestScoped
@Controller
public class DummyMvcController {
  
  @GET
  public String index() {
    return "index.jsp";
  }
}
